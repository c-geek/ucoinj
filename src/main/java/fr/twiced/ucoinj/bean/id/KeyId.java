package fr.twiced.ucoinj.bean.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.NaturalId;

@Embeddable
public class KeyId extends Hash implements NaturalId {

	public KeyId() {
		super();
	}
	
	public KeyId(String hash) {
		super(hash);
	}
	
	@Column(name = "owner", nullable = true)
	@Override
	public String getHash() {
		return super.getHash();
	}

}
