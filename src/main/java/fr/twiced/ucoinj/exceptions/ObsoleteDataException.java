package fr.twiced.ucoinj.exceptions;

public class ObsoleteDataException extends RefusedDataException {

	private static final long serialVersionUID = 2160176498761867452L;

	public ObsoleteDataException() {
		this("signature is considered obsolete");
	}
	
	public ObsoleteDataException(String message) {
		super(message);
	}
}
