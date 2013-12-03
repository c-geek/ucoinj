package fr.twiced.ucoinj.bean;

import fr.twiced.ucoinj.exceptions.BadFormatException;

public interface Rawable {

	String getRaw();
	
	void parseFromRaw(String raw) throws BadFormatException;
}
