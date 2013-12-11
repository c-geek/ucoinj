package fr.twiced.ucoinj.bean;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.exceptions.BadFormatException;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"sender", "number"})
})
public class Transaction extends UCoinEntity<TransactionId> implements Merklable,Rawable {
	
	private Integer id;
	private String hash;
	private Integer version;
	private String currency;
	private String sender;
	private Integer number;
	private String previousHash;
	private String recipient;
	private TransactionType type;
	private String comment;
	private List<CoinEntry> coins;
	private Signature signature;
	
	public Transaction() {
	}
	
	public Transaction(String raw, Signature sig) throws BadFormatException {
		this.signature = sig;
		parseFromRaw(raw.replace("\r\n", "\n").replace("\n", "\r\n"));
	}

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	public Integer getId() {
		return id;
	}
	
	@Column(nullable = false)
	public Integer getVersion() {
		return version;
	}

	@Column(nullable = false, length = 255)
	public String getCurrency() {
		return currency;
	}

	@Column(nullable = false, length = 40)
	public String getSender() {
		return sender;
	}
	
	@Column(nullable = false)
	public Integer getNumber() {
		return number;
	}

	@Column(nullable = true, length = 40)
	public String getPreviousHash() {
		return previousHash;
	}

	@Column(nullable = false, length = 40)
	public String getRecipient() {
		return recipient;
	}

	@Enumerated(EnumType.STRING)
	public TransactionType getType() {
		return type;
	}

	@Column(nullable = false, columnDefinition = "TEXT")
	public String getComment() {
		return comment;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public List<CoinEntry> getCoins() {
		return coins;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public Signature getSignature() {
		return signature;
	}

	@Override
	@Column(nullable = false, length = 40)
	public String getHash() {
		return hash;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("version", version);
		map.put("currency", currency);
		map.put("sender", sender);
		map.put("number", number);
		map.put("recipient", recipient);
		map.put("type", type);
		List<Map<String, String>> listOfCoins = new ArrayList<>();
		List<CoinEntry> theCoins = getCoins();
		for (CoinEntry c : theCoins) {
			Map<String, String> coinValues = new HashMap<>();
			coinValues.put("id", c.getCoindId().getJSON().toString());
			coinValues.put("transaction_id", c.getTransactionId() != null ? c.getTransactionId().getJSON().toString() : "");
			listOfCoins.add(coinValues);
		}
		map.put("coins", listOfCoins);
		map.put("comment", comment);
		return map;
	}

	@Transient
	public Object getJSONObject() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("signature", getSignature().getArmored());
		map.put("transaction", getJSON());
		map.put("raw", getRaw());
		return map;
	}

	@Transient
	@Override
	public TransactionId getNaturalId() {
		return new TransactionId(sender, number);
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setCoins(List<CoinEntry> coins) {
		this.coins = coins;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	@Transient
	@Override
	public String getRaw() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Version: %d", version) + CARRIAGE_RETURN);
		sb.append(String.format("Currency: %s", currency) + CARRIAGE_RETURN);
		sb.append(String.format("Sender: %s", sender) + CARRIAGE_RETURN);
		sb.append(String.format("Number: %d", number) + CARRIAGE_RETURN);
		if (number > 0)
			sb.append(String.format("PreviousHash: %s", previousHash) + CARRIAGE_RETURN);
		sb.append(String.format("Recipient: %s", recipient) + CARRIAGE_RETURN);
		sb.append(String.format("Type: %s", type.name()) + CARRIAGE_RETURN);
		sb.append("Coins:" + CARRIAGE_RETURN);
		List<CoinEntry> coins = getCoins();
		for (CoinEntry c : coins) {
			sb.append(c.getRaw());
			sb.append(CARRIAGE_RETURN);
		}
		sb.append(String.format("Comment:" + CARRIAGE_RETURN +"%s", comment));
		return sb.toString();
	}

	@Override
	public void parseFromRaw(String raw) throws BadFormatException {
		String generic = "Version: (\\d+)\r\n"
				+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
				+ "Sender: ([A-Z0-9]{40})\r\n"
				+ "Number: (0|(\\d+)\r\n"
				+ "PreviousHash: ([A-Z0-9]{40}))\r\n"
				+ "Recipient: ([A-Z0-9]{40})\r\n"
				+ "Type: (TRANSFER|ISSUANCE|FUSION)\r\n"
				+ "Coins:\r\n"
				+ "((([A-Z0-9]{40}-\\d+-\\d-\\d+-[AF]-\\d+)(, [A-Z0-9]{40}-\\d+)?)\r\n)*"
				+ "Comment:\r\n"
				+ ".*";
		Pattern p = Pattern.compile(generic, Pattern.DOTALL);
		Matcher m = p.matcher(raw);
		if (m.matches()) {
			String extractPattern = "Version: (\\d+)\r\n"
					+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
					+ "Sender: ([A-Z0-9]{40})\r\n"
					+ "Number: (\\d+)\r\n"
					+ "(PreviousHash: ([A-Z0-9]{40})\r\n)?"
					+ "Recipient: ([A-Z0-9]{40})\r\n"
					+ "Type: (TRANSFER|ISSUANCE|FUSION)\r\n"
					+ "Coins:\r\n"
					+ "(.*)"
					+ "Comment:\r\n"
					+ "(.*)";
			p = Pattern.compile(extractPattern, Pattern.DOTALL);
			m = p.matcher(raw);
			m.matches();
			version = Integer.parseInt(m.group(1));
			currency = m.group(2);
			sender = m.group(3);
			number = Integer.parseInt(m.group(4));
			previousHash = m.group(6);
			recipient = m.group(7);
			type = TransactionType.valueOf(m.group(8));
			String[] coinsSplit = m.group(9).split("\r\n");
			coins = new ArrayList<>();
			for (String s : coinsSplit) {
				if (!s.equals("")) {
					CoinEntry c = new CoinEntry();
					c.parseFromRaw(s);
					coins.add(c);
				}
			}
			comment = m.group(10);
		} else {
			throw new BadFormatException();
		}
		this.hash = new Sha1(getRaw() + this.getSignature().getArmored()).getHash();
	}

	@Transient
	public boolean isFullFilled() {
		return version != null
				&& currency != null
				&& sender != null
				&& number != null
				&& (number == 0 || previousHash != null)
				&& recipient != null
				&& type != null
				&& coins != null && !coins.isEmpty()
				&& comment != null;
	}

	@Transient
	public BigInteger getValue() {
		BigInteger sum = BigInteger.ZERO;
		ListIterator<CoinEntry> iter = getCoins().listIterator(type.equals(TransactionType.FUSION) ? 1 : 0);
		while (iter.hasNext()) {
			sum = sum.add(iter.next().getValue());
		}
		return sum;
	}

	@Transient
	public CoinEntry getCoinEntry(CoinId coinId) {
		CoinEntry ce = null;
		Iterator<CoinEntry> iter = coins.iterator();
		while (iter.hasNext() && ce == null) {
			CoinEntry cei = iter.next();
			boolean sameIssuer = cei.getCoindId().getIssuer().equals(coinId.getIssuer());
			boolean sameCoinNumber = cei.getCoindId().getCoinNumber().equals(coinId.getCoinNumber());
			if (sameIssuer && sameCoinNumber) {
				ce = cei;
			}
		}
		return ce;
	}
}
