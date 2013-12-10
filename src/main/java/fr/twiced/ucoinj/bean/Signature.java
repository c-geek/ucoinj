package fr.twiced.ucoinj.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;

import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
@Table(name = "signature")
public class Signature implements Merklable {

	private Integer id;
	private String armored;
	private String hash;
	private String issuer;
	private Date sigDate;
	private ISignature sigObj;

	public Signature() {
	}
	
	public Signature(String signatureStream) throws BadSignatureException {
		armored = signatureStream.replace("\r\n", "\n").replace("\n", "\r\n");
		sigDate = getSigObj().getSignatureDate();
		issuer = getSigObj().getIssuerKeyId().toUpperCase();
		hash = new Sha1(armored).getHash();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	@Column(nullable = false, columnDefinition = "TEXT")
	public String getArmored() {
		return armored;
	}
	
	@Column(nullable = false, length = 40)
	public String getIssuer() {
		return issuer;
	}

	@Column(nullable = false)
	public Date getSigDate() {
		return sigDate;
	}

	@Transient
	public String getIssuerKeyId() throws BadSignatureException{
		return getSigObj().getIssuerKeyId();
	}

	@Transient
	public ISignature getSigObj() throws BadSignatureException{
		try{
			if(sigObj == null){
				PGPObjectFactory pgpObjFactoryA = new PGPObjectFactory(new ArmoredInputStream(new ByteArrayInputStream(armored.getBytes())));
	
				// Lecture de la donnée PGP
				Object obj = pgpObjFactoryA.nextObject();
				
				if (obj instanceof PGPCompressedData) {
					throw new BadSignatureException("Handle only detached signatures");
				} else if (obj instanceof PGPSignatureList) {
					PGPSignatureList p3 = (PGPSignatureList) obj;
					PGPSignature signature = p3.get(0);
					sigObj = new DetachedSignature(signature);
				}
			}
			return sigObj;
		} catch (BadSignatureException e) {
			throw e;
		} catch (Exception e) {
			throw new BadSignatureException("Error while verifying signature");
		}
	}
	
	public boolean isMoreRecentThan(Signature otherSig){
		return sigDate.after(otherSig.getSigDate());
	}
	
	public boolean isLessRecentThan(Signature otherSig){
		return sigDate.before(otherSig.getSigDate());
	}

	public boolean verify(PublicKey publicKey, String data) throws BadSignatureException {
		try {
			return getSigObj().verify(publicKey.getPGPPublicKey(), data);
		} catch (BadSignatureException e) {
			throw e;
		} catch (Exception e) {
			throw new BadSignatureException("Error while verifying signature");
		}
	}
	
	private interface ISignature {
		
		boolean verify(PGPPublicKey publicKey, String data) throws Exception;
		public String getIssuerKeyId();
		public Date getSignatureDate();
	}
	
	private class DetachedSignature implements ISignature {

		private PGPSignature signature;

		public DetachedSignature(PGPSignature sig) throws Exception {
			this.signature = sig;
		}

		@Override
		public boolean verify(PGPPublicKey publicKey, String data) throws NoSuchProviderException, PGPException, SignatureException, IOException {
			InputStream literalDataStream = new ByteArrayInputStream(data.getBytes());
			signature.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey);
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

		@Override
		public Date getSignatureDate() {
			return signature.getCreationTime();
		}
	}

	@Override
	public String getHash() {
		return hash;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("issuer", getIssuer());
		map.put("signature", getArmored());
		return map;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setArmored(String armored) {
		this.armored = armored;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public void setSigDate(Date sigDate) {
		this.sigDate = sigDate;
	}

	public void setSigObj(ISignature sigObj) {
		this.sigObj = sigObj;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
