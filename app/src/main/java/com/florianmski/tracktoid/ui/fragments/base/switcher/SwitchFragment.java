package com.florianmski.tracktoid.ui.fragments.base.switcher;

import android.animation.Animator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.ui.fragments.BaseFragment;
import com.getbase.floatingactionbutton.FloatingActionButton2;

public abstract class SwitchFragment extends BaseFragment implements View.OnClickListener
{
    private final static String BUNDLE_INDEX = "index";
    private final static int FRAGMENT_SWITCH_RES_ID = R.id.fragment_switch;

    protected FloatingActionButton2 fab;
    protected int index = getDefaultIndex();

    public abstract Fragment getFragment(int index);
    public abstract int getCount();
    public abstract int getDefaultIndex();

    public SwitchFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_INDEX, index);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        fab.setOnClickListener(this);

        if(savedInstanceState != null)
            index = savedInstanceState.getInt(BUNDLE_INDEX);

        replaceFragment(null, getFragment(index));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_switch, container, false);
        fab = (FloatingActionButton2) v.findViewById(R.id.fab);
        return v;
    }

    protected void replaceFragment(Fragment previousFragment, Fragment nextFragment)
    {
        //        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        //
        //        // check if we've already added the fragment to the activity, if not do it
        //        String tag = String.valueOf(index);
        //        if(getChildFragmentManager().findFragmentByTag(tag) == null)
        //            transaction.add(R.id.fragment_switch, nextFragment, tag);
        //
        //        transaction.attach(nextFragment);
        //
        //        // if previousFragment != null it means we can detach it
        //        if(previousFragment != null)
        //        {
        //            transaction.detach(previousFragment);
        //        }
        //
        //        transaction.commit();

        getChildFragmentManager().beginTransaction().replace(FRAGMENT_SWITCH_RES_ID, nextFragment).commit();
    }

    protected void next()
    {
        final Fragment previousFragment = getFragment(index);
        index = ++index % getCount();
        final Fragment nextFragment = getFragment(index);
        replaceFragment(previousFragment, nextFragment);
    }

    public FloatingActionButton2 getFAB()
    {
        return fab;
    }

    @Override
    public void onClick(View v)
    {
        next();
    }

    @Override
    public void onInsetsChanged(Rect insets)
    {
        super.onInsetsChanged(insets);

        // put the fab above the nav bar
        ((FrameLayout.LayoutParams)fab.getLayoutParams()).setMargins(0, 0, 0, insets.bottom);
    }

    @Override
    public TraktoidTheme getTheme()
    {
        return ((BaseFragment)getFragment(index)).getTheme();
    }

    private void revealEffect(Animator.AnimatorListener listener)
    {
        // get the center for the clipping circle
        int cx = (fab.getLeft() + fab.getRight()) / 2;
        int cy = (fab.getTop() + fab.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(getView().getWidth(), getView().getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(getView(), cx, cy, 0, finalRadius);
        anim.addListener(listener);
        anim.start();
    }
}
