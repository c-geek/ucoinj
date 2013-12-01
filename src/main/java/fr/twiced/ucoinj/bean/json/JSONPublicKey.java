package fr.twiced.ucoinj.bean.json;

public class JSONPublicKey {

	private String email, comment, name, fingerprint, raw;

	public JSONPublicKey(String email, String comment, String name, String fingerprint, String raw) {
		super();
		this.email = email;
		this.comment = comment;
		this.name = name;
		this.fingerprint = fingerprint;
		this.raw = raw;
	}

	public String getEmail() {
		return email;
	}

	public String getComment() {
		return comment;
	}

	public String getName() {
		return name;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public String getRaw() {
		return raw;
	}
}