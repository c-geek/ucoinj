package fr.twiced.ucoinj.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.exceptions.BadFormatException;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
public class Peer implements Merklable, Rawable {

	private Integer id;
	private Integer version;
	private String currency;
	private String fingerprint;
	private String dns;
	private String ipv4;
	private String ipv6;
	private Integer port;
	private String hash;
	private Signature signature;
	
	public Peer() {
	}
	
	public Peer(String entryStream, Signature sig) throws BadFormatException {
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
	
	@Column(nullable = false, unique = true, length = 40)
	public String getFingerprint() {
		return fingerprint;
	}

	@Column(nullable = true, length = 255)
	public String getDns() {
		return dns;
	}

	@Column(nullable = true, length = 20)
	public String getIpv4() {
		return ipv4;
	}

	@Column(nullable = true, length = 100)
	public String getIpv6() {
		return ipv6;
	}

	@Column(nullable = false)
	public Integer getPort() {
		return port;
	}

	@OneToOne(optional = false)
	public Signature getSignature() {
		return signature;
	}

	@Override
	@Column(nullable = false)
	public String getHash() {
		if (hash == null) {
			hash = new Sha1(getRaw()).getHash();
		}
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

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}

	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		map.put("currency", currency);
		map.put("fingerprint", fingerprint);
		map.put("dns", dns);
		map.put("ipv4", ipv4);
		map.put("ipv6", ipv6);
		map.put("port", port);
		map.put("signature", signature.getArmored());
		return map;
	}

	@Transient
	@Override
	public String getRaw() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Version: %d", version) + CARRIAGE_RETURN);
		sb.append(String.format("Currency: %s", currency) + CARRIAGE_RETURN);
		sb.append(String.format("Fingerprint: %s", fingerprint) + CARRIAGE_RETURN);
		sb.append(String.format("Dns: %s", dns) + CARRIAGE_RETURN);
		sb.append(String.format("IPv6: %s", ipv4) + CARRIAGE_RETURN);
		sb.append(String.format("IPv6: %s", ipv6) + CARRIAGE_RETURN);
		sb.append(String.format("Port: %d", port) + CARRIAGE_RETURN);
		return sb.toString();
	}

	@Override
	public void parseFromRaw(String raw) throws BadFormatException {
		String generic = "Version: (\\d+)\r\n"
				+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
				+ "Fingerprint: ([A-Z0-9]{40})\r\n"
				+ "(Dns: (\\d+)\r\n)?"
				+ "(IPv4: ([0-9.]+)\r\n)?"
				+ "(IPv6: ([:a-fA-F0-9]+)\r\n)?"
				+ "(Port: (\\d+)\r\n)?";
		Pattern p = Pattern.compile(generic, Pattern.DOTALL);
		Matcher m = p.matcher(raw);
		if (m.matches()) {
			m.matches();
			version = Integer.parseInt(m.group(1));
			currency = m.group(2);
			fingerprint = m.group(3);
			if (m.group(4) != null)
				dns = m.group(5);
			if (m.group(6) != null)
				ipv4 = m.group(7);
			if (m.group(8) != null)
				ipv6 = m.group(9);
			if (m.group(10) != null)
				port = Integer.parseInt(m.group(11));
			hash = new Sha1(getRaw()).getHash();
		} else {
			throw new BadFormatException();
		}
	}
	
	@Transient
	public KeyId getKeyId() {
		return new KeyId(this.getFingerprint());
	}
}
