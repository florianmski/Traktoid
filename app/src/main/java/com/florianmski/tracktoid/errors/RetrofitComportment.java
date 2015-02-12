package com.florianmski.tracktoid.errors;

import android.view.View;

import java.net.URI;
import java.net.URISyntaxException;

import retrofit.RetrofitError;
import rx.functions.Func2;

public class RetrofitComportment extends Comportment
{
    public RetrofitComportment(View.OnClickListener actionOnTap)
    {
        // set message to null because func will put the right one
        super(RetrofitError.class, null, "tap to retry", actionOnTap);
        func = new RetrofitComportmentFunc();
    }

    public RetrofitComportment()
    {
        this(null);
    }

    private class RetrofitComportmentFunc implements Func2<Throwable, Comportment, Boolean>
    {
        private String getDomain(RetrofitError e) throws URISyntaxException
        {
            URI uri = new URI(e.getUrl());
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }

        @Override
        public Boolean call(Throwable throwable, Comportment comportment)
        {
            RetrofitError e = (RetrofitError) throwable;
            String domain = null;
            try
            {
                domain = getDomain(e);
            }
            catch (URISyntaxException e1)
            {
                e1.printStackTrace();
            }

            switch (e.getKind())
            {
                case NETWORK:
                    comportment.userMessage = String.format("Error while trying to reach %s\nCheck your connection", domain);
                    break;
                case HTTP:
                    // if it's a server error
                    if(String.valueOf(e.getResponse().getStatus()).startsWith("5"))
                        comportment.userMessage = String.format("Impossible to reach %s at the moment", domain);
                    else
                        return false;
                    break;
                case CONVERSION:
                case UNEXPECTED:
                    return false;
            }
            return true;
        }
    }
}
