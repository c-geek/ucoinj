package fr.twiced.ucoinj.service;

import java.util.List;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;

public interface PKSService {

	/**
	 * Tries to persist given public key with given self signature.
	 * @param pubkey The public key to store.
	 * @param signature The ASCII armored signature of the ASCII armored public key.
	 * @return PublicKey stored public key
	 * @throws ObsoleteDataException if a more recent data was recorded
	 * @throws BadSignatureException if signature does not match signed pubkey
	 */
	PublicKey add(PublicKey pubkey, Signature signature) throws ObsoleteDataException, BadSignatureException;
	
	/**
	 * Find a list of public keys matching given search
	 * @param search Search string that matches some results.
	 * @return A list of matching public keys.
	 */
	List<PublicKey> lookup(String search);
	
	/**
	 * Merkle resource of all stored public keys.
	 * @return A Merkle result of all stored public keys.
	 */
	Merkle<PublicKey> all();
}
