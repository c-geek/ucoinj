package fr.twiced.ucoinj.service.impl;

import java.security.Security;
import java.security.SignatureException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;
import fr.twiced.ucoinj.pgp.PGPHelper;
import fr.twiced.ucoinj.service.PGPService;

@Service
public class PGPServiceImpl implements PGPService {
	
	public PGPServiceImpl() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider ());
	}

	@Override
	public PGPPublicKey extractPubkey(String base64stream) throws NoPublicKeyPacketException {
		List<PGPPublicKey> pubkeys = PGPHelper.extract(base64stream);
		if (pubkeys.isEmpty()) {
			throw new NoPublicKeyPacketException("error.packet.publickey.not.found");
		}
		return pubkeys.get(0);
	}

	@Override
	public boolean verify(String originalDocument, String base64signature, PGPPublicKey pubkey) throws Exception {
		try {
			return new Signature(base64signature).verify(pubkey, originalDocument);
		} catch (SignatureException | PGPException e) {
			throw new BadSignatureException(e);
		}
	}

	@Override
	public String extractIssuer(String base64signature) throws Exception {
		return new Signature(base64signature).getIssuerKeyId();
	}

	@Override
	public String extractFingerprint(PGPPublicKey publicKey) {
		byte[] twentyBytesFingerprint = publicKey.getFingerprint();
		byte[] fourtyHexBytesFingerprint = Hex.encode(twentyBytesFingerprint);
		return new String(fourtyHexBytesFingerprint).toUpperCase();
	}

	@Override
	public String extractFingerprint(String base64PublicKey) throws NoPublicKeyPacketException {
		return extractFingerprint(extractPubkey(base64PublicKey));
	}

	@Override
	public boolean verify(String base64signature, PGPPublicKey pubkey) throws BadSignatureException, NoPublicKeyPacketException,
			NoSignaturePacketException, Exception {
		try {
			return new Signature(base64signature).verify(pubkey, "");
		} catch (SignatureException | PGPException e) {
			throw new BadSignatureException(e);
		}
	}
}
