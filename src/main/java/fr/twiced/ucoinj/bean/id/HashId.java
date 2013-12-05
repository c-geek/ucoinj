package fr.twiced.ucoinj.bean.id;

import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.NaturalId;

public class HashId extends Hash implements NaturalId {

	public HashId(String hash) {
		super(hash);
	}

}
