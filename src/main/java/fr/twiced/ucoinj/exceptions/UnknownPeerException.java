package fr.twiced.ucoinj.exceptions;

import fr.twiced.ucoinj.bean.Hash;

public class UnknownPeerException extends UCoinException {

	private static final long serialVersionUID = -6410611370992491798L;

	public UnknownPeerException() {
		this("Unknown peer");
	}
	
	public UnknownPeerException(String message) {
		super(message);
	}
	
	public UnknownPeerException(Hash hash) {
		this(String.format("Unknown peer #%s", hash.getJSON()));
	}
}
