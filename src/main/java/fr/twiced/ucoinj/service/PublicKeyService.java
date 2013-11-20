package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.PublicKey;

public interface PublicKeyService {

	PublicKey getByFingerprint(String fpr);
	
	void save(PublicKey pubkey);
}