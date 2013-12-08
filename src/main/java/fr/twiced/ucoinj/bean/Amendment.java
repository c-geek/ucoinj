package fr.twiced.ucoinj.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.exceptions.BadFormatException;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"number" , "hash"})
})
public class Amendment extends UCoinEntity<AmendmentId> implements Jsonable,Hashable,Rawable {

	private Integer id;
	private String hash;
	private Boolean promoted;
	private Boolean current;
	private Integer version;
	private String currency;
	private Integer number;
	private Long generatedOn;
	private Integer dividend;
	private Integer coinMinimalPower;
	private Integer nextRequiredVotes;
	private String previousHash;
	private String previousVotesRoot;
	private Integer previousVotesCount;
	private String membersRoot;
	private Integer membersCount;
	private List<String> membersChanges;
	private String votersRoot;
	private Integer votersCount;
	private List<String> votersChanges;
	private List<Vote> votes;

	public Amendment() {
	}
	
	public Amendment(String amendment) throws BadFormatException {
		this.parseFromRaw(amendment);
		this.hash = new Sha1(amendment).toString().toUpperCase();
		this.current = false;
		this.promoted = false;
	}

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	public Integer getId() {
		return id;
	}

	@Override
	@Column(nullable = false)
	public String getHash() {
		return hash;
	}

	@Column(nullable = false)
	public Boolean getPromoted() {
		return promoted;
	}

	@Column(nullable = false)
	public Boolean getCurrent() {
		return current;
	}

	@Column(nullable = false)
	public Integer getVersion() {
		return version;
	}

	@Column(nullable = false, length = 255)
	public String getCurrency() {
		return currency;
	}

	@Column(nullable = false)
	public Integer getNumber() {
		return number;
	}

	@Column(nullable = false)
	public Long getGeneratedOn() {
		return generatedOn;
	}

	@Column
	public Integer getDividend() {
		return dividend;
	}

	@Column(nullable = false)
	public Integer getNextRequiredVotes() {
		return nextRequiredVotes;
	}

	@Column
	public Integer getCoinMinimalPower() {
		return coinMinimalPower;
	}

	@Column(length = 40)
	public String getPreviousHash() {
		return previousHash;
	}

	@Column(length = 40)
	public String getPreviousVotesRoot() {
		return previousVotesRoot;
	}

	@Column
	public Integer getPreviousVotesCount() {
		return previousVotesCount;
	}

	@Column(nullable = false, length = 40)
	public String getMembersRoot() {
		return membersRoot;
	}

	@Column(nullable = false)
	public Integer getMembersCount() {
		return membersCount;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "members_changes")
	@OrderBy
	public List<String> getMembersChanges() {
		return membersChanges;
	}

	@Column(nullable = false, length = 40)
	public String getVotersRoot() {
		return votersRoot;
	}

	@Column(nullable = false)
	public Integer getVotersCount() {
		return votersCount;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "voters_changes")
	@OrderBy
	public List<String> getVotersChanges() {
		return votersChanges;
	}

	@OneToMany(cascade = CascadeType.ALL)
	public List<Vote> getVotes() {
		return votes;
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

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setGeneratedOn(Long generatedOn) {
		this.generatedOn = generatedOn;
	}

	public void setDividend(Integer dividend) {
		this.dividend = dividend;
	}

	public void setCoinMinimalPower(Integer coinMinimalPower) {
		this.coinMinimalPower = coinMinimalPower;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public void setPreviousVotesRoot(String previousVotesRoot) {
		this.previousVotesRoot = previousVotesRoot;
	}

	public void setPreviousVotesCount(Integer previousVotesCount) {
		this.previousVotesCount = previousVotesCount;
	}

	public void setMembersRoot(String membersRoot) {
		this.membersRoot = membersRoot;
	}

	public void setMembersCount(Integer membersCount) {
		this.membersCount = membersCount;
	}

	public void setMembersChanges(List<String> membersChanges) {
		this.membersChanges = membersChanges;
	}

	public void setVotersRoot(String votersRoot) {
		this.votersRoot = votersRoot;
	}

	public void setVotersCount(Integer votersCount) {
		this.votersCount = votersCount;
	}

	public void setVotersChanges(List<String> votersChanges) {
		this.votersChanges = votersChanges;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setPromoted(Boolean promoted) {
		this.promoted = promoted;
	}

	public void setCurrent(Boolean current) {
		this.current = current;
	}

	public void setNextRequiredVotes(Integer nextRequiredVotes) {
		this.nextRequiredVotes = nextRequiredVotes;
	}

	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}
	
	@Transient
	public AmendmentId getNaturalId() {
		return new AmendmentId(number, hash);
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("version", version);
		map.put("currency", currency);
		map.put("number", number);
		map.put("generated", generatedOn);
		map.put("nextVotes", nextRequiredVotes);
		if (dividend != null)
			map.put("dividend", dividend);
		if (coinMinimalPower != null)
			map.put("coinMinPower", coinMinimalPower);
		if (previousHash != null)
			map.put("previousHash", previousHash);
		if (previousVotesRoot != null)
			map.put("previousVotesRoot", previousVotesRoot);
		if (previousVotesCount != null)
			map.put("previousVotesCount", previousVotesCount);
		map.put("votersRoot", votersRoot);
		map.put("votersCount", votersCount);
		map.put("votersChanges", votersChanges);
		map.put("membersRoot", membersRoot);
		map.put("membersCount", membersCount);
		map.put("raw", getRaw());
		return map;
	}

	@Transient
	@Override
	public String getRaw() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("Version: %d", version) + CARRIAGE_RETURN);
		sb.append(String.format("Currency: %s", currency) + CARRIAGE_RETURN);
		sb.append(String.format("Number: %d", number) + CARRIAGE_RETURN);
		sb.append(String.format("GeneratedOn: %d", generatedOn) + CARRIAGE_RETURN);
		if (dividend != null)
			sb.append(String.format("UniversalDividend: %d", dividend) + CARRIAGE_RETURN);
		if (coinMinimalPower != null)
			sb.append(String.format("CoinMinimalPower: %d", coinMinimalPower) + CARRIAGE_RETURN);
		sb.append(String.format("NextRequiredVotes: %d", nextRequiredVotes) + CARRIAGE_RETURN);
		if (previousHash != null)
			sb.append(String.format("PreviousHash: %s", previousHash) + CARRIAGE_RETURN);
		if (previousVotesRoot != null)
			sb.append(String.format("PreviousVotesRoot: %s", previousVotesRoot) + CARRIAGE_RETURN);
		if (previousVotesCount != null)
			sb.append(String.format("PreviousVotesCount: %d", previousVotesCount) + CARRIAGE_RETURN);
		sb.append(String.format("MembersRoot: %s", membersRoot) + CARRIAGE_RETURN);
		sb.append(String.format("MembersCount: %d", membersCount) + CARRIAGE_RETURN);
		sb.append("MembersChanges:" + CARRIAGE_RETURN);
		for (String change : this.getMembersChanges()) {
			sb.append(change + CARRIAGE_RETURN);
		}
		sb.append(String.format("VotersRoot: %s", votersRoot) + CARRIAGE_RETURN);
		sb.append(String.format("VotersCount: %d", votersCount) + CARRIAGE_RETURN);
		sb.append("VotersChanges:" + CARRIAGE_RETURN);
		for (String change : this.getVotersChanges()) {
			sb.append(change + CARRIAGE_RETURN);
		}
		return sb.toString();
	}

