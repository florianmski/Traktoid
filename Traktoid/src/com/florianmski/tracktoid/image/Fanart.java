/*
 * Copyright 2011 Florian Mierzejewski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.florianmski.tracktoid.image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.util.Log;

public class Fanart extends DefaultHandler
{
	public final static int CLEARLOGO = 0, CLEARART = 1, TVTHUMB = 2, SEASONTHUMB = 3;
	
	private static Fanart fanart;

	private HttpResponse res;
	private DefaultHttpClient http;
	private SAXParserFactory spf;
	private SAXParser sp;
	private XMLReader xr;
	
	private ArrayList<String> urls;
	private String type;

	public static synchronized Fanart getFanartParser()
	{
		if (fanart == null)
			fanart = new Fanart();
		return fanart;
	}

	//initialize each parameter once
	private Fanart() 
	{		
		http = new DefaultHttpClient();

		spf = SAXParserFactory.newInstance();
		try
		{
			sp = spf.newSAXParser();
			xr = sp.getXMLReader();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		xr.setContentHandler(this);
	}
	
	public String getFanart(String tvdb_id, int type, Context c)
	{
		switch(type)
		{
			case CLEARLOGO:
				this.type = "clearlogo";
				break;
			case CLEARART:
				this.type = "clearart";
				break;
			case TVTHUMB:
				this.type = "tvthumb";
				break;
			case SEASONTHUMB:
				this.type = "seasonthumb";
				break;
		}
		URL url = null;
		try 
		{
			url = new URL("http://fanart.tv/api/fanart.php?id=" + tvdb_id + "&type=" + this.type + "&sort=favdesc");
		} 
		catch (MalformedURLException e) {}
		loadData(url, c);
		return getUrl();
		
	}
	
	private String getUrl()
	{
		if(urls == null || urls.size() == 0)
			return null;
		return urls.get(0);
	}

	//get xml and parse it
	private synchronized void loadData(URL url, Context c) 
	{
		try
		{
			HttpUriRequest request = new HttpGet(url.toString());
			request.addHeader("Accept-Encoding", "gzip");
			res = http.execute(request);
		} 
		catch (IOException e)
		{
			Log.e("FanartParser", "Error loading data", e);
		}
    	try
		{
    		//using special encoding to reduce download time
    		InputStream instream = res.getEntity().getContent();
    		Header contentEncoding = res.getFirstHeader("Content-Encoding");
    		if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip"))
    		    instream = new GZIPInputStream(instream);
    		
			xr.parse(new InputSource(instream));
		}
    	catch (Exception e)
		{
    		Log.e("FanartParser", "Error parsing data", e);
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
	{
		if (localName.equals("fanart"))
		{
			urls = new ArrayList<String>();
		}
		else if (localName.equals(this.type))
		{
			urls.add(attributes.getValue("url"));
		}
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {}

	@Override
	public void characters(char ch[], int start, int length) {}
}