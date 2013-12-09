package fr.twiced.ucoinj.bean;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.TransactionId;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"issuer", "coinNumber"})
})
@Access(AccessType.PROPERTY)
public class Coin extends UCoinEntity<CoinId> {
	
	private Integer id;
	private CoinId coindId;
	private Transaction transaction;
	private Key key;
	
	public Coin() {
	}

	public Coin(CoinId coindId, Transaction transaction, Key key) {
		super();
		this.coindId = coindId;
		this.transaction = transaction;
		this.key = key;
	}

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	public Integer getId() {
		return id;
	}

	@Embedded
	public CoinId getCoindId() {
		return coindId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	public Transaction getTransaction() {
		return transaction;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	public Key getKey() {
		return key;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", coindId.getJSON());
		map.put("transaction", transaction.getJSON());
		map.put("owner", key.getJSON());
		return map;
	}
	
	@Transient
	@Override
	public CoinId getNaturalId() {
		return coindId;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setCoindId(CoinId coindId) {
		this.coindId = coindId;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public static String getRaw(CoinId cid, TransactionId tid) {
		StringBuffer sb = new StringBuffer(cid.toString());
		if (tid != null){
			sb.append(String.format(", %s", tid.getJSON()));
		}
		return sb.toString();
	}
	
	@Transient
	public BigInteger getValue() {
		double coinValue = getCoindId().getCoinBase() * Math.pow(10, getCoindId().getCoinPower());
		return new BigInteger(new Integer(new Double(coinValue).intValue()).toString());
	}
	
	@Transient
	public String getCoinIdButIssuer() {
		return getCoindId().getJSON().toString().replace(getCoindId().getIssuer() + "-", "");
	}
	
	@Transient
	public String getOwner() {
		return getKey() != null ? getKey().getFingerprint() : null;
	}
}
