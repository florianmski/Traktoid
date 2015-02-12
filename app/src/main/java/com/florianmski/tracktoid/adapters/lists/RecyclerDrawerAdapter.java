package com.florianmski.tracktoid.adapters.lists;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerDrawerAdapter extends RecyclerAdapter<RecyclerDrawerAdapter.DrawerItem, RecyclerAdapter.ViewHolder>
{
    private final int ITEM = 0, SEPARATOR = 1, SUBHEADER = 2;

    private int checkedPosition = -1;

    private SparseArray<DrawerItem> items = new SparseArray<>();
    private SparseArray<DrawerSeparator> separators = new SparseArray<>();
    private SparseArray<DrawerSubheader> subheaders = new SparseArray<>();

    public RecyclerDrawerAdapter(Context context, OnItemClickListener listener)
    {
        super(context, new ArrayList<RecyclerDrawerAdapter.DrawerItem>(), listener);
    }

    public RecyclerDrawerAdapter addItem(int id, String title, Drawable icon)
    {
        items.put(getItemCount(), new DrawerItem(id, title, icon.mutate()));
        return this;
    }

    public RecyclerDrawerAdapter addItem(int id, String title, int iconResId)
    {
        addItem(id, title, context.getResources().getDrawable(iconResId));
        return this;
    }

    public RecyclerDrawerAdapter addSeparator()
    {
        separators.put(getItemCount(), new DrawerSeparator());
        return this;
    }

    public RecyclerDrawerAdapter addSubheader(String title)
    {
        subheaders.put(getItemCount(), new DrawerSubheader(title));
        return this;
    }

    @Override
    public void refresh(List<RecyclerDrawerAdapter.DrawerItem> data)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset()
    {
        items.clear();
        separators.clear();
        subheaders.clear();
        super.reset();
    }

    public void setCheckedPosition(int position)
    {
        this.checkedPosition = position;
        notifyDataSetChanged();
    }

    public int getCheckedPosition()
    {
        return checkedPosition;
    }

    public int getItemPosition(int id)
    {
        int i = 0;
        for(DrawerItem item = items.valueAt(i); i < items.size() ; i++)
        {
            if(item.id == id)
                return items.keyAt(i);
        }

        return -1;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerAdapter.ViewHolder vh;

        switch(viewType)
        {
            case SEPARATOR:
            default:
                View v = LayoutInflater.from(context).inflate(R.layout.list_item_drawer_separator, parent, false);
                vh = new SeparatorViewHolder(v);
                break;
            case ITEM:
                v = LayoutInflater.from(context).inflate(R.layout.list_item_drawer, parent, false);
                vh = new ItemViewHolder(v, listener);
                break;
            case SUBHEADER:
                v = LayoutInflater.from(context).inflate(R.layout.list_item_drawer_subheader, parent, false);
                vh = new SubheaderViewHolder(v);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        if(getItemViewType(position) == SEPARATOR)
        {
            SeparatorViewHolder castedHolder = (SeparatorViewHolder)holder;
            DrawerSeparator separator = separators.get(position);
        }
        else if(getItemViewType(position) == ITEM)
        {
            boolean checked = checkedPosition == position;
            ItemViewHolder castedHolder = (ItemViewHolder)holder;
            DrawerItem item = items.get(position);
            castedHolder.tvTitle.setText(item.title);
            castedHolder.itemView.setActivated(checked);

            if(checked)
            {
                int color = TraktoidTheme.DEFAULT.getColorDark(context);
                castedHolder.tvTitle.setTextColor(color);
                Drawable icon = item.icon;
                icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                castedHolder.ivIcon.setImageDrawable(icon);
            }
            else
            {
                castedHolder.tvTitle.setTextColor(context.getResources().getColor(android.R.color.primary_text_dark));
                item.icon.clearColorFilter();
                castedHolder.ivIcon.setImageDrawable(item.icon);
            }
        }
        else
        {
            SubheaderViewHolder castedHolder = (SubheaderViewHolder)holder;
            DrawerSubheader subheader = subheaders.get(position);
            castedHolder.tvTitle.setText(subheader.title);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        if(items.get(position) != null)
            return ITEM;
        else if(separators.get(position) != null)
            return SEPARATOR;
        return SUBHEADER;
    }

    @Override
    public int getItemCount()
    {
        return separators.size() + items.size() + subheaders.size();
    }

    @Override
    public DrawerItem getItem2(int position)
    {
        return items.get(position);
    }

    protected static class SeparatorViewHolder extends RecyclerAdapter.ViewHolder
    {
        public SeparatorViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    protected static class ItemViewHolder extends RecyclerAdapter.ViewHolder
    {
        private TextView tvTitle;
        private ImageView ivIcon;

        public ItemViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView, listener);

            tvTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            ivIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
        }
    }

    protected static class SubheaderViewHolder extends RecyclerAdapter.ViewHolder
    {
        private TextView tvTitle;

        public SubheaderViewHolder(View itemView)
        {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        }
    }

    public class DrawerItem
    {
        public int id;
        public String title;
        public Drawable icon;

        public DrawerItem(int id, String title, Drawable icon)
        {
            this.id = id;
            this.title = title;
            this.icon = icon;
        }
    }

    protected class DrawerSeparator
    {
        public DrawerSeparator() {}
    }

    protected class DrawerSubheader
    {
        public String title;

        public DrawerSubheader(String title)
        {
            this.title = title;
        }
    }
}
