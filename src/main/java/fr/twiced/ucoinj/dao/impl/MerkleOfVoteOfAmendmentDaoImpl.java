package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.MerkleOfVoteOfAmendmentDao;

@Repository
@Transactional
public class MerkleOfVoteOfAmendmentDaoImpl extends GenericMultipleMerkleDaoImpl<Signature, AmendmentId> implements MerkleOfVoteOfAmendmentDao {

	@Autowired
	private AmendmentDao amendDao;

	@Override
	public Signature getLeaf(String hash, AmendmentId natId) {
		return amendDao.getVote(natId, hash);
	}

	@Override
	public Signature getNew(String hash) {
		Signature sig = new Signature();
		sig.setHash(hash);
		return sig;
	}
}
