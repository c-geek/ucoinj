package fr.twiced.ucoinj.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.util.encoders.Hex;

import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;

@Entity
@Table(name = "publickey", uniqueConstraints = { @UniqueConstraint(columnNames = "fpr") })
public class PublicKey {

	private Integer id;
	private String fingerprint;
	private String armored;
	private PGPPublicKey PGPPublicKey;
	
	public PublicKey() {
	}
	
	public PublicKey(String fingerprint) {
		super();
		this.fingerprint = fingerprint;
	}

	public PublicKey(List<PGPPublicKey> pubkeys) throws NoPublicKeyPacketException, IOException {
		super();
		if (pubkeys.isEmpty()) {
			throw new NoPublicKeyPacketException("error.packet.publickey.not.found");
		}
		// Fingerprint computation
		PGPPublicKey = pubkeys.get(0);
		byte[] twentyBytesFingerprint = PGPPublicKey.getFingerprint();
		byte[] fourtyHexBytesFingerprint = Hex.encode(twentyBytesFingerprint);
		fingerprint = new String(fourtyHexBytesFingerprint).toUpperCase();
		// Armored computation
		ByteArrayOutputStream armoredOut = new ByteArrayOutputStream();
		OutputStream out = new ArmoredOutputStream(armoredOut);
		for(PGPPublicKey key : pubkeys){
			out.write(key.getEncoded());
		}
		out.close();
		armored = new String(armoredOut.toString().toCharArray());
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "fpr", unique = true, nullable = false)
	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	@Transient
	public String getArmored() {
		return armored;
	}

	public void setArmored(String armored) {
		this.armored = armored;
	}

	@Transient
	public PGPPublicKey getPGPPublicKey() {
		return PGPPublicKey;
	}
}
