package com.florianmski.tracktoid.errors;

import android.view.View;

import rx.functions.Func2;

public class Comportment
{
    public Class<?> clazz;
    public String userMessage;
    public String userActionMessage;
    public View.OnClickListener actionOnTap;
    // this func is useful to modify a comportment or analyse the exception and decide if whether
    // or not we handle it
    // return true if exception is handled, false otherwise
    public Func2<Throwable, Comportment, Boolean> func;

    public Comportment(Class<?> clazz, String userMessage, String userActionMessage, View.OnClickListener actionOnTap)
    {
        this.clazz = clazz;
        this.userMessage = userMessage;
        this.userActionMessage = userActionMessage;
        this.actionOnTap = actionOnTap;
    }

    public Comportment(Class<?> clazz, String userMessage, String userActionMessage)
    {
        this(clazz, userMessage, userActionMessage, null);
    }

    public Comportment(Class<?> clazz, String userMessage)
    {
        this(clazz, userMessage, null, null);
    }

    public Comportment(Class<?> clazz)
    {
        this(clazz, null, null, null);
    }
}