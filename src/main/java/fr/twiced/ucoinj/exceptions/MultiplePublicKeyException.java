package fr.twiced.ucoinj.exceptions;

public class MultiplePublicKeyException extends Exception {

	private static final long serialVersionUID = 4463591736420472376L;

	public MultiplePublicKeyException() {
		this("Several public keys are matching this keyID, cannot go any further");
	}
	
	public MultiplePublicKeyException(String message) {
		super(message);
	}
}
