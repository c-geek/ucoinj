package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface PeerDao extends GenericDao<Peer> {

	public Peer getByKeyId(KeyId natId);
}
