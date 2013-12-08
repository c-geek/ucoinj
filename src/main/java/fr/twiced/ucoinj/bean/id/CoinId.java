package fr.twiced.ucoinj.bean.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import fr.twiced.ucoinj.TransactionOrigin;
import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.NaturalId;

@Embeddable
public class CoinId implements NaturalId, Jsonable {

	private String issuer;
	private Integer coinNumber;
	private Integer coinBase;
	private Integer coinPower;
	private TransactionOrigin originType;
	private Integer originNumber;
	
	public CoinId() {
	}

	public CoinId(String issuer, Integer coinNumber, Integer coinBase, Integer coinPower, TransactionOrigin originType, Integer originNumber) {
		super();
		this.issuer = issuer;
		this.coinNumber = coinNumber;
		this.coinBase = coinBase;
		this.coinPower = coinPower;
		this.originType = originType;
		this.originNumber = originNumber;
	}

	@Column(nullable = false, length = 40)
	public String getIssuer() {
		return issuer;
	}

	@Column(nullable = false)
	public Integer getCoinNumber() {
		return coinNumber;
	}

	@Column(nullable = false)
	public Integer getCoinBase() {
		return coinBase;
	}

	@Column(nullable = false)
	public Integer getCoinPower() {
		return coinPower;
	}

	@Enumerated(EnumType.STRING)
	public TransactionOrigin getOriginType() {
		return originType;
	}

	@Column(nullable = false)
	public Integer getOriginNumber() {
		return originNumber;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public void setCoinNumber(Integer coinNumber) {
		this.coinNumber = coinNumber;
	}

	public void setCoinBase(Integer coinBase) {
		this.coinBase = coinBase;
	}

	public void setCoinPower(Integer coinPower) {
		this.coinPower = coinPower;
	}

	public void setOriginType(TransactionOrigin originType) {
		this.originType = originType;
	}

	public void setOriginNumber(Integer originNumber) {
		this.originNumber = originNumber;
	}

	@Transient
	@Override
	public Object getJSON() {
		return String.format("%s-%d-%d-%d-%s-%d", issuer, coinNumber, coinBase, coinPower, originType, originNumber);
	}

	@Override
	public String toString() {
		return getJSON().toString();
	}
}
