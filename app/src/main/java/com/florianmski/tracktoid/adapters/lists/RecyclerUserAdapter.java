package com.florianmski.tracktoid.adapters.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;
import com.florianmski.tracktoid.ui.widgets.FlagsView;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.trakt.v2.entities.User;

import java.util.List;

public class RecyclerUserAdapter extends RecyclerAdapter<User, RecyclerUserAdapter.UsersViewHolder>
{
    public RecyclerUserAdapter(Context context, List<User> data, OnItemClickListener listener)
    {
        super(context, data, listener);
    }

    @Override
    public RecyclerUserAdapter.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_network, parent, false);
        return new UsersViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(RecyclerUserAdapter.UsersViewHolder holder, int position)
    {
        User user = getItem2(position);

        Picasso.with(context).load(ImagesTest.getUrl(Type.AVATAR, user.images)).into(holder.tivAvatar);
        holder.fv.init(new FlagsView.Flags.Builder().title(user.username).build());
    }

    public static class UsersViewHolder extends RecyclerAdapter.ViewHolder
    {
        private TraktImageView tivAvatar;
        private FlagsView fv;

        public UsersViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView, listener);

            tivAvatar = (TraktImageView) itemView.findViewById(R.id.imageViewAvatar);
            fv = (FlagsView) itemView.findViewById(R.id.flagsView);
        }
    }
}
