package fr.twiced.ucoinj.pgp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Formatter;

import fr.twiced.ucoinj.bean.Hashable;
import fr.twiced.ucoinj.exceptions.Sha1ConvertionException;

public class Sha1 implements Hashable {

	private String data;

	public Sha1(String data) {
		super();
		this.data = data != null ? data : "";
	}
	
	@Override
	public String toString() {
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA1", "BC");
			crypt.reset();
			crypt.update(data.getBytes());
			return byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new Sha1ConvertionException(e);
		}
	}
	
	private static String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}

	@Override
	public String getHash() {
		return this.toString().toUpperCase();
	}
}
