package fr.twiced.ucoinj.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.SignatureException;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.pgp.PGPHelper;
import fr.twiced.ucoinj.service.PGPService;

@Service
public class PGPServiceImpl implements PGPService {

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
			return new Signature(base64signature).verify(originalDocument, pubkey);
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

	private class Signature {

		private PGPOnePassSignature onePassSignature;
		private PGPSignature signature;
		private String data;
		private PGPLiteralData pgpLiteralData;

		public Signature(String signatureStream) throws Exception {

			PGPObjectFactory pgpObjFactory = new PGPObjectFactory(new ArmoredInputStream(new ByteArrayInputStream(signatureStream.getBytes())));

			// 1. Extrait la signature compressée
			PGPCompressedData compressedData = (PGPCompressedData) pgpObjFactory.nextObject();

			// Get the signature from the file

			// 2. Extrait la signature décompressée
			pgpObjFactory = new PGPObjectFactory(compressedData.getDataStream());
			PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) pgpObjFactory.nextObject();
			onePassSignature = onePassSignatureList.get(0);

			// 3. Extrait les données littérales signées
			pgpLiteralData = (PGPLiteralData) pgpObjFactory.nextObject();

			// Get the signature from the written out file
			PGPSignatureList p3 = (PGPSignatureList) pgpObjFactory.nextObject();
			signature = p3.get(0);
		}

		public boolean verify(String originalData, PGPPublicKey publicKey) throws Exception {

			InputStream literalDataStream = pgpLiteralData.getInputStream();
			ByteArrayOutputStream literalDataOutputStream = new ByteArrayOutputStream();
			onePassSignature.initVerify(publicKey, "BC");

			int ch;
			while ((ch = literalDataStream.read()) >= 0) {
				// 4. Feed onePassStructure + literalData
				onePassSignature.update((byte) ch);
				literalDataOutputStream.write(ch);
			}
			literalDataOutputStream.close();
			data = new String(literalDataOutputStream.toByteArray());
			// Verify the two signatures
			if (onePassSignature.verify(signature)) {
				return true && data.equals(originalData);
			} else {
				return false;
			}
		}
		
		public String getIssuerKeyId(){
			return Long.toHexString(signature.getKeyID());
		}
	}
}
