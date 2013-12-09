package fr.twiced.ucoinj.bean;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import fr.twiced.ucoinj.TransactionOrigin;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.exceptions.BadFormatException;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"issuer", "coinNumber", "sender", "number"})
})
@Access(AccessType.PROPERTY)
public class CoinEntry extends UCoinEntity<CoinId> implements Rawable {
	
	private Integer id;
	private CoinId coindId;
	private TransactionId transactionId;
	
	public CoinEntry() {
	}
	
	public CoinEntry(CoinId coindId) {
		super();
		this.coindId = coindId;
	}
	
	public CoinEntry(CoinId coindId, TransactionId transactionId) {
		super();
		this.coindId = coindId;
		this.transactionId = transactionId;
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

	@Embedded
	public TransactionId getTransactionId() {
		return transactionId;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", coindId.getJSON());
		map.put("transaction", transactionId.getJSON());
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

	public void setTransactionId(TransactionId transactionId) {
		this.transactionId = transactionId;
	}

	@Transient
	@Override
	public String getRaw() {
		return getRaw(coindId, transactionId);
	}

	public static String getRaw(CoinId cid, TransactionId tid) {
		StringBuffer sb = new StringBuffer(cid.toString());
		if (tid != null){
			sb.append(String.format(", %s", tid.getJSON()));
		}
		return sb.toString();
	}

	@Override
	public void parseFromRaw(String raw) throws BadFormatException {
		String generic = "([A-Z0-9]{40})-(\\d+)-(\\d)-(\\d+)-(A|F)-(\\d+)( ,([A-Z0-9]{40})-(\\d+))?";
		Pattern p = Pattern.compile(generic, Pattern.DOTALL);
		Matcher m = p.matcher(raw);
		if (m.matches()) {
			coindId = new CoinId();
			coindId.setIssuer(m.group(1));
			coindId.setCoinNumber(Integer.parseInt(m.group(2)));
			coindId.setCoinBase(Integer.parseInt(m.group(3)));
			coindId.setCoinPower(Integer.parseInt(m.group(4)));
			coindId.setOriginType(TransactionOrigin.getByShortName(m.group(5)));
			coindId.setOriginNumber(Integer.parseInt(m.group(6)));
			if (m.group(8) != null) {
				transactionId = new TransactionId();
				transactionId.setSender(m.group(8));
				transactionId.setNumber(Integer.parseInt(m.group(9)));
			}
		} else {
			throw new BadFormatException();
		}
	}
	
	@Transient
	public BigInteger getValue() {
		double coinValue = getCoindId().getCoinBase() * Math.pow(10, getCoindId().getCoinPower());
		return new BigInteger(new Integer(new Double(coinValue).intValue()).toString());
	}
}
