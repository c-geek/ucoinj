package fr.twiced.ucoinj.exceptions;

public class BadFormatException extends Exception {

	private static final long serialVersionUID = 8167131438803222091L;

	public BadFormatException() {
		this("Format does not match");
	}
	
	public BadFormatException(Exception e) {
		super(e);
	}

	public BadFormatException(String message) {
		super(message);
	}

}
