package fr.twiced.ucoinj.bean.id;

import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.NaturalId;

public class KeyId extends Hash implements NaturalId {

	public KeyId(String hash) {
		super(hash);
	}

}
