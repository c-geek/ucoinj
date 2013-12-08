package fr.twiced.ucoinj.bean.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.NaturalId;

@Embeddable
public class TransactionId implements NaturalId,Jsonable {

	private String sender;
	private Integer number;
	
	public TransactionId() {
	}
	
	public TransactionId(String sender, Integer number) {
		super();
		this.sender = sender;
		this.number = number;
	}



	@Column(nullable = true, length = 40)
	public String getSender() {
		return sender;
	}

	@Column(nullable = true)
	public Integer getNumber() {
		return number;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Transient
	@Override
	public Object getJSON() {
		return String.format("%s-%d", sender, number);
	}

	@Transient
	@Override
	public String toString() {
		return getJSON().toString();
	}
}
