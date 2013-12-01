package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.PublicKey;


public interface PublicKeyService extends PKSService {
	
	PublicKey getByFingerprint(String fingerprint);
}
