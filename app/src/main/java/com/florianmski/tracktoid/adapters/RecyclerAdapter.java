package com.florianmski.tracktoid.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerAdapter<E, VH extends RecyclerAdapter.ViewHolder> extends RecyclerView.Adapter<VH> implements AdapterInterface<E>
{
    protected Context context;
    protected List<E> data;
    protected OnItemClickListener listener;

    public RecyclerAdapter(Context context, List<E> data, OnItemClickListener listener)
    {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    public RecyclerAdapter(Context context, List<E> data)
    {
        this(context, data, null);
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    @Override
    public void refresh(List<E> data)
    {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void reset()
    {
        this.data = new ArrayList<E>();
        notifyDataSetChanged();
    }

    @Override
    public E getItem2(int position)
    {
        return data.get(position);
    }

    public interface OnItemClickListener
    {
        public void onItemClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(final View itemView, final OnItemClickListener listener)
        {
            super(itemView);

            if(listener != null)
                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        listener.onItemClick(itemView, getPosition());
                    }
                });
        }

        public ViewHolder(final View itemView)
        {
            this(itemView, null);
        }
    }
}
