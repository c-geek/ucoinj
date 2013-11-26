package fr.twiced.ucoinj.service;

import java.io.IOException;
import java.security.SignatureException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;

public interface PGPService {
	
	PublicKey extractPublicKey(String base64stream) throws NoPublicKeyPacketException, IOException;
	
	boolean verify(String originalDocument, String base64signature, PublicKey pubkey) throws BadSignatureException, NoPublicKeyPacketException, NoSignaturePacketException, Exception;
	
	String extractIssuer(String base64signature) throws NoSignaturePacketException, Exception;
	
	String extractFingerprint(String base64PublicKey) throws NoPublicKeyPacketException, IOException;
	
	String sign(String data, PGPPrivateKey privateKey) throws PGPException, SignatureException, IOException;
	
	PGPPrivateKey extractPrivateKey(String stream, String password) throws PGPException, IOException;
}
