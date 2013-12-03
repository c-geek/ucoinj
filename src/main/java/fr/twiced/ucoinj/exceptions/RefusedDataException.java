package fr.twiced.ucoinj.exceptions;

public class RefusedDataException extends Exception {

	private static final long serialVersionUID = 4562055141310615780L;

	public RefusedDataException() {
		this("Given data is not accepted by this node");
	}
	
	public RefusedDataException(String message) {
		super(message);
	}
}
