package com.florianmski.tracktoid.ui.fragments.traktitems;

import android.graphics.drawable.ColorDrawable;

import com.florianmski.tracktoid.data.TraktBase;
import com.florianmski.tracktoid.data.TraktoidItem;
import com.florianmski.tracktoid.image.ImagesTest;
import com.florianmski.tracktoid.image.Type;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.trakt.v2.entities.BaseCheckinResponse;

public abstract class MediaBaseFragment<T extends TraktoidItem, C extends BaseCheckinResponse> extends TraktItemFragment<T, C>
{
    @Override
    protected void refreshGeneralView(TraktBase traktBase)
    {
        super.refreshGeneralView(traktBase);

        Picasso.with(getActivity())
                .load(ImagesTest.getUrl(Type.POSTER, item.getTraktItem().images))
                .placeholder(new ColorDrawable(getTheme().getColorDark(getActivity())))
                .into(tivPoster);
    }
}
