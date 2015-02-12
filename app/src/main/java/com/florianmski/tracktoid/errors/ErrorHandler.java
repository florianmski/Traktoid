package com.florianmski.tracktoid.errors;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ErrorHandler
{
    // TODO maybe cleaner to provide a builder (and for comportment too?)

    private boolean reportToUser = true;
    private Map<Class, Comportment> handlers = new HashMap<>();
    private Comportment defaultComportment = null;

    private Context context;
    private TextView tv;

    public ErrorHandler(Context context)
    {
        this.context = context;
    }

    public ErrorHandler(Context context, Comportment defaultComportment)
    {
        this(context);
        this.defaultComportment = defaultComportment;
    }

    public ErrorHandler(Context context, TextView target, Comportment defaultComportment)
    {
        this(context, defaultComportment);
        this.tv = target;
    }

    public ErrorHandler reportToUser(boolean reportToUser)
    {
        this.reportToUser = reportToUser;
        return this;
    }

    public ErrorHandler putComportment(Comportment comportment)
    {
        handlers.put(comportment.clazz, comportment);
        return this;
    }

    public void handle(Throwable t, String messageIfNotHandled)
    {
        Comportment comportment = handlers.get(t.getClass());
        if(comportment == null || (comportment.func != null && !comportment.func.call(t, comportment)))
        {
            comportment = defaultComportment;
            Timber.e(t, messageIfNotHandled);
        }

        if(!reportToUser)
            return;

        // display error in the textview
        if(tv != null)
        {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(comportment.userMessage).append("\n");
            int start = builder.length();

            if(comportment.userActionMessage != null)
            {
                builder.append(comportment.userActionMessage.toUpperCase());
                builder.setSpan(new ForegroundColorSpan(Color.WHITE), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new RelativeSizeSpan(0.9f), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new TypefaceSpan("sans-serif-condensed"), start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            tv.setText(builder);
            tv.setOnClickListener(comportment.actionOnTap);
            tv.setClickable(comportment.actionOnTap != null);
        }
        // else display in a toast
        else
        {
            // TODO use snackbar and event bus (rx?) so it can be displayed in the current activity
        }
    }
}
