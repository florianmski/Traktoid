package com.florianmski.tracktoid.adapters.lists;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.SpannableString;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.adapters.RecyclerAdapter;
import com.florianmski.tracktoid.ui.widgets.BlurSpan;
import com.florianmski.tracktoid.ui.widgets.Placeholder;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.trakt.v2.entities.Comment;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class RecyclerCommentsAdapter extends RecyclerAdapter<Comment, RecyclerCommentsAdapter.CommentsViewHolder>
{
    private final DateTimeFormatter dtf = DateTimeFormat.longDate();
    private Placeholder placeholder;
    private float maxRadius;
    private float minRadius = 0.1f;
    private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();

    private static final Property<BlurSpan, Float> BLUR_PROPERTY = new Property<BlurSpan, Float>(Float.class, "BLUR_PROPERTY")
    {
        @Override
        public void set(BlurSpan span, Float value)
        {
            span.setRadius(value);
        }

        @Override
        public Float get(BlurSpan span)
        {
            return span.getRadius();
        }
    };

    public RecyclerCommentsAdapter(Context context, List<Comment> data, TraktoidTheme theme, OnItemClickListener listener)
    {
        super(context, data, listener);
        maxRadius = context.getResources().getDisplayMetrics().density * 8;
        placeholder = new Placeholder(context, theme);
    }

    @Override
    public RecyclerCommentsAdapter.CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_comment, parent, false);
        return new CommentsViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(final RecyclerCommentsAdapter.CommentsViewHolder holder, int position)
    {
        final Comment c = getItem2(position);
        final BlurSpan span = new BlurSpan(maxRadius);
        final SpannableString spannableString = new SpannableString(c.comment);

        if(c.user.images != null)
            Picasso.with(context).load(c.user.images.avatar.full).placeholder(placeholder.getDrawable()).into(holder.ivAvatar);
        else
            holder.ivAvatar.setImageDrawable(placeholder.getDrawable());


        holder.tvUsername.setText(c.user.username);
        holder.tvDate.setText(c.created_at.toString(dtf));

        if(c.spoiler)
        {
            spannableString.setSpan(span, 0, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvComment.setText(spannableString);
        }
        else
            holder.tvComment.setText(c.comment);

        holder.ivAvatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                UserActivity.launch((Activity) context, c.user.username);
            }
        });

        holder.tvComment.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!c.spoiler)
                    return;

                ObjectAnimator blurAnimator = ObjectAnimator.ofFloat(span, BLUR_PROPERTY, maxRadius, minRadius);

                AnimatorSet as = new AnimatorSet();
                as.play(blurAnimator);
                as.setInterpolator(interpolator);

                blurAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        holder.tvComment.setText(spannableString);
                    }
                });

                blurAnimator.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        c.spoiler = false;
                    }
                });

                as.start();
            }
        });
    }

    public static class CommentsViewHolder extends RecyclerAdapter.ViewHolder
    {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvDate;
        private TextView tvComment;

        public CommentsViewHolder(View itemView, OnItemClickListener listener)
        {
            super(itemView, listener);

            ivAvatar = (ImageView) itemView.findViewById(R.id.imageViewAvatar);
            tvUsername = (TextView) itemView.findViewById(R.id.textViewUsername);
            tvDate = (TextView) itemView.findViewById(R.id.textViewDate);
            tvComment = (TextView) itemView.findViewById(R.id.textViewComment);

            // BlurSpan doesn't work with HW
            tvComment.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
}
