package fr.twiced.ucoinj.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"amendment_id" , "signature_id"})
})
public class Vote {

	private Integer id;
	private Amendment amendment;
	private PublicKey publicKey;
	private Signature signature;
	
	public Vote() {
	}

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	public Integer getId() {
		return id;
	}

	@OneToOne(orphanRemoval = true)
	@JoinColumn(nullable = false, name = "amendment_id")
	public Amendment getAmendment() {
		return amendment;
	}

	@OneToOne(orphanRemoval = true)
	@JoinColumn(nullable = false, name = "signature_id")
	public Signature getSignature() {
		return signature;
	}

	@OneToOne(orphanRemoval = true)
	@JoinColumn(nullable = false, name = "pubkey_id")
	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setAmendment(Amendment amendment) {
		this.amendment = amendment;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
}
