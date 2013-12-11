package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.MerkleOfSenderTransferTransactionDao;
import fr.twiced.ucoinj.dao.TransactionDao;

@Repository
@Transactional
public class MerkleOfSenderTransferTransactionDaoImpl extends GenericMultipleMerkleDaoImpl<Transaction, KeyId> implements MerkleOfSenderTransferTransactionDao {

	@Autowired
	private TransactionDao txDao;

	@Override
	public Transaction getLeaf(String hash, KeyId natId) {
		return txDao.getByHashSenderAndType(hash, natId.getHash(), TransactionType.TRANSFER);
	}

	@Override
	public Transaction getNew(String hash) {
		Transaction tx = new Transaction();
		tx.setHash(hash);
		return tx;
	}
}
