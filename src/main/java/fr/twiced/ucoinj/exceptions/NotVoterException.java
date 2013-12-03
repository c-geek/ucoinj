package fr.twiced.ucoinj.exceptions;

public class NotVoterException extends RefusedDataException {

	private static final long serialVersionUID = 9015934007275597884L;

	public NotVoterException() {
		this("Voting is only possible for allowed voters");
	}
	
	public NotVoterException(String message) {
		super(message);
	}
}
