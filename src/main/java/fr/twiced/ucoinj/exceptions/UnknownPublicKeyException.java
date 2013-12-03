package fr.twiced.ucoinj.exceptions;

public class UnknownPublicKeyException extends Exception {

	private static final long serialVersionUID = -6410611370992491798L;

	public UnknownPublicKeyException() {
		this("Public key was not found on this server");
	}
	
	public UnknownPublicKeyException(String message) {
		super(message);
	}
}
