package com.florianmski.tracktoid.containers;

import com.florianmski.tracktoid.adapters.AdapterInterface;

public interface ContainerInterface<T>
{
    public T get();
    public void set(T data);

    public interface DataContainerInterface<T> extends ContainerInterface<T>
    {
        public boolean isEmpty();
        public void init();
        public void clear();
    }

    public interface ViewContainerInterface<E, V, A extends AdapterInterface<E>> extends ContainerInterface<V>
    {
        public void setAdapter(A adapter);
        public A getAdapter();
        public int getLayoutId();
    }
}
