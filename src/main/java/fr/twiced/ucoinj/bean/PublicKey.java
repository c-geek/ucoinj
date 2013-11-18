package fr.twiced.ucoinj.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "publickey", uniqueConstraints = { @UniqueConstraint(columnNames = "fpr") })
public class PublicKey {

	private Integer id;
	private String fingerprint;
	
	public PublicKey() {
	}
	
	public PublicKey(String fingerprint) {
		super();
		this.fingerprint = fingerprint;
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
}
