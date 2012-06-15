package com.jakewharton.apibuilder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * <p>HTTP method execution.</p>
 * 
 * <p>Parts of the code for this class are from Nabeel Mukhtar's
 * github-java-sdk library.</p>
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 */
public class ApiService {
	protected static final String CONTENT_ENCODING = "UTF-8";
	
	private static final int DEFAULT_TIMEOUT_CONNECT = -1;
	private static final int DEFAULT_TIMEOUT_READ = -1;
	private static final String GZIP = "gzip";
	private static final String HTTP_METHOD_POST = "POST";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	
	private final Map<String, String> requestHeaders;
	private final Map<String, String> requestParameters;
	private int connectTimeout;
	private int readTimeout;
	
	public ApiService() {
		this.requestHeaders = new HashMap<String, String>();
		this.requestParameters = new HashMap<String, String>();
		
		this.setConnectTimeout(DEFAULT_TIMEOUT_CONNECT);
		this.setReadTimeout(DEFAULT_TIMEOUT_READ);

	}
	
	/**
	 * Add the HTTP header denoting that we accept GZIP.
	 */
	protected void acceptGzip() {
		this.addRequestHeader(HEADER_ACCEPT_ENCODING, GZIP);
	}
	
	/**
	 * Add an HTTP request header.
	 * 
	 * @param name Header name.
	 * @param value Header value.
	 */
	public void addRequestHeader(String name, String value) {
		this.requestHeaders.put(name, value);
	}
	
	/**
	 * Get an HTTP request header value.
	 * 
	 * @param value Header name.
	 * @return Header value.
	 */
	public String getRequestHeader(String name) {
	    return this.requestHeaders.get(name);
	}
	
	/**
	 * Set of all HTTP request header names.
	 * 
	 * @return Header names.
	 */
	public Set<String> getRequestHeaderNames() {
	    return this.requestHeaders.keySet();
	}
	
	/**
	 * Remove an HTTP request header.
	 * 
	 * @param name Header name.
	 */
	public void removeRequestHeader(String name) {
		this.requestHeaders.remove(name);
	}
	
	/**
	 * Get connection timeout value.
	 * 
	 * @return Timeout (in milliseconds).
	 */
	public int getConnectTimeout() {
		return this.connectTimeout;
	}
	
	/**
	 * Set the connection timeout value.
	 * 
	 * @param connectTimeout Timeout (in milliseconds).
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	/**
	 * Get read timeout value.
	 * 
	 * @return Timout (in milliseconds).
	 */
	public int getReadTimeout() {
		return this.readTimeout;
	}
	
	/**
	 * Set read timeout value.
	 * 
	 * @param readTimeout Timeout (in milliseconds).
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	/**
	 * Execute the URL using HTTP GET.
	 * 
	 * @param apiUrl URL.
	 * @return Response stream.
	 */
	protected InputStream executeGet(String apiUrl) {
		return this.executeGet(apiUrl, HttpURLConnection.HTTP_OK);
	}
	
