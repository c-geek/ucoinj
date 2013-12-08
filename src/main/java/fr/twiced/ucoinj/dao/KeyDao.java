package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface KeyDao extends GenericDao<Key> {

	public boolean isManaged(KeyId natId);
	
	public Key getByKeyId(KeyId natId);
}
