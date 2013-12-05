package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.MerkleOfSignatureOfAmendmentDao;

@Repository
@Transactional
public class MerkleOfSignatureOfAmendmentDaoImpl extends GenericMultipleMerkleDaoImpl<Signature, AmendmentId> implements MerkleOfSignatureOfAmendmentDao {

	@Autowired
	private AmendmentDao amendDao;

	@Override
	public Signature getLeaf(String hash, AmendmentId natId) {
		return amendDao.getSignature(natId, hash);
	}

	@Override
	public Signature getNew(String hash) {
		Signature sig = new Signature();
		sig.setHash(hash);
		return sig;
	}
}
