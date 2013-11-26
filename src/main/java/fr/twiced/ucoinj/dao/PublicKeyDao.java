package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.PublicKey;

public interface PublicKeyDao extends GenericDao<PublicKey> {

	PublicKey getByFingerprint(String fpr);
	
	PublicKey getByKeyID(String keyID);

	List<PublicKey> lookup(String search);
}
