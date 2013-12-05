package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.id.HashId;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;

@Repository
@Transactional
public class MerkleOfHashDaoImpl extends GenericMultipleMerkleDaoImpl<Hash, HashId> implements MerkleOfHashDao {

	@Override
	public Hash getLeaf(String hash, HashId natId) {
		return new Hash(hash);
	}

	@Override
	public Hash getNew(String hash) {
		return new Hash(hash);
	}
}
