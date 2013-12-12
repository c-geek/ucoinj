package fr.twiced.ucoinj.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.twiced.ucoinj.bean.id.KeyId;

@Entity
@Table(name = "key_table")
public class Key extends UCoinEntity<KeyId> implements Merklable {
	
	private Integer id;
	private String fingerprint;
	private Boolean managed;
	
	public Key() {
	}
	
	public Key(String fingerprint) {
		super();
		this.fingerprint = fingerprint;
	}

	@Id
	@GeneratedValue
	@Column(nullable = true, unique = true)
	public Integer getId() {
		return id;
	}

	@Column(nullable = true, unique = true)
	public String getFingerprint() {
		return fingerprint;
	}

	@Column(nullable = true)
	public Boolean getManaged() {
		return managed;
	}

	@Transient
	@Override
	public String getHash() {
		return fingerprint;
	}

	@Transient
	@Override
	public Object getJSON() {
		return fingerprint;
	}

	@Transient
	@Override
	public KeyId getNaturalId() {
		return new KeyId(fingerprint);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void setManaged(Boolean managed) {
		this.managed = managed;
	}
}
