package com.jakewharton.apibuilder;

/**
 * Represents an exception which occured executing a remote API method.
 * 
 * @author Jake Wharton <jakewharton@gmail.com>
 */
public class ApiException extends RuntimeException {
	private static final long serialVersionUID = -6336249534326959151L;
	
	public ApiException(Throwable e) {
		super(e);
	}
	public ApiException(String message) {
		super(message);
	}
}
