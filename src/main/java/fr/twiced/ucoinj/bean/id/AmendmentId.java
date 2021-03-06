package fr.twiced.ucoinj.bean.id;

import javax.persistence.Embeddable;

import fr.twiced.ucoinj.bean.NaturalId;

@Embeddable
public class AmendmentId implements NaturalId {

	private String hash;
	private int number;

	public AmendmentId() {
	}

	public AmendmentId(int number, String hash) {
		super();
		this.hash = hash;
		this.number = number;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return number + "-" + hash;
	}

}
