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
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;
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

	private interface ISignature {
		
		boolean verify(PGPPublicKey publicKey, String data) throws Exception;
		
		public String getIssuerKeyId();
	}

	private class Signature {
		
		private ISignature sigObj;

		public Signature(String signatureStream) throws Exception {

			PGPObjectFactory pgpObjFactoryA = new PGPObjectFactory(new ArmoredInputStream(new ByteArrayInputStream(signatureStream.getBytes())));

			// Lecture de la donnée PGP
			Object obj = pgpObjFactoryA.nextObject();
			
			if (obj instanceof PGPCompressedData) {
				// 1. Extrait la signature compressée
				PGPCompressedData compressedData = (PGPCompressedData) obj;

				// Get the signature from the file

				// 2. Extrait la signature décompressée
				PGPObjectFactory pgpObjFactory = new PGPObjectFactory(compressedData.getDataStream());
				Object obj2 = pgpObjFactory.nextObject();
				PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) obj2;
				PGPOnePassSignature onePassSignature = onePassSignatureList.get(0);

				// 3. Extrait les données littérales signées
				Object obj3 = pgpObjFactory.nextObject();
				PGPLiteralData pgpLiteralData = (PGPLiteralData) obj3;

				// Get the signature from the written out file
				Object obj4 = pgpObjFactory.nextObject();
				PGPSignatureList p3 = (PGPSignatureList) obj4;
				PGPSignature signature = p3.get(0);
				sigObj = new AttachedSignature(onePassSignature, signature, pgpLiteralData);
			} else if (obj instanceof PGPSignatureList) {
				PGPSignatureList p3 = (PGPSignatureList) obj;
				PGPSignature signature = p3.get(0);
				sigObj = new DetachedSignature(signature);
			}
		}

		public boolean verify(String originalData, PGPPublicKey publicKey, String data) throws Exception {
			return verify(publicKey, data) && data.equals(originalData);
		}

		public boolean verify(PGPPublicKey publicKey, String data) throws Exception {
			return sigObj.verify(publicKey, data);
		}
		
		public String getIssuerKeyId(){
			return sigObj.getIssuerKeyId();
		}
	}

	private class AttachedSignature implements ISignature {

		private PGPOnePassSignature onePassSignature;
		private PGPSignature signature;
		private PGPLiteralData pgpLiteralData;

		public AttachedSignature(PGPOnePassSignature onePassSignature, PGPSignature signature, PGPLiteralData pgpLiteralData) {
			super();
			this.onePassSignature = onePassSignature;
			this.signature = signature;
			this.pgpLiteralData = pgpLiteralData;
		}

		@Override
		public boolean verify(PGPPublicKey publicKey, String data) throws Exception {
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
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getIssuerKeyId(){
			return Long.toHexString(signature.getKeyID());
		}
	}

	private class DetachedSignature implements ISignature {

		private PGPSignature signature;

		public DetachedSignature(PGPSignature sig) throws Exception {
			this.signature = sig;
		}

		@Override
		public boolean verify(PGPPublicKey publicKey, String data) throws Exception {
			InputStream literalDataStream = new ByteArrayInputStream(data.getBytes());
			signature.initVerify(publicKey, "BC");

			int ch;
			while ((ch = literalDataStream.read()) >= 0) {
				// 4. Feed onePassStructure + literalData
				signature.update((byte) ch);
			}
			return signature.verify();
		}

		@Override
		public String getIssuerKeyId(){
			return Long.toHexString(signature.getKeyID());
		}
	}
}
