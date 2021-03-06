package fr.twiced.ucoinj.exceptions;

public class BadSignatureException extends Exception {

	private static final long serialVersionUID = 8167131438803222091L;

	public BadSignatureException() {
		this("Signature does not match");
	}
	
	public BadSignatureException(Exception e) {
		super(e);
	}

	public BadSignatureException(String message) {
		super(message);
	}

}
