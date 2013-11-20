package fr.twiced.ucoinj.service;

import org.bouncycastle.openpgp.PGPPublicKey;

import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;

public interface PGPService {

	PGPPublicKey extractPubkey(String base64stream) throws NoPublicKeyPacketException;
	
	boolean verify(String originalDocument, String base64signature, PGPPublicKey pubkey) throws BadSignatureException, NoPublicKeyPacketException, NoSignaturePacketException, Exception;
	
	String extractIssuer(String base64signature) throws NoSignaturePacketException, Exception;
	
	String extractFingerprint(PGPPublicKey publicKey);
	
	String extractFingerprint(String base64PublicKey) throws NoPublicKeyPacketException;
}
