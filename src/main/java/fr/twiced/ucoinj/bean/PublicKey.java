package fr.twiced.ucoinj.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.util.encoders.Hex;

import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.bean.json.JSONPublicKey;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;

@Entity
@Table(name = "publickey", uniqueConstraints = { @UniqueConstraint(columnNames = "fpr") })
public class PublicKey extends UCoinEntity<KeyId> implements Merklable {

	private Integer id;
	private String email;
	private String comment;
	private String name;
	private String fingerprint;
	private String armored;
	private PGPPublicKey PGPPublicKey;
	private Signature signature;
	
	public PublicKey() {
	}

	public PublicKey(List<PGPPublicKey> pubkeys, String armored) throws NoPublicKeyPacketException, IOException {
		super();
		if (pubkeys.isEmpty()) {
			throw new NoPublicKeyPacketException("error.packet.publickey.not.found");
		}
		// Fingerprint computation
		PGPPublicKey = pubkeys.get(0);
		byte[] twentyBytesFingerprint = PGPPublicKey.getFingerprint();
		byte[] fourtyHexBytesFingerprint = Hex.encode(twentyBytesFingerprint);
		fingerprint = new String(fourtyHexBytesFingerprint).toUpperCase();
		if (armored == null) {
			// Armored computation
			ByteArrayOutputStream armoredOut = new ByteArrayOutputStream();
			OutputStream out = new ArmoredOutputStream(armoredOut);
			for(PGPPublicKey key : pubkeys){
				out.write(key.getEncoded());
			}
			out.close();
			armored = new String(armoredOut.toString().toCharArray());
		} else {
			this.armored = armored;
		}
		Iterator<String> userIds = PGPPublicKey.getUserIDs();
		if (userIds.hasNext()) {
			String uid = userIds.next();
			String p3 = "^(.*) \\((.*)\\) <(.*)>$";
			if (uid.matches(p3)) {
				Pattern p = Pattern.compile(p3);
				Matcher m = p.matcher(uid);
				m.find();
				name = m.group(1);
				comment = m.group(2);
				email = m.group(3);
			}
		}
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
	
	@Column(name = "fpr", unique = true, nullable = false, precision = 40)
	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	@Column(name = "raw", nullable = false, columnDefinition = "TEXT")
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

	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "comment")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "signatureId", nullable = false)
	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	@Transient
	public Object getJSONObject() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("signature", signature.getArmored());
		map.put("key", new JSONPublicKey(email, comment, name, fingerprint, armored));
		return map;
	}

	@Transient
	@Override
	public String getHash() {
		return this.getFingerprint().toUpperCase();
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("fingerprint", this.fingerprint);
		map.put("pubkey", this.armored);
		map.put("signature", signature.getArmored());
		return map;
	}

	public void setPGPPublicKey(PGPPublicKey pGPPublicKey) {
		PGPPublicKey = pGPPublicKey;
	}

	@Transient
	@Override
	public KeyId getNaturalId() {
		return new KeyId(this.fingerprint);
	}
}
