package fr.twiced.ucoinj;

import org.bouncycastle.openpgp.PGPPrivateKey;

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

	private String currency = "";
	private String privateKey = "";
	private String DBURL = "";
	private String DBUsername = "root";
	private String DBPassword = "";
	private PGPPrivateKey PGPPrivateKey;
	private String PGPPassword;
	private String IPv4;
	private int port;
	private String remoteHost;
	private String remoteIPv4;
	private String remoteIPv6;
	private int remotePort;

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

	public PGPPrivateKey getPGPPrivateKey() {
		return PGPPrivateKey;
	}

	public void setPGPPrivateKey(PGPPrivateKey privateKey) {
		PGPPrivateKey = privateKey;
	}

	public String getPGPPassword() {
		return PGPPassword;
	}

	public void setPGPPassword(String pGPPassword) {
		PGPPassword = pGPPassword;
	}

	public String getIPv4() {
		return IPv4;
	}

	public void setIPv4(String iPv4) {
		IPv4 = iPv4;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemoteIPv4() {
		return remoteIPv4;
	}

	public void setRemoteIPv4(String remoteIPv4) {
		this.remoteIPv4 = remoteIPv4;
	}

	public String getRemoteIPv6() {
		return remoteIPv6;
	}

	public void setRemoteIPv6(String remoteIPv6) {
		this.remoteIPv6 = remoteIPv6;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
}
