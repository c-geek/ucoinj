package fr.twiced.ucoinj;

public class GlobalConfiguration {
	
	private static GlobalConfiguration config = null;
	
	public static GlobalConfiguration getInstance(){
		if (config == null) {
			config = new GlobalConfiguration();
		}
		return config;
	}
	
	private GlobalConfiguration() {
	}

	private String privateKey = "";
	private String DBURL = "jdbc:mysql://localhost:3306/ucoinj";
	private String DBUsername = "root";
	private String DBPassword = "";

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getDBURL() {
		return DBURL;
	}

	public void setDBURL(String dBURL) {
		DBURL = dBURL;
	}

	public String getDBUsername() {
		return DBUsername;
	}

	public void setDBUsername(String dBUsername) {
		DBUsername = dBUsername;
	}

	public String getDBPassword() {
		return DBPassword;
	}

	public void setDBPassword(String dBPassword) {
		DBPassword = dBPassword;
	}
}
