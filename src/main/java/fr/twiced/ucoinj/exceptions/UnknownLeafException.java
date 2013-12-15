package fr.twiced.ucoinj.exceptions;

public class UnknownLeafException extends UCoinException {

	private static final long serialVersionUID = -6410611370992491798L;

	public UnknownLeafException() {
		this("Public key was not found on this server");
	}
	
	public UnknownLeafException(String message) {
		super(message);
	}
}
