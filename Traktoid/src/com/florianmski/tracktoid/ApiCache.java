package com.florianmski.tracktoid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;

import com.jakewharton.trakt.TraktApiBuilder;

public class ApiCache 
{
	private static final String GET_FOLDER = "get/";
	@SuppressWarnings("unused")
	private static final String POST_FOLDER = "post/";
	
	private static String getPath(TraktApiBuilder<?> builder, String type, Context context)
	{
		return Utils.getExtFolderPath(context) + type + Utils.getMD5Hex(builder.getURL());
	}

	public static void save(TraktApiBuilder<?> builder, Object toSave, Context context)
	{
		if(builder == null || toSave == null || context == null)
			return;
		
		ObjectOutputStream outputStream = null;

		try 
		{
			String path = getPath(builder, GET_FOLDER, context);
			File f = new File(path);
			f.getParentFile().mkdirs();
			f.createNewFile();
			outputStream = new ObjectOutputStream(new FileOutputStream(f));
			outputStream.writeObject(toSave);
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

	@SuppressWarnings("unchecked")
	public static <T> T read(TraktApiBuilder<T> builder, Context context)
	{
		ObjectInputStream inputStream = null;
		T result = null;

		try 
		{
			String path = getPath(builder, GET_FOLDER, context);
			File f = new File(path);
			inputStream = new ObjectInputStream(new FileInputStream(f));
			result = (T) inputStream.readObject();
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
		return result;
	}
	
	public static List<TraktApiBuilder<?>> getPendingRequests()
	{
		//TODO do something to allow user to perform offline modification then when an internet connection is available, send it to trakt.tv
		//this is an idea for maybe a "future future" update
		return null;
	}
}
