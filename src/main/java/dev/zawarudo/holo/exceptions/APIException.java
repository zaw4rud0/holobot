package dev.zawarudo.holo.exceptions;

/**
 * An exception that is thrown when the API returned an error
 */
public class APIException extends Exception {

	public APIException() {
	}
	
	public APIException(String message) {
		super(message);
	}
}