	@Override
	public void parseFromRaw(String raw) throws BadFormatException {
		String generic = "Version: (\\d+)\r\n"
				+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
				+ "Number: (\\d+)\r\n"
				+ "GeneratedOn: (\\d+)\r\n"
				+ "(UniversalDividend: (\\d+)\r\n)?"
				+ "(CoinMinimalPower: (\\d+)\r\n)?"
				+ "NextRequiredVotes: (\\d+)\r\n"
				+ "(PreviousHash: ([A-Z0-9]{40})\r\n"
				+ "PreviousVotesRoot: ([A-Z0-9]{40})\r\n"
				+ "PreviousVotesCount: (\\d+)\r\n)?"
				+ "MembersRoot: ([A-Z0-9]{40})\r\n"
				+ "MembersCount: (\\d+)\r\n"
				+ "MembersChanges:\r\n"
				+ "(((-|\\+)([A-Z0-9]{40})\r\n)*)"
				+ "VotersRoot: ([A-Z0-9]{40})\r\n"
				+ "VotersCount: (\\d+)\r\n"
				+ "VotersChanges:\r\n"
				+ "(((-|\\+)([A-Z0-9]{40})\r\n)*)";
		if (raw.matches(generic)) {
			String requiredFields = ""
					+ "Version: (\\d+)\r\n"
					+ "Currency: ([a-zA-Z0-9 -_]+)\r\n"
					+ "Number: (\\d+)\r\n"
					+ "GeneratedOn: (\\d+)\r\n"
					+ "(UniversalDividend: (\\d+)\r\n)?"
					+ "(CoinMinimalPower: (\\d+)\r\n)?"
					+ "NextRequiredVotes: (\\d+)\r\n"
					+ "(PreviousHash: ([A-Z0-9]{40})\r\n"
					+ "PreviousVotesRoot: ([A-Z0-9]{40})\r\n"
					+ "PreviousVotesCount: (\\d+)\r\n)?"
					+ "MembersRoot: ([A-Z0-9]{40})\r\n"
					+ "MembersCount: (\\d+)\r\n"
					+ "MembersChanges:\r\n(.*)"
					+ "VotersRoot: ([A-Z0-9]{40})\r\n"
					+ "VotersCount: (\\d+)\r\n"
					+ "VotersChanges:\r\n(.*)";
			Pattern p = Pattern.compile(requiredFields, Pattern.DOTALL);
			Matcher m = p.matcher(raw);
			m.matches();
			version = Integer.parseInt(m.group(1));
			currency = m.group(2);
			number = Integer.parseInt(m.group(3));
			generatedOn = Long.parseLong(m.group(4));
			if (m.group(5) != null)
				dividend = Integer.parseInt(m.group(6));
			if (m.group(7) != null)
				coinMinimalPower = Integer.parseInt(m.group(8));
			nextRequiredVotes = Integer.parseInt(m.group(9));
			if (m.group(10) != null) {
				previousHash = m.group(11);
				previousVotesRoot = m.group(12);
				previousVotesCount = Integer.parseInt(m.group(13));
			}
			membersRoot = m.group(14);
			membersCount = Integer.parseInt(m.group(15));
			votersRoot = m.group(17);
			votersCount = Integer.parseInt(m.group(18));
			String mchanges = m.group(16);
			String vchanges = m.group(19);
			String[] msplit = mchanges.split("\r\n");
			membersChanges = new ArrayList<>();
			for (String s : msplit) {
				if (!s.equals(""))
					membersChanges.add(s);
			}
			String[] vsplit = vchanges.split("\r\n");
			votersChanges = new ArrayList<>();
			for (String s : vsplit) {
				if (!s.equals(""))
					votersChanges.add(s);
			}
		} else {
			throw new BadFormatException();
		}
	}

}
