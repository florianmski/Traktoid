package com.florianmski.tracktoid.ui.fragments.base.pager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.florianmski.tracktoid.utils.ColorFilterHelper;
import com.florianmski.tracktoid.R;
import com.florianmski.tracktoid.utils.Utils;
import com.florianmski.tracktoid.ui.widgets.CrossFadeDrawable;
import com.florianmski.tracktoid.ui.widgets.TraktImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public abstract class PagerHeaderImageFragment extends PagerHeaderFragment
{
    protected TraktImageView tivScreen;

    private final ColorFilterHelper cfh = new ColorFilterHelper();

    protected SparseArray<String> imagesArray = new SparseArray<String>();

    protected CrossFadeDrawable cfd = new CrossFadeDrawable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_pager_header_image, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        tivScreen = (TraktImageView)view.findViewById(R.id.imageViewScreen);
    }

    @Override
    public void onHeaderTranslate(int key, float translationY)
    {
        float percentTranslated = 1 - (flHeader.getTop() - translationY) / (flHeader.getHeight() - getActionBar().getHeight() - tabs.getHeight());
        tivScreen.setTranslationY(-translationY * 0.5f);
        cfh.update(tivScreen, percentTranslated);
    }

    public void changeHeaderImage(String url)
    {
        Picasso.with(getActivity()).load(url).placeholder(new ColorDrawable(Utils.getColorFromAttribute(getActivity(), R.attr.colorPrimary))).into(tivScreen);
//        Picasso.with(getActivity()).load(i.getUrl()).into(new Target()
//        {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
//            {
//                Drawable initialDrawable = tivScreen.getDrawable();
//                Drawable[] drawables = new Drawable[] {
//                        initialDrawable == null ? new ColorDrawable() : initialDrawable,
//                        new BitmapDrawable(getResources(), bitmap)
//                };
//                TransitionDrawable d = new TransitionDrawable(drawables);
//                tivScreen.setImageDrawable(d);
//                d.startTransition(500);
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable)
//            {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable)
//            {
//
//            }
//        });
    }

    public void setImage(int position, String url)
    {
        imagesArray.put(position, url);
    }

    @Override
    public void onPageSelected(int position)
    {
        super.onPageSelected(position);

        picassoLoad(position-1);
        picassoLoad(position);
        picassoLoad(position+1);
    }

    private void picassoLoad(final int position)
    {
        if(position < 0 || position >= imagesArray.size())
            return;

        Picasso.with(getActivity()).load(imagesArray.get(position)).into(new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                if(position == PagerHeaderImageFragment.this.position)
//                    PagerHeaderImageFragment.this.cfd.setCurrent(new BitmapDrawable(getResources(), bitmap));
                    PagerHeaderImageFragment.this.cfd.setCurrent(new ColorDrawable(Color.GREEN));
                else if(position < PagerHeaderImageFragment.this.position)
//                    PagerHeaderImageFragment.this.cfd.setBefore(new BitmapDrawable(getResources(), bitmap));
                    PagerHeaderImageFragment.this.cfd.setBefore(new ColorDrawable(Color.BLUE));
                else
//                    PagerHeaderImageFragment.this.cfd.setAfter(new BitmapDrawable(getResources(), bitmap));
                    PagerHeaderImageFragment.this.cfd.setAfter(new ColorDrawable(Color.RED));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable)
            {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable)
            {

            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);

        cfd.setPositionOffset(this.position == position ? positionOffset : -positionOffset);
        tivScreen.setImageDrawable(cfd);
    }
}
