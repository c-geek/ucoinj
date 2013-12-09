package fr.twiced.ucoinj;

public enum TransactionOrigin {

	AMENDMENT("A"), FUSION("F");
	
	private String code;

	private TransactionOrigin(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	public static TransactionOrigin getByShortName(String shortName) {
		if (shortName.equals("A")) {
			return AMENDMENT;
		}
		if (shortName.equals("F")) {
			return FUSION;
		}
		return null;
	}
}
