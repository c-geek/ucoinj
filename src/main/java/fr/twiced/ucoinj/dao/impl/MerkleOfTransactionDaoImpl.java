package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.dao.MerkleOfTransactionDao;
import fr.twiced.ucoinj.dao.TransactionDao;

@Repository
@Transactional
public class MerkleOfTransactionDaoImpl extends GenericMultipleMerkleDaoImpl<Transaction, TransactionId> implements MerkleOfTransactionDao {

	@Autowired
	private TransactionDao txDao;

	@Override
	public Transaction getLeaf(String hash, TransactionId natId) {
		return txDao.getByHash(hash);
	}

	@Override
	public Transaction getNew(String hash) {
		Transaction tx = new Transaction();
		tx.setHash(hash);
		return tx;
	}
}
