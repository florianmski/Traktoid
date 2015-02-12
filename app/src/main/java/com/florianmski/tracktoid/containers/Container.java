package com.florianmski.tracktoid.containers;

public class Container<E> implements ContainerInterface<E>
{
	protected E data;
	
	public Container() {}
	
	@Override
	public E get()
	{
		return data;
	}
	
	@Override
	public void set(E data)
	{
		this.data = data;
	}
}
