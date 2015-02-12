package com.florianmski.tracktoid.ui.fragments.comments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.rx.observables.TraktObservable;
import com.florianmski.tracktoid.TraktoidConstants;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.adapters.lists.RecyclerCommentsAdapter;
import com.florianmski.tracktoid.ui.fragments.base.list.ItemRecyclerViewFragment;
import com.florianmski.tracktoid.ui.widgets.DividerItemDecoration;
import com.uwetrottmann.trakt.v2.entities.Comment;

import java.util.List;

import rx.Observable;

public abstract class CommentsFragment extends ItemRecyclerViewFragment<Comment> implements RecyclerAdapter.OnItemClickListener
{
    protected String id;

    protected EditText edtShout;
    protected ImageView btnSend;
    protected ToggleButton tbSpoiler;

    public abstract List<Comment> getComments();

    protected static Bundle getBundle(String id)
    {
        Bundle args = new Bundle();
        args.putString(TraktoidConstants.BUNDLE_ID, id);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        id = getArguments().getString(TraktoidConstants.BUNDLE_ID);
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager()
    {
        return new StaggeredGridLayoutManager(getResources().getInteger(R.integer.grid_fanart_columns), StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getGroupView().addItemDecoration(new DividerItemDecoration(getActivity(), null));

        btnSend.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //empty shout
                if (edtShout.getText().toString().trim().equals(""))
                    Toast.makeText(getActivity(), "Empty shout!", Toast.LENGTH_LONG).show();
                else
                {

                }
            }
        });
    }

    @Override
    protected View getCustomLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState)
    {
        super.onViewCreated(v, savedInstanceState);

        edtShout = (EditText)v.findViewById(R.id.editTextShout);
        btnSend = (ImageView)v.findViewById(R.id.buttonSend);
        tbSpoiler = (ToggleButton)v.findViewById(R.id.toggleButtonSpoiler);
    }

    @Override
    protected Observable<List<Comment>> createObservable()
    {
        return Observable.create(new TraktObservable<List<Comment>>()
        {
            @Override
            public List<Comment> fire()
            {
                return getComments();
            }
        });
    }

    @Override
    protected RecyclerAdapter<Comment, ?> createAdapter(List<Comment> items)
    {
        return new RecyclerCommentsAdapter(getActivity(), items, getTheme(), this);
    }

    @Override
    public void onItemClick(View v, int position)
    {

    }

    @Override
    protected boolean hasFixedSize()
    {
        return false;
    }
}
