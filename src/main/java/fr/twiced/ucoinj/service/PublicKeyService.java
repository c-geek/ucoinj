package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;


public interface PublicKeyService extends PKSService {
	
	PublicKey getByFingerprint(String fingerprint);
	
	PublicKey getBySignature(Signature sig) throws MultiplePublicKeyException, UnknownPublicKeyException;
	
	PublicKey getWorking(PublicKey pubkey);
	
	Object all(Boolean leaves, String leaf) throws UnknownLeafException;
}
