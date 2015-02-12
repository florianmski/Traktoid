package com.florianmski.tracktoid.ui.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.florianmski.tracktoid.R;

public class ReadMoreView extends LinearLayout implements ValueAnimator.AnimatorUpdateListener
{
    private final static int MAX_LINES = 4;

    // TODO should remember its state
    protected View vContent;
    protected ScrollView sv;
    protected TextView tvReadMore;
    protected TextView tvText;

    private int contentId;
    private int textId;
    private int contentMaxHeight;
    private int contentMinHeight;
    private int readMoreHeight;

    private boolean isExpanded = true;

    private Interpolator interpolator = new AccelerateDecelerateInterpolator();

    private static final Property<View, Integer> VIEW_LAYOUT_HEIGHT = new Property<View, Integer>(Integer.class, "viewLayoutHeight")
    {
        @Override
        public void set(View object, Integer value)
        {
            object.getLayoutParams().height = value;
            object.requestLayout();
        }

        @Override
        public Integer get(View object)
        {
            return object.getLayoutParams().height;
        }
    };

    public ReadMoreView(Context context)
    {
        super(context);
    }

    public ReadMoreView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        setOrientation(LinearLayout.VERTICAL);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreView);
        // TODO IllegalArgumentException
        contentId = attributes.getResourceId(R.styleable.ReadMoreView_content, R.id.viewContent);
        textId = attributes.getResourceId(R.styleable.ReadMoreView_text, R.id.viewContent);
        attributes.recycle();

        tvReadMore = (TextView) LayoutInflater.from(context).inflate(R.layout.view_read_more, this, false);

        //        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        //        {
        //            @Override
        //            public void onGlobalLayout()
        //            {
        //                getViewTreeObserver().removeGlobalOnLayoutListener(this);
        //
        //                // by default, collapse
        //                setExpanded(false, false);
        //            }
        //        });
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        vContent = findViewById(contentId);
        tvText = (TextView) findViewById(textId);

        addView(tvReadMore);

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toggle(true);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        vContent.measure(widthMeasureSpec, heightMeasureSpec);
        tvText.measure(widthMeasureSpec, heightMeasureSpec);
        tvReadMore.measure(widthMeasureSpec, heightMeasureSpec);
        contentMaxHeight = vContent.getMeasuredHeight();
        contentMinHeight = Math.min((tvText.getLineHeight() * MAX_LINES), tvText.getMeasuredHeight()) + tvText.getTotalPaddingTop() + tvText.getTotalPaddingBottom() + vContent.getPaddingTop() + vContent.getPaddingBottom();
        readMoreHeight = tvReadMore.getMeasuredHeight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setExpanded(boolean expanded, boolean animate)
    {
        if(isExpanded != expanded)
            toggle(animate);
    }

    public void toggle(boolean animate)
    {
        ObjectAnimator expandCollapseContentAnimator = ObjectAnimator.ofInt(vContent, VIEW_LAYOUT_HEIGHT, vContent.getHeight(), isExpanded ? contentMinHeight : contentMaxHeight);
        ObjectAnimator expandCollapseReadMoreAnimator = ObjectAnimator.ofInt(tvReadMore, VIEW_LAYOUT_HEIGHT, tvReadMore.getHeight(), isExpanded ? readMoreHeight : 0);

        AnimatorSet as = new AnimatorSet();
        as.play(expandCollapseContentAnimator).with(expandCollapseReadMoreAnimator);
        as.setInterpolator(interpolator);

        expandCollapseContentAnimator.addUpdateListener(this);
        as.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isExpanded = !isExpanded;
            }
        });

        as.setDuration(animate ? 300 : 0);
        as.start();
    }

    public boolean isExpanded()
    {
        return isExpanded;
    }

    public void makeItScroll(ScrollView scrollView)
    {
        this.sv = scrollView;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation)
    {
        // we want to make it scroll only in case we are collapsed, the content is not higher than the view
        // and the contentMaxHeight is not enough to from to the bottom of the scrollview
//        if(sv != null && !isExpanded && contentMaxHeight < sv.getHeight() && (getBottom() + sv.getPaddingTop()) >= sv.getBottom())
//        {
//            // TODO when scrolling to bottom, it's off by some pixels, weird
//            int pixels = Math.round(sv.getBottom() * animation.getAnimatedFraction());
//            sv.scrollTo(0, pixels);
//        }

        int bottom = vContent.getBottom() + vContent.getPaddingBottom();
        if(sv != null && !isExpanded && bottom > sv.getScrollY())
        {
            sv.scrollTo(0, bottom);
        }
    }
}
