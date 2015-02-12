package com.florianmski.tracktoid.ui.fragments.user;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.trakt.TraktManager;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemScrollViewFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.uwetrottmann.trakt.v2.entities.User;
import com.uwetrottmann.trakt.v2.enums.Extended;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import rx.Observable;

public class ProfileFragment extends ItemScrollViewFragment<User>
{
    private String userId;

    public ProfileFragment() {}

    public static ProfileFragment newInstance(String userId)
    {
        ProfileFragment f = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_ID, userId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        userId = getArguments().getString(TraktoidConstants.BUNDLE_ID);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return TraktoidTheme.DEFAULT;
    }

    @Override
    protected int getContentLayoutId()
    {
        return R.layout.fragment_profile;
    }

    @Override
    protected Observable<User> createObservable()
    {
        return Observable.create(new TraktObservable<User>()
        {
            @Override
            public User fire() throws OAuthUnauthorizedException
            {
                return TraktManager.getInstance().users().profile(userId, Extended.FULLIMAGES);
            }
        });
    }

    @Override
    protected void refreshView(User data)
    {
        getActionBar().setTitle(data.username);
        if(data.name != null && !data.name.isEmpty())
            getActionBar().setSubtitle(data.name);

        Picasso.with(getActivity()).load(data.images.avatar.full).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom)
            {
                // TODO
//                getActionBar().setDisplayShowHomeEnabled(true);
//                getActionBar().setDisplayUseLogoEnabled(true);
//                getActionBar().setIcon(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {}

            @Override
            public void onPrepareLoad(Drawable drawable) {}
        });
    }
}
