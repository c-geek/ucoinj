package fr.twiced.ucoinj.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import fr.twiced.ucoinj.ForwardType;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.exceptions.BadFormatException;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"fromFPR", "toFPR"})
})
public class Forward implements Merklable, Rawable {

	private Integer id;
	private Integer version;
	private String currency;
	private String from;
	private String to;
	private String hash;
	private ForwardType forward;
	private List<String> keys;
	private Signature signature;
	
	public Forward() {
		keys = new ArrayList<>();
	}
	
	public Forward(String entryStream, Signature sig) throws BadFormatException {
		this();
		this.parseFromRaw(entryStream);
		this.setSignature(sig);
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

	@Column(nullable = false, length = 50)
	public String getCurrency() {
		return currency;
	}
	
	@Transient
	public String getFingerprint() {
		return from;
	}

	@Column(nullable = false, length = 40, name = "fromFPR")
	public String getFrom() {
		return from;
	}

	@Column(nullable = false, length = 40, name = "toFPR")
	public String getTo() {
		return to;
	}

	@Enumerated(EnumType.STRING)
	public ForwardType getForward() {
		return forward;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@Column(name = "fingerprints")
	@CollectionTable(name = "forward_keys")
	@OrderBy
	public List<String> getKeys() {
		return keys;
	}

	@OneToOne(optional = false)
	public Signature getSignature() {
		return signature;
	}

	@Override
	public String getHash() {
		return hash;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setForward(ForwardType forward) {
		this.forward = forward;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		map.put("currency", currency);
		map.put("from", from);
		map.put("to", to);
		map.put("forward", forward.name());
		if (forward.equals(ForwardType.KEYS)) {
			map.put("key", keys);
		}
		return map;
	}

	@Transient
	@Override
	public String getRaw() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Version: %d", version) + CARRIAGE_RETURN);
		sb.append(String.format("Currency: %s", currency) + CARRIAGE_RETURN);
		sb.append(String.format("From: %s", from) + CARRIAGE_RETURN);
		sb.append(String.format("To: %s", to) + CARRIAGE_RETURN);
		sb.append(String.format("Forward: %s", forward.name()) + CARRIAGE_RETURN);
		if (forward.equals(ForwardType.KEYS)) {
			sb.append("Keys:" + CARRIAGE_RETURN);
			for (String k : keys) {
				sb.append(k + CARRIAGE_RETURN);
			}
		}
		return sb.toString();
	}

	@Override
	public void parseFromRaw(String raw) throws BadFormatException {
		String generic = "Version: (\\d+)\r\n"
				+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
				+ "From: ([A-Z0-9]{40})\r\n"
				+ "To: ([A-Z0-9]{40})\r\n"
				+ "Forward: (ALL\r\n|(KEYS\r\nKeys:\r\n([A-Z0-9]{40}\r\n)*))";
		Pattern p = Pattern.compile(generic, Pattern.DOTALL);
		Matcher m = p.matcher(raw);
		if (m.matches()) {
			m.matches();
			version = Integer.parseInt(m.group(1));
			currency = m.group(2);
			from = m.group(3);
			to = m.group(4);
			if (m.group(5).startsWith(ForwardType.ALL.name())) {
				forward = ForwardType.ALL;
			} else {
				forward = ForwardType.KEYS;
				keys = new ArrayList<>();
				String[] split = m.group(6).split("\r\n");
				for (int i = 2; i < split.length; i++) {
					keys.add(split[i]);
				}
			}
			hash = new Sha1(getRaw()).getHash();
		} else {
			throw new BadFormatException();
		}
	}
	
	@Transient
	public KeyId getFromKeyId() {
		return new KeyId(this.getFrom());
	}
	
	@Transient
	public KeyId getToKeyId() {
		return new KeyId(this.getTo());
	}
}
