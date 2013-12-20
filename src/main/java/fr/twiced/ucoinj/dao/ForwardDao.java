package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface ForwardDao extends GenericDao<Forward> {

	public Forward getByKeyIds(KeyId from, KeyId to);
	
	public Forward getByHash(String hash);
}
