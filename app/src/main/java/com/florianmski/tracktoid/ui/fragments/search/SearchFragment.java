package com.florianmski.tracktoid.ui.fragments.search;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.ui.fragments.base.trakt.TraktItemsFragment;
import com.uwetrottmann.trakt.v2.exceptions.OAuthUnauthorizedException;

import java.util.List;

import rx.Observable;

public abstract class SearchFragment<E extends TraktoidItem> extends TraktItemsFragment<E> implements SearchView.OnQueryTextListener
{
    private final static String BUNDLE_QUERY = "query";

    private SearchView searchView;
    private String query;

    public SearchFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(savedInstanceState == null)
            setRefreshOnStart(false);
        else
            query = savedInstanceState.getString(BUNDLE_QUERY);
    }

    protected abstract List<E> getSearch(String query);

    @Override
    protected Type getImageType()
    {
        return Type.FANART;
    }

    @Override
    protected Observable<List<E>> createObservable()
    {
        return Observable.create(new TraktObservable<List<E>>()
        {
            @Override
            public List<E> fire() throws OAuthUnauthorizedException
            {
                return getSearch(query);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        // create the search view
        searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setQueryHint("What are you looking for?");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        final MenuItem searchMenuItem = menu.add(Menu.NONE, R.id.action_bar_search, Menu.NONE, getString(android.R.string.search_go));
        searchMenuItem
                .setIcon(android.support.v7.appcompat.R.drawable.abc_ic_search_api_mtrl_alpha)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                searchView.setQuery(query, false);
            }
        });

        searchMenuItem.expandActionView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_QUERY, query);
    }

    @Override
    public boolean onQueryTextSubmit(String newText)
    {
        query = newText;
        refresh(true);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        return true;
    }
}
