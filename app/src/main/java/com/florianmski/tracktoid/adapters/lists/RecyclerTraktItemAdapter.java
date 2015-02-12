package com.florianmski.tracktoid.adapters.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.data.TraktBase;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.ui.widgets.FlagsView;
import com.florianmski.tracktoid.ui.widgets.Placeholder;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;

import java.util.List;
import java.util.Random;

public class RecyclerTraktItemAdapter<E extends TraktoidItem> extends RecyclerAdapter<E, RecyclerTraktItemAdapter.TraktItemViewHolder>
{
    private Type imageType = Type.POSTER;
    private boolean titleVisible = true;
    private Random r = new Random();
    private TraktoidTheme theme;

    private Placeholder placeholder;

    public RecyclerTraktItemAdapter(Context context, List<E> list, TraktoidTheme theme, OnItemClickListener listener)
    {
        super(context, list, listener);
        setHasStableIds(true);
        this.theme = theme;
        placeholder = new Placeholder(context, theme);
    }

    public RecyclerTraktItemAdapter<E> imageType(Type imageType)
    {
        this.imageType = imageType;
        return this;
    }

    public RecyclerTraktItemAdapter<E> titleVisible(boolean titleVisible)
    {
        this.titleVisible = titleVisible;
        return this;
    }

    @Override
    public TraktItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_trakt_item, parent, false);
        return new TraktItemViewHolder(v, imageType, listener);
    }

    @Override
    public void onBindViewHolder(final TraktItemViewHolder holder, int position)
    {
        final TraktBase traktBase = getItem2(position).getTraktBase();

        //        Picasso.with(context).load(ImagesTest.getUrl(holder.iv.getType(), traktBase.images)).placeholder(placeholder).into(holder.iv);

        // We need to do that because before first layout pass this will return a size of 0x0
//        if(holder.iv.getWidth() == 0 && holder.iv.getHeight() == 0)
//            holder.iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
//            {
//                @Override
//                public void onGlobalLayout()
//                {
//                    ImagesTest.load(context, holder.iv, traktBase.images).placeholder(placeholder).into(holder.iv);
//                    holder.iv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });

        ImagesTest.load(context, holder.iv, traktBase.images).placeholder(placeholder.getDrawable()).into(holder.iv);

        holder.fv.init(new FlagsView.Flags.Builder(traktBase).displayTitle(titleVisible).theme(theme).build());
    }

    @Override
    public long getItemId(int position)
    {
        return getItem2(position).getIds().trakt;
    }

    public static class TraktItemViewHolder extends RecyclerAdapter.ViewHolder
    {
        public FlagsView fv;
        public TraktImageView iv;

        public TraktItemViewHolder(View itemView, Type imageType, OnItemClickListener listener)
        {
            super(itemView, listener);

            fv = (FlagsView) itemView.findViewById(R.id.flagsView);
            iv = (TraktImageView) itemView.findViewById(R.id.imageView);
            iv.setType(imageType);
        }
    }
}
