package com.florianmski.tracktoid.adapters.lists;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.Utils;
import com.florianmski.tracktoid.adapters.RootAdapter;
import com.florianmski.tracktoid.image.TraktImage;
import com.florianmski.tracktoid.ui.fragments.SearchFragment;
import com.florianmski.tracktoid.widgets.BadgesView;
import com.florianmski.traktoid.TraktoidInterface;
import com.jakewharton.trakt.entities.Movie;
import com.jakewharton.trakt.entities.TvEntity;
import com.jakewharton.trakt.entities.TvShow;

public abstract class ListSearchAdapter<T> extends RootAdapter<T>
{
	@SuppressWarnings("unchecked")
	public static ListSearchAdapter<?> createAdapter(Context context, List<?> items, int searchType)
	{
		switch(searchType)
		{
		case SearchFragment.SHOWS:
			return new ListSearchShowsAdapter(context, (List<TvShow>) items);
		case SearchFragment.MOVIES:
			return new ListSearchMoviesAdapter(context, (List<Movie>) items);
//		case SearchFragment.EPISODES:
//			return new ListSearchEpisodesAdapter(context, (List<TvEntity>) items);
//		case SearchFragment.PEOPLES:
//			return tm.searchService().people(query).fire();
//		case SearchFragment.USERS:
//			return tm.searchService().users(query).fire();
		}
		return null;
	}
	
	public ListSearchAdapter(Context context, List<T> items)
	{
		super(context, items);
	}

	protected abstract String getText(T item);
	protected abstract String getBanner(T item);
	protected abstract boolean isTraktItem();
	
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@Override
	public View doGetView(final int position, View convertView, ViewGroup parent) 
	{
		final ViewHolder holder;

		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_serie, parent, false);
			holder = new ViewHolder();

			holder.bvBanner = (BadgesView)convertView.findViewById(R.id.badgesLayoutBanner);
			holder.ivBanner = (ImageView)convertView.findViewById(R.id.imageViewBanner);
			holder.tvSeason = (TextView)convertView.findViewById(R.id.textViewShow);

			int height = (int) (parent.getWidth()*TraktImage.RATIO_BANNER);
			holder.bvBanner.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, height));
			holder.ivBanner.setScaleType(ScaleType.FIT_CENTER);

			convertView.setTag(holder);
		} 
		else
			holder = (ViewHolder) convertView.getTag();

//		TvShow show = getItem(position);

		holder.bvBanner.initialize();
		
//		TraktImage i = TraktImage.getBanner(show);
		
		T item = getItem(position);
		String url = getBanner(item);
		String text = getText(item);
		
		AQuery aq = listAq.recycle(convertView);
		if(aq.shouldDelay(convertView, parent, url, 0))
			setPlaceholder(holder.ivBanner);
		else
		{
			removePlaceholder(holder.ivBanner, ScaleType.CENTER_CROP);
			
			if(isTraktItem())
				holder.bvBanner.setTraktItem((TraktoidInterface<T>)item);
			aq.id(holder.ivBanner).image(url, true, false, 0, 0, null, android.R.anim.fade_in);
		}

		holder.tvSeason.setText(text);

		return convertView;
	}

	private static class ViewHolder 
	{
		@SuppressWarnings("rawtypes")
		private BadgesView bvBanner;
		private ImageView ivBanner;
		private TextView tvSeason;
	}
	
	public static final class ListSearchShowsAdapter extends ListSearchAdapter<TvShow>
	{
		public ListSearchShowsAdapter(Context context, List<TvShow> items) 
		{
			super(context, items);
		}

		@Override
		protected String getText(TvShow item) 
		{
			return item.title;
		}

		@Override
		protected String getBanner(TvShow item) 
		{
			return TraktImage.getBanner(item).getUrl();
		}

		@Override
		protected boolean isTraktItem() 
		{
			return true;
		}
	}
	
	public static final class ListSearchMoviesAdapter extends ListSearchAdapter<Movie>
	{
		public ListSearchMoviesAdapter(Context context, List<Movie> items) 
		{
			super(context, items);
		}

		@Override
		protected String getText(Movie item) 
		{
			return item.title;
		}

		@Override
		protected String getBanner(Movie item) 
		{
			return TraktImage.getFanart(item).getUrl();
		}

		@Override
		protected boolean isTraktItem() 
		{
			return true;
		}
	}
	
	public static final class ListSearchEpisodesAdapter extends ListSearchAdapter<TvEntity>
	{
		public ListSearchEpisodesAdapter(Context context, List<TvEntity> items) 
		{
			super(context, items);
		}

		@Override
		protected String getText(TvEntity item) 
		{
			return item.show.title + " " + Utils.addZero(item.episode.season) + "x" + Utils.addZero(item.episode.number);
		}

		@Override
		protected String getBanner(TvEntity item) 
		{
			return TraktImage.getScreen(item.episode).getUrl();
		}

		@Override
		protected boolean isTraktItem() 
		{
			return true;
		}
	}
}
