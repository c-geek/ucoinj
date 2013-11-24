package fr.twiced.ucoinj.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;

public class Signature {
		
	private ISignature sigObj;

	public Signature(String signatureStream) throws Exception {

		PGPObjectFactory pgpObjFactoryA = new PGPObjectFactory(new ArmoredInputStream(new ByteArrayInputStream(signatureStream.getBytes())));

		// Lecture de la donnée PGP
		Object obj = pgpObjFactoryA.nextObject();
		
		if (obj instanceof PGPCompressedData) {
			throw new PGPException("Handle only detached signatures");
//			// 1. Extrait la signature compressée
//			PGPCompressedData compressedData = (PGPCompressedData) obj;
//
//			// Get the signature from the file
//
//			// 2. Extrait la signature décompressée
//			PGPObjectFactory pgpObjFactory = new PGPObjectFactory(compressedData.getDataStream());
//			Object obj2 = pgpObjFactory.nextObject();
//			PGPOnePassSignatureList onePassSignatureList = (PGPOnePassSignatureList) obj2;
//			PGPOnePassSignature onePassSignature = onePassSignatureList.get(0);
//
//			// 3. Extrait les données littérales signées
//			Object obj3 = pgpObjFactory.nextObject();
//			PGPLiteralData pgpLiteralData = (PGPLiteralData) obj3;
//
//			// Get the signature from the written out file
//			Object obj4 = pgpObjFactory.nextObject();
//			PGPSignatureList p3 = (PGPSignatureList) obj4;
//			PGPSignature signature = p3.get(0);
//			sigObj = new AttachedSignature(onePassSignature, signature, pgpLiteralData);
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
	private interface ISignature {
		
		boolean verify(PGPPublicKey publicKey, String data) throws Exception;
		
		public String getIssuerKeyId();
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
			literalDataStream.close();
			boolean verified = signature.verify();
			return verified;
		}

		@Override
		public String getIssuerKeyId(){
			return Long.toHexString(signature.getKeyID());
		}
	}
}
