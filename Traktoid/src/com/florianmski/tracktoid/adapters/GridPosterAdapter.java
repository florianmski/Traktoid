package com.florianmski.tracktoid.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SectionIndexer;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.ListCheckerManager;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.widgets.OverlaysView;
import com.florianmski.traktoid.TraktoidInterface;

public class GridPosterAdapter<T extends TraktoidInterface<T>> extends RootAdapter<T> implements SectionIndexer
{
	public static final int FILTER_ALL = 0;
	public static final int FILTER_UNWATCHED = 1;

	protected List<T> filteredItems = new ArrayList<T>();
	protected int height;
	protected int currentFilter = 0;
	protected Handler h = new Handler();
	private ListCheckerManager<T> lcm;
	
	private Map<String, Integer> alphaIndexer = new HashMap<String, Integer>();
	private String[] sections;

	public GridPosterAdapter(Activity context, List<T> items, int height, ListCheckerManager<T> lcm)
	{
		super(context, items);
		this.height = height;
		this.lcm = lcm;
	}

	@Override
	public void clear() 
	{
		items.clear();
		filteredItems.clear();
		currentFilter = 0;
		notifyDataSetChanged();
	}

	public void setHeight(int height)
	{
		this.height = height;
		notifyDataSetChanged();
	}

	public void setFilter(int filter)
	{
		currentFilter = filter;
		switch(filter)
		{
		case FILTER_ALL :
			filteredItems.clear();
			filteredItems.addAll(items);
			break;
		case FILTER_UNWATCHED :
			filteredItems.clear();
			for(T item : items)
			{
				if(!item.isWatched())
					filteredItems.add(item);
			}
			break;
		}
		this.notifyDataSetChanged();
	}

	public void updateItem(T item)
	{
		int index = Collections.binarySearch(items, item);
		if(index < 0 || index >= items.size())
			items.add(item);
		else
			items.set(index, item);

		Collections.sort(items);

		setFilter(currentFilter);
	}

	public void updateItems(List<T> items)
	{
		boolean dataChanged = false;

		for(T item : items)
		{
			int index = Collections.binarySearch(this.items, item);
			if(index < 0 || index >= this.items.size())
				this.items.add(item);
			else
				this.items.set(index, item);

			dataChanged = true;
		}

		if(dataChanged)
		{
			Collections.sort(this.items);
			setFilter(currentFilter);
			createAlphaIndexer();
		}
	}

	@Override
	public void remove(T item)
	{
		int index = Collections.binarySearch(items, item);
		if(index >= 0 && index < items.size())
		{
			items.remove(index);
			setFilter(currentFilter);
			createAlphaIndexer();
		}
	}

	public void remove(List<T> items)
	{
		boolean dataChanged = false;

		for(T item : items)
		{
			int index = Collections.binarySearch(this.items, item);
			if(index >= 0 && index < this.items.size())
			{
				this.items.remove(index);
				dataChanged = true;
			}
		}

		if(dataChanged)
		{
			setFilter(currentFilter);
			createAlphaIndexer();
		}
	}

	@Override
	public void refreshItems(List<T> items)
	{
		this.items = items;
		setFilter(currentFilter);
		createAlphaIndexer();
	}

	@Override
	public int getCount() 
	{
		return filteredItems.size();
	}

	@Override
	public T getItem(int position) 
	{
		return filteredItems.get(position);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public View doGetView(int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder<T> holder;

		if (convertView == null)
		{
			holder = new ViewHolder<T>();

			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_show, null, false);

			holder.ivPoster = (ImageView) convertView.findViewById(R.id.imageViewPoster);
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);

			holder.ov = (OverlaysView<T>) convertView.findViewById(R.id.badgesLayout);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder<T>) convertView.getTag();

		GridView.LayoutParams paramsRl = new GridView.LayoutParams(LayoutParams.FILL_PARENT, height);
		holder.ov.setLayoutParams(paramsRl);

		final T item = getItem(position);

		holder.ov.initialize();
		holder.ivPoster.setImageBitmap(null);

		TraktImage i = TraktImage.getPoster(item);
		AQuery aq = listAq.recycle(convertView);

		if(aq.shouldDelay(convertView, parent, i.getUrl(), 0))
			setPlaceholder(holder.ivPoster);
		else
		{
			holder.ivPoster.setScaleType(ScaleType.CENTER_CROP);
			aq.id(holder.ivPoster).image(i.getUrl(), true, true);

			holder.ov.setTraktItem(item);
		}

		return lcm.checkView(item, convertView);
	}
	
	public void createAlphaIndexer()
	{
		if(getCount() <= 0)
			return;

		alphaIndexer.clear();

		String previousFirstLetter = items.get(0).getTitle().substring(0, 1).toUpperCase();
		alphaIndexer.put(previousFirstLetter, 0);
		for (int i = 0; i < items.size(); i++)
		{
			String actualFirstLetter = items.get(i).getTitle().substring(0, 1).toUpperCase();
			if (!actualFirstLetter.equals(previousFirstLetter))
			{
				alphaIndexer.put(actualFirstLetter, i);
				previousFirstLetter = actualFirstLetter;
			}
		}

		Set<String> keys = alphaIndexer.keySet();
		Iterator<String> it = keys.iterator();
		ArrayList<String> keyList = new ArrayList<String>();
		while (it.hasNext())
		{
			String key = it.next();
			keyList.add(key);
		}

		Collections.sort(keyList);

		sections = new String[keyList.size()];
		keyList.toArray(sections);
	}
	
	@Override
	public int getPositionForSection(int section)
	{
		String letter = "";
		if(section < sections.length)
			letter = sections[section];
		return alphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(int position)
	{
		return 0;
	}

	@Override
	public Object[] getSections()
	{
		return sections;
	}

	private static class ViewHolder<T extends TraktoidInterface<T>> 
	{
		private ImageView ivPoster;
		private OverlaysView<T> ov;
	}
}