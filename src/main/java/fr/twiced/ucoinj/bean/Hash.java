package fr.twiced.ucoinj.bean;


public class Hash implements Merklable {

	private String hash;
	
	public Hash() {
	}
	
	public Hash(String hash) {
		this.hash = hash;
	}
	
	public Hash(Hashable hashable) {
		this.hash = hashable.getHash();
	}
	
	@Override
	public Object getJSON() {
		return hash;
	}

	@Override
	public String getHash() {
		return hash;
	}
	
	@Override
	public String toString() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
