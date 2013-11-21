package fr.twiced.ucoinj.exceptions;

public class Sha1ConvertionException extends RuntimeException {

	private static final long serialVersionUID = -2641929319470635195L;

	public Sha1ConvertionException(Exception e) {
		super(e);
	}
}
