package fr.twiced.ucoinj;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.service.impl.PGPServiceImpl;


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
	private String PGPPassword = "";
	private String IPv4 = "";
	private Integer port = 8081;
	private String remoteHost = "";
	private String remoteIPv4 = "";
	private String remoteIPv6 = "";
	private Integer remotePort = 8081;
	private String kmanagement = "ALL";
	
	private String getFileName() {
		return String.format("%s.properties", currency);
	}
	
	public void save() throws IOException {
		save(getFileName());
	}
	
	public void save(String name) throws IOException {
		Properties prop = new Properties();
		prop.put("currency", currency);
		prop.put("privateKey", privateKey);
		prop.put("DBURL", DBURL);
		prop.put("DBUsername", DBUsername);
		prop.put("DBPassword", DBPassword);
		prop.put("PGPPassword", PGPPassword);
		prop.put("IPv4", IPv4);
		prop.put("port", port.toString());
		prop.put("remoteHost", remoteHost);
		prop.put("remoteIPv4", remoteIPv4);
		prop.put("remoteIPv6", remoteIPv6);
		prop.put("remotePort", remotePort.toString());
		prop.put("kmanagement", kmanagement);
		try (FileOutputStream out = new FileOutputStream(new File(name))) {
			prop.store(out, "");
		}
	}
	
	public void reset() throws IOException {
		reset(getFileName());
	}
	
	public static void reset(String name) throws IOException {
		Properties prop = new Properties();
		prop.put("currency", "");
		prop.put("privateKey", "");
		prop.put("DBURL", "");
		prop.put("DBUsername", "");
		prop.put("DBPassword", "");
		prop.put("PGPPassword", "");
		prop.put("IPv4", "");
		prop.put("port", "");
		prop.put("remoteHost", "");
		prop.put("remoteIPv4", "");
		prop.put("remoteIPv6", "");
		prop.put("remotePort", "");
		prop.put("kmanagement", "");
		try (FileOutputStream out = new FileOutputStream(new File(name))) {
			prop.store(out, "");
		}
	}
	
	public void load() throws IOException {
		load(getFileName());
	}
	
	public void load(String name) throws IOException {
		Properties prop = new Properties();
		File f = new File(name);
		if (f.exists()) {
			try (FileInputStream fis = new FileInputStream(f)) {
				prop.load(fis);
			}
			setCurrency((String) prop.get("currency"));
			setPrivateKey((String) prop.get("privateKey"));
			setDBURL((String) prop.get("DBURL"));
			setDBUsername((String) prop.get("DBUsername"));
			setDBPassword((String) prop.get("DBPassword"));
			setPGPPassword((String) prop.get("PGPPassword"));
			setIPv4((String) prop.get("IPv4"));
			setRemoteHost((String) prop.get("remoteHost"));
			setRemoteIPv4((String) prop.get("remoteIPv4"));
			setRemoteIPv6((String) prop.get("remoteIPv6"));
			setKmanagement((String) prop.get("kmanagement"));
			Integer port = prop.get("port") == null ? 8081 : Integer.valueOf(prop.get("port").toString());
			Integer remotePort = prop.get("remotePort") == null ? 8081 : Integer.valueOf(prop.get("remotePort").toString());
			setRemotePort(remotePort);
			setPort(port);
		}
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey == null ? "" : privateKey;
	}

	public String getDBURL() {
		return DBURL;
	}

	public void setDBURL(String dBURL) {
		DBURL = dBURL == null ? "" : dBURL;
	}

	public String getDBUsername() {
		return DBUsername;
	}

	public void setDBUsername(String dBUsername) {
		DBUsername = dBUsername == null ? "" : dBUsername;
	}

	public String getDBPassword() {
		return DBPassword;
	}

	public void setDBPassword(String dBPassword) {
		DBPassword = dBPassword == null ? "" : dBPassword;
	}

	public String getPGPPassword() {
		return PGPPassword;
	}

	public void setPGPPassword(String pGPPassword) {
		PGPPassword = pGPPassword == null ? "" : pGPPassword;
	}

	public String getIPv4() {
		return IPv4;
	}

	public String getKmanagement() {
		return kmanagement;
	}

	public void setKmanagement(String kmanagement) {
		this.kmanagement = kmanagement == null ? "" : kmanagement;
	}

	public void setIPv4(String iPv4) {
		IPv4 = iPv4 == null ? "" : iPv4;
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
		this.currency = currency == null ? "" : currency;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost == null ? "" : remoteHost;
	}

	public String getRemoteIPv4() {
		return remoteIPv4;
	}

	public void setRemoteIPv4(String remoteIPv4) {
		this.remoteIPv4 = remoteIPv4 == null ? "" : remoteIPv4;
	}

	public String getRemoteIPv6() {
		return remoteIPv6;
	}

	public void setRemoteIPv6(String remoteIPv6) {
		this.remoteIPv6 = remoteIPv6 == null ? "" : remoteIPv6;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}
	
	public boolean isManagingALLKeys() {
		return this.kmanagement.equals("ALL");
	}
	
	public PGPPrivateKey getPGPPrivateKey() throws PGPException, IOException {
		String privateKeyStream = GlobalConfiguration.getInstance().getPrivateKey();
		PGPPrivateKey privateKey = null;
		if(!(privateKeyStream == null || privateKeyStream.isEmpty())){
			String password = GlobalConfiguration.getInstance().getPGPPassword();
			privateKey = new PGPServiceImpl().extractPrivateKey(privateKeyStream, password);
		}
		return privateKey;
	}
	
	public PublicKey getPublicKey() throws PGPException, IOException, NoPublicKeyPacketException {
		PGPPrivateKey privateKey = getPGPPrivateKey();
		if (privateKey != null) {
			// Computes armored public key from private key
			ByteArrayOutputStream armoredOut = new ByteArrayOutputStream();
			OutputStream out = new ArmoredOutputStream(armoredOut);
			out.write(privateKey.getPublicKeyPacket().getEncoded());
			out.close();
			String armoredPubkey = armoredOut.toString();
			return new PGPServiceImpl().extractPublicKey(armoredPubkey);
		}
		return null;
	}
}
