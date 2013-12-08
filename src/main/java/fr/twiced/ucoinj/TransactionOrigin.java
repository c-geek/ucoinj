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
}
