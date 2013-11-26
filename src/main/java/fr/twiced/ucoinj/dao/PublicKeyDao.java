package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.PublicKey;

public interface PublicKeyDao extends GenericDao<PublicKey> {

	PublicKey getByFingerprint(String fpr);
	
	PublicKey getByKeyID(String keyID);
}
