package fr.twiced.ucoinj.bean;

import fr.twiced.ucoinj.exceptions.BadFormatException;

public interface Rawable {
	
	public final static String CARRIAGE_RETURN = "\r\n";

	String getRaw();
	
	void parseFromRaw(String raw) throws BadFormatException;
}
