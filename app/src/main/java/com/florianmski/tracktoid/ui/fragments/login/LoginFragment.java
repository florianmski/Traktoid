package com.florianmski.tracktoid.ui.fragments.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.florianmski.tracktoid.BuildConfig;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidPrefs;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.provider.TraktoidProvider;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.services.sync.SyncAdapter;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.ui.activities.HomeActivity;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemWebViewFragment;
import com.uwetrottmann.trakt.v2.TraktV2;
import com.uwetrottmann.trakt.v2.entities.User;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.math.BigInteger;
import java.security.SecureRandom;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class LoginFragment extends ItemWebViewFragment<User>
{
    // TODO would be cool to extract the oauth flow in a base oauth fragment

    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String authCode;
    private String state;

    private Subscription subscriptionUser = Subscriptions.empty();

    public static LoginFragment newInstance(Bundle args)
    {
        LoginFragment f = new LoginFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRefreshOnStart(false);

        retryListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // if we already have the token
                // if not, start from scratch
                if(TraktoidPrefs.INSTANCE.isUserLoggedIn())
                    getUser();
                else
                    loadUrlForAuthorization();
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        clientId = getString(R.string.trakt_client_id);
        clientSecret = getString(R.string.trakt_client_secret);
        redirectUri = getString(R.string.trakt_redirect_uri);

        getGroupView().setBackgroundColor(Color.TRANSPARENT);
        getGroupView().getSettings().setJavaScriptEnabled(true);
        getGroupView().getSettings().setBuiltInZoomControls(true);
        CookieManager.getInstance().removeAllCookie();
        getToolbar().setVisibility(View.GONE);

        loadUrlForAuthorization();
    }

    protected void loadUrlForAuthorization()
    {
        state = new BigInteger(130, new SecureRandom()).toString(32);

        try
        {
            OAuthClientRequest request = TraktV2.getAuthorizationRequest(clientId, redirectUri, state, null);
            getGroupView().loadUrl(request.getLocationUri());
        }
        catch (OAuthSystemException e)
        {
            e.printStackTrace();
        }
    }

    protected Observable<User> createUserObservable()
    {
        return Observable.create(new TraktObservable<User>()
        {
            @Override
            public User fire() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().users().profile("me", Extended.DEFAULT_MIN);
            }
        }).doOnNext(new Action1<User>()
        {
            @Override
            public void call(User user)
            {
                String username = user.username;

                TraktoidPrefs.INSTANCE.putUsername(username);
                if (!BuildConfig.DEBUG)
                    Crashlytics.setUserName(username);

                // get the account or create one
                AccountManager manager = (AccountManager) getActivity().getSystemService(Context.ACCOUNT_SERVICE);
                Account[] accounts = manager.getAccountsByType(BuildConfig.APPLICATION_ID);
                Account account;
                if (accounts.length > 0)
                    account = accounts[0];
                else
                {
                    account = new Account(SyncAdapter.ACCOUNT_NAME, BuildConfig.APPLICATION_ID);
                    manager.addAccountExplicitly(account, null, null);
                }

                ContentResolver.setIsSyncable(account, TraktoidProvider.AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, TraktoidProvider.AUTHORITY, true);
                ContentResolver.addPeriodicSync(account, TraktoidProvider.AUTHORITY, new Bundle(), 24 * 60 * 60);
            }
        }).doOnCompleted(new Action0()
        {
            @Override
            public void call()
            {
                HomeActivity.launch(getActivity());
                getActivity().finish();
            }
        });
    }

    protected void getUser()
    {
        subscribe(createUserObservable());
    }

    protected Observable<OAuthAccessTokenResponse> createTokenObservable()
    {
        return Observable.create(new Observable.OnSubscribe<OAuthAccessTokenResponse>()
        {
            @Override
            public void call(Subscriber<? super OAuthAccessTokenResponse> subscriber)
            {
                try
                {
                    OAuthAccessTokenResponse response = TraktV2.getAccessToken(clientId, clientSecret, redirectUri, authCode);
                    String accessToken = response.getAccessToken();

                    TraktoidPrefs.INSTANCE.putAccessToken(accessToken);
                    TraktManager.getInstance().setAccessToken(accessToken);

                    subscriber.onNext(response);
                }
                catch (OAuthSystemException | OAuthProblemException e)
                {
                    e.printStackTrace();
                    subscriber.onError(e);
                }

                subscriber.onCompleted();
            }
        });
    }

    protected void getToken()
    {
        Observable<User> observable = createTokenObservable().flatMap(new Func1<OAuthAccessTokenResponse, Observable<User>>()
        {
            @Override
            public Observable<User> call(OAuthAccessTokenResponse oAuthAccessTokenResponse)
            {
                return createUserObservable();
            }
        });

        subscribe(observable);
    }

    protected void subscribe(Observable<User> observable)
    {
        subscriptions.remove(subscriptionUser);
        subscriptionUser = AndroidObservable.bindFragment(this, observable.subscribeOn(Schedulers.io())).subscribe(this);
        subscriptions.add(subscriptionUser);
        showProgressBar(null);
    }

    @Override
    protected Observable<User> createObservable()
    {
        return Observable.empty();
    }

    @Override
    protected void refreshView(User data)
    {
        Toast.makeText(getActivity(), "Welcome " + data.username + "!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted()
    {
        super.onCompleted();
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.DEFAULT;
    }

    @Override
    protected RefreshWebViewClient getWebViewClient()
    {
        return new LoginWebViewClient();
    }

    public class LoginWebViewClient extends RefreshWebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url.startsWith(redirectUri))
            {
                Uri uri = Uri.parse(url);
                String requestState = uri.getQueryParameter(OAuth.OAUTH_STATE);

                if (state.equals(requestState))
                {
                    authCode = uri.getQueryParameter(OAuth.OAUTH_CODE);

                    getToken();

                    // don't go to redirectUri
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            int paddingTop = (int) (view.getPaddingTop() / view.getScale());
            int paddingBottom = view.getPaddingBottom();

            // tweak the height of the view
            //            view.loadUrl("javascript:(function() { " +
            //                    "document.body.style.paddingTop = '" + paddingTop  + "px'; " +
            //                    "document.getElementById('auth-form-wrapper').style.top=(parseInt(window.getComputedStyle(document.getElementById('auth-form-wrapper'), null).getPropertyValue('top').replace('px', '')) + " + paddingTop + ") + 'px'\n" +
            //                    //                    "document.getElementById('auth-bg-wrapper').style.height = ($('#auth-bg-wrapper').height() + " + paddingTop  + ") + 'px'; " +
            //                    "})()");
        }
    }
}
