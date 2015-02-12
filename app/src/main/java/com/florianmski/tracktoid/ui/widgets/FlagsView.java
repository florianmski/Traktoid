package com.florianmski.tracktoid.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.TraktoidTheme;
import com.florianmski.tracktoid.data.TraktBase;

public class FlagsView extends RelativeLayout
{
    private View vSeen;
    private View vCollection;
    private View vWatchlist;

    private TextView tvTitle;
    private TextView tvSubtitle;

    public FlagsView(Context context)
    {
        this(context, null, 0);
    }

    public FlagsView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FlagsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context)
    {
        View v = inflate(context, R.layout.view_flags, this);
        vSeen = v.findViewById(R.id.viewSeen);
        vCollection = v.findViewById(R.id.viewCollection);
        vWatchlist = v.findViewById(R.id.viewWatchlist);
        tvTitle = (TextView)v.findViewById(R.id.textViewTitle);
        tvSubtitle = (TextView)v.findViewById(R.id.textViewSubtitle);
    }

    private void resetVisibility()
    {
        flagsVisibility(false);
        visibility(tvTitle, false);
        visibility(tvSubtitle, false);
    }

    private void flagsVisibility(Boolean flagsVisibility)
    {
        visibility(vSeen, flagsVisibility);
        visibility(vCollection, flagsVisibility);
        visibility(vWatchlist, flagsVisibility);
    }

    public void init(Flags flags)
    {
        resetVisibility();

        if(flags.traktBase != null)
        {
            visibility(vSeen, flags.traktBase.watched);
            visibility(vCollection, flags.traktBase.collected);
            visibility(vWatchlist, flags.traktBase.watchlisted);

            setTitle(flags.traktBase.title);
        }

        if(flags.title != null)
            setTitle(flags.title);

        if(flags.subtitle != null)
            setSubtitle(flags.subtitle);

        if(flags.theme != null)
        {
            tvTitle.setBackgroundColor(flags.theme.getColor(getContext()));
            tvSubtitle.setBackgroundColor(flags.theme.getColor(getContext()));
        }

        flagsVisibility(flags.displayFlags);
        visibility(tvTitle, flags.displayTitle);
        visibility(tvSubtitle, flags.displaySubtitle);
    }

    private void setText(TextView tv, String text)
    {
        if(text != null && !text.isEmpty())
        {
            visibility(tv, true);
            tv.setText(text);
        }
        else
            visibility(tv, false);
    }

    public void setTitle(String title)
    {
        setText(tvTitle, title);
    }

    public void setSubtitle(String subtitle)
    {
        setText(tvSubtitle, subtitle);
    }

    // TODO put it in a Utils class (ViewUtils?)
    private void visibility(View v, Boolean visible)
    {
        if(visible != null)
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static class Flags
    {
        public final TraktBase traktBase;
        public final String title;
        public final String subtitle;
        public Boolean displayFlags;
        public Boolean displayTitle;
        public Boolean displaySubtitle;
        public TraktoidTheme theme;

        public Flags(Builder builder)
        {
            this.traktBase = builder.traktBase;
            this.title = builder.title;
            this.subtitle = builder.subtitle;
            this.displayFlags = builder.displayFlags;
            this.displayTitle = builder.displayTitle;
            this.theme = builder.theme;
        }

        public static class Builder
        {
            private TraktBase traktBase;
            private String title;
            private String subtitle;
            private Boolean displayFlags;
            private Boolean displayTitle;
            private Boolean displaySubtitle;
            private TraktoidTheme theme;

            public Builder() {}

            public Builder(TraktBase traktBase)
            {
                this.traktBase = traktBase;
            }

            public Builder title(String title)
            {
                this.title = title;
                return this;
            }

            public Builder subtitle(String subtitle)
            {
                this.subtitle = subtitle;
                return this;
            }

            public Builder displayFlags(boolean displayFlags)
            {
                this.displayFlags = displayFlags;
                return this;
            }

            public Builder displayTitle(boolean displayTitle)
            {
                this.displayTitle = displayTitle;
                return this;
            }

            public Builder displaySubtitle(boolean displaySubtitle)
            {
                this.displaySubtitle = displaySubtitle;
                return this;
            }

            public Builder theme(TraktoidTheme theme)
            {
                this.theme = theme;
                return this;
            }

            public Flags build()
            {
                return new Flags(this);
            }
        }
    }
}