	/**
	 * Execute the URL using HTTP GET.
	 * 
	 * @param apiUrl URL.
	 * @param expected Expected HTTP response code.
	 * @return Response stream.
	 */
	protected InputStream executeGet(String apiUrl, int expected) {
	    try {
	        URL url = new URL(apiUrl);
	        if (!this.requestParameters.isEmpty()) {
	        	if (url.getQuery() == null) {
	        		url = new URL(apiUrl + "?" + ApiService.getParametersString(this.requestParameters));
	        	} else {
	        		url = new URL(apiUrl + "&" + ApiService.getParametersString(this.requestParameters));
	        	}
	        }

	        HttpURLConnection request = (HttpURLConnection)url.openConnection();

	        if (this.connectTimeout > DEFAULT_TIMEOUT_CONNECT) {
	            request.setConnectTimeout(this.connectTimeout);
	        }
	        if (this.readTimeout > DEFAULT_TIMEOUT_READ) {
	            request.setReadTimeout(this.readTimeout);
	        }

	        for (String headerName : this.requestHeaders.keySet()) {
	            request.setRequestProperty(headerName, this.requestHeaders.get(headerName));
	        }

	        request.connect();

	        if (request.getResponseCode() != expected) {
	            throw new ApiException(convertStreamToString(getWrappedInputStream(request.getErrorStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()))));
	        } else {
	            return getWrappedInputStream(request.getInputStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()));
	        }
	    } catch (IOException e) {
	        throw new ApiException(e);
	    }
	}
	
	/**
	 * Execute the URL using HTTP POST.
	 * 
	 * @param apiUrl URL.
	 * @param parameters POST body parameters.
	 * @return Response stream.
	 */
	protected InputStream executePost(String apiUrl, Map<String, String> parameters) {
		return this.executePost(apiUrl, parameters, HttpURLConnection.HTTP_OK);
	}
	
	/**
	 * Execute the URL using HTTP POST
	 * 
	 * @param apiUrl URL.
	 * @param parameters POST body parameters.
	 * @param expected Excepted HTTP response code.
	 * @return Response stream.
	 */
	protected InputStream executePost(String apiUrl, Map<String, String> parameters, int expected) {
		try {
            URL url = new URL(apiUrl);
            HttpURLConnection request = (HttpURLConnection)url.openConnection();

            if (this.connectTimeout > DEFAULT_TIMEOUT_CONNECT) {
                request.setConnectTimeout(this.connectTimeout);
            }
            if (this.readTimeout > DEFAULT_TIMEOUT_READ) {
                request.setReadTimeout(this.readTimeout);
            }
            
            for (String headerName : this.requestHeaders.keySet()) {
                request.setRequestProperty(headerName, this.requestHeaders.get(headerName));
            }
            
            parameters.putAll(this.requestParameters);

            request.setRequestMethod(HTTP_METHOD_POST);
            request.setDoOutput(true);

            PrintStream out = new PrintStream(new BufferedOutputStream(request.getOutputStream()));
            
            out.print(getParametersString(parameters));
            out.flush();
            out.close();

            request.connect();
            
            if (request.getResponseCode() != expected) {
            	throw new ApiException(convertStreamToString(getWrappedInputStream(request.getErrorStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()))));
            } else {
                return getWrappedInputStream(request.getInputStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()));
            }
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}
	
	/**
	 * Execute the URL using HTTP DELETE.
	 * 
	 * @param apiUrl URL.
	 * @return Response stream.
	 */
	protected InputStream executeDelete(String apiUrl) {
		return this.executeDelete(apiUrl, HttpURLConnection.HTTP_OK);
	}
	
	/**
	 * Execute the URL using HTTP DELETE.
	 * 
	 * @param apiUrl URL.
	 * @param expected Expected HTTP response code.
	 * @return Response stream.
	 */
	protected InputStream executeDelete(String apiUrl, int expected) {
	    try {
	        URL url = new URL(apiUrl);
	        HttpURLConnection request = (HttpURLConnection)url.openConnection();

	        if (this.connectTimeout > DEFAULT_TIMEOUT_CONNECT) {
	            request.setConnectTimeout(this.connectTimeout);
	        }
	        if (this.readTimeout > DEFAULT_TIMEOUT_READ) {
	            request.setReadTimeout(this.readTimeout);
	        }

	        for (String headerName : this.requestHeaders.keySet()) {
	            request.setRequestProperty(headerName, this.requestHeaders.get(headerName));
	        }

            request.setRequestMethod(HTTP_METHOD_DELETE);
            
	        request.connect();

	        if (request.getResponseCode() != expected) {
	            throw new ApiException(convertStreamToString(getWrappedInputStream(request.getErrorStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()))));
	        } else {
	            return getWrappedInputStream(request.getInputStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()));
	        }
	    } catch (IOException e) {
	        throw new ApiException(e);
	    }
	}
	
	/**
	 * Execute URL using the specified HTTP method name.
	 * 
	 * @param apiUrl URL.
	 * @param content Request body content.
	 * @param contentType Request body content type.
	 * @param method HTTP method name.
	 * @param expected Expected HTTP response code.
	 * @return Response stream.
	 */
	protected InputStream executeMethod(String apiUrl, String content, String contentType, String method, int expected) {
	    try {
	        URL url = new URL(apiUrl);
	        HttpURLConnection request = (HttpURLConnection) url.openConnection();

	        if (this.connectTimeout > DEFAULT_TIMEOUT_CONNECT) {
	            request.setConnectTimeout(this.connectTimeout);
	        }
	        if (this.readTimeout > DEFAULT_TIMEOUT_READ) {
	            request.setReadTimeout(this.readTimeout);
	        }

	        for (String headerName : this.requestHeaders.keySet()) {
	            request.setRequestProperty(headerName, this.requestHeaders.get(headerName));
	        }

	        request.setRequestMethod(method);
	        request.setDoOutput(true);

	        if (contentType != null) {
	            request.setRequestProperty(HEADER_CONTENT_TYPE, contentType);
	        }
	        if (content != null) {
	            PrintStream out = new PrintStream(new BufferedOutputStream(request.getOutputStream()));
	            out.print(content);
	            out.flush();
	            out.close();
	        }

	        request.connect();

	        if (request.getResponseCode() != expected) {
	            throw new ApiException(convertStreamToString(getWrappedInputStream(request.getErrorStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()))));
	        } else {
	            return getWrappedInputStream(request.getInputStream(), GZIP.equalsIgnoreCase(request.getContentEncoding()));
	        }
	    } catch (IOException e) {
	        throw new ApiException(e);
	    }
	}
	
	
	/**
	 * Assemble a parameter string from a mapping.
	 * 
	 * @param parameters Mapping of parameter names to values.
	 * @return String representation.
	 */
	protected static String getParametersString(Map<String, String> parameters) {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : parameters.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(encodeUrl(entry.getValue()));
			builder.append("&");
		}
		
		//Remove last '&'
		builder.deleteCharAt(builder.length() - 1);

		return builder.toString();
	}
	
	/**
	 * Close the specified stream.
	 * 
	 * @param is Stream to close.
	 */
	protected static void closeStream(InputStream is) {
	    try {
	    	if (is != null) {
		        is.close();
	    	}
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * Close the specified connection.
	 * 
	 * @param connection Connection to close.
	 */
	protected static void closeConnection(HttpURLConnection connection) {
	    try {
	    	if (connection != null) {
	    		connection.disconnect();
	    	}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	/**
	 * Properly wrap the stream accounting for GZIP.
	 * 
	 * @param is Stream to wrap.
	 * @param gzip Whether or not to include a GZIP wrapper.
	 * @return Wrapped stream.
	 * @throws IOException
	 */
	protected static InputStream getWrappedInputStream(InputStream is, boolean gzip) throws IOException {
	    if (gzip) {
	        return new BufferedInputStream(new GZIPInputStream(is));
	    } else {
	        return new BufferedInputStream(is);
	    }
	}
	
	/**
	 * Encode the URL.
	 * 
	 * @param original Original URL.
	 * @return Encoded URL.
	 */
    private static String encodeUrl(String original) {
    	try {
			return URLEncoder.encode(original, CONTENT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			// should never be here..
			return original;
		}
    }
	
    /**
     * Read an entire stream to end and assemble in a string.
     * 
     * @param is Stream to read.
     * @return Entire stream contents.
     */
	protected static String convertStreamToString(InputStream is) {
	    /*
	     * To convert the InputStream to String we use the BufferedReader.readLine()
	     * method. We iterate until the BufferedReader return null which means
	     * there's no more data to read. Each line will appended to a StringBuilder
	     * and returned as String.
	     */
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    return sb.toString();
	}
}