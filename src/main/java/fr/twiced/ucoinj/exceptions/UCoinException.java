package fr.twiced.ucoinj.exceptions;

public abstract class UCoinException extends Exception {

	private static final long serialVersionUID = -4831002041427111144L;

	public UCoinException(Throwable e) {
		super(e);
	}
	
	public UCoinException(String message) {
		super(message);
	}
}
