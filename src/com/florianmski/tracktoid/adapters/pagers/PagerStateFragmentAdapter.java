package com.florianmski.tracktoid.adapters.pagers;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.florianmski.tracktoid.ui.fragments.pagers.items.PagerItemFragment;

public class PagerStateFragmentAdapter extends FragmentStatePagerAdapter
{
	private ArrayList<?> list;
	private PagerItemFragment fragment;
	
	public PagerStateFragmentAdapter(ArrayList<?> list, PagerItemFragment fragment) 
	{
		super(fragment.getSupportFragmentManager());
		this.list = list;
		this.fragment = fragment;
	}
	
	@Override
    public int getCount() 
	{
        return list.size();
    }

    @Override
    public Fragment getItem(int position) 
    {
        return fragment.newInstance(list.get(position));
    }

    public Object getObject(int position) 
    {
        return fragment.getObject();
    }
}
