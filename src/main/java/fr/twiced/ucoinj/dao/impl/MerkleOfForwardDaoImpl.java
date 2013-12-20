package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.ForwardDao;
import fr.twiced.ucoinj.dao.MerkleOfForwardDao;

@Repository
@Transactional
public class MerkleOfForwardDaoImpl extends GenericMultipleMerkleDaoImpl<Forward, KeyId> implements MerkleOfForwardDao {

	@Autowired
	private ForwardDao fwdDao;

	@Override
	public Forward getLeaf(String hash, KeyId natId) {
		return fwdDao.getByHash(hash);
	}

	@Override
	public Forward getNew(String hash) {
		Forward p = new Forward();
		p.setHash(hash);
		return p;
	}
}
