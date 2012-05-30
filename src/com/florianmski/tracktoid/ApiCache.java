package com.florianmski.tracktoid;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap.Config;

import com.jakewharton.trakt.TraktApiBuilder;

public class ApiCache 
{
	private static final String GET_FOLDER = "/get";
	private static final String POST_FOLDER = "/post";
	
	private static String getPath(TraktApiBuilder<?> builder, String type, Context context)
	{
		try 
		{
			return Utils.getExtFolderPath(context) + type + URLEncoder.encode(builder.getURL(), "UTF-8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	public static void save(TraktApiBuilder<?> builder, Context context)
	{
		ObjectOutputStream outputStream = null;

		try 
		{
			outputStream = new ObjectOutputStream(new FileOutputStream(getPath(builder, GET_FOLDER, context)));
			outputStream.writeObject(builder);
		} 
		catch (FileNotFoundException ex) 
		{
			ex.printStackTrace();
		} 
		catch (IOException ex) 
		{
			ex.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (outputStream != null) 
				{
					outputStream.flush();
					outputStream.close();
				}
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
	}

	public static Object load(TraktApiBuilder<?> builder, Context context)
	{
		ObjectInputStream inputStream = null;
		Object o = null;

		try 
		{
			inputStream = new ObjectInputStream(new FileInputStream(getPath(builder, POST_FOLDER, context)));
			o = (Config) inputStream.readObject();
		}  
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				if (inputStream != null) 
					inputStream.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return o;
	}
	
	public static List<TraktApiBuilder<?>> getPendingRequests()
	{
		//TODO
		return null;
	}
}
