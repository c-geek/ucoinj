package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.PublicKey;

public interface PublicKeyDao {

	PublicKey getByFingerprint(String fpr);
	
	void save(PublicKey pubkey);
}
