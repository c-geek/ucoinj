package fr.twiced.ucoinj.service.tx;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.CoinEntry;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.exceptions.RefusedDataException;

@Service
@Transactional
public abstract class TransactionProcessor {

	@Autowired
	private TransactionDao txDao;
	
	@Autowired
	private SignatureDao sigDao;
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private CoinDao coinDao;
	
	public void checkTransactionCoherence(Transaction tx, Transaction previous) throws RefusedDataException {
		if (!tx.isFullFilled()) {
			throw new RefusedDataException("Transaction is not complete");
		}
		if (previous == null && tx.getNumber() != 0) {
			throw new RefusedDataException("First transaction must have number #0");
		}
		if (previous == null && tx.getNumber() == 0 && tx.getPreviousHash() != null && !tx.getPreviousHash().isEmpty()) {
			throw new RefusedDataException("First transaction must not have previous hash");
		}
		if (tx.getNumber() > 0) {
			// Checking chaining coherence
			if (!tx.getNumber().equals(previous.getNumber() + 1)) {
				throw new RefusedDataException(String.format("Transaction number #%d does not follow last received transaction #%d", tx.getNumber(), previous.getNumber()));
			}
			if (!tx.getPreviousHash().equals(previous.getHash())) {
				throw new RefusedDataException(String.format("Previous hash #%s does not match the last received transaction #%s", tx.getHash(), previous.getHash()));
			}
		}
		if (tx.getCoins().size() == 0) {
			throw new RefusedDataException("Transaction must carry at least one coin");
		}
		checkCoinsValidity(tx.getSender(), tx.getCoins());
	}

	public void store(Transaction tx) throws RefusedDataException {
		if (!shouldBeStored(tx)) {
			throw new RefusedDataException("Node not concerned by this transaction");
		}
		Transaction lastOfSender = txDao.getLast(tx.getSender());
		checkTransactionCoherence(tx, lastOfSender);
		// Hmmm .. seems OK, store it.
		Key k = keyDao.getByKeyId(new KeyId(tx.getRecipient()));
		if (k == null) {
			k = new Key();
			k.setFingerprint(tx.getRecipient());
			k.setManaged(false);
			keyDao.save(k);
		}
		// Save tx
		txDao.save(tx);
		// Create/Update coins
		saveCoins(tx, k);
	}
	
	protected abstract void saveCoins(Transaction tx, Key k);
	
	public abstract void updateMerkles(Transaction tx);
	
	public abstract boolean shouldBeStored(Transaction tx);
	
	public abstract void checkCoinsValidity(String sender, List<CoinEntry> coins) throws RefusedDataException;
}
