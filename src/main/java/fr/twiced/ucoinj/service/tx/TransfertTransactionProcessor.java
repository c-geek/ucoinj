package fr.twiced.ucoinj.service.tx;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Coin;
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
public class TransfertTransactionProcessor extends TransactionProcessor {

	@Autowired
	private TransactionDao txDao;
	
	@Autowired
	private SignatureDao sigDao;
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private CoinDao coinDao;

	@Override
	public void updateMerkles(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldBeStored(Transaction tx) {
		boolean mayBeStoredForSender = keyDao.isManaged(new KeyId(tx.getSender()));
		boolean mayBeStoredForRecipient = keyDao.isManaged(new KeyId(tx.getRecipient()));
		if (!mayBeStoredForSender && !mayBeStoredForRecipient) {
			return false;
		}
		return true;
	}

	@Override
	public void checkCoinsValidity(String sender, List<CoinEntry> coins) throws RefusedDataException {
		// Checking coins ownership
		for (CoinEntry c : coins) {
			if (!coinDao.getByCoinId(c.getCoindId()).getOwner().equals(sender)) {
				throw new RefusedDataException(String.format("Coin #%s cannot be spent by %s", c.getCoindId().getJSON(), sender));
			}
		}
	}

	@Override
	protected void saveCoins(Transaction tx, Key k) {
		List<CoinEntry> txCoins = tx.getCoins();
		for (CoinEntry c : txCoins) {
			Coin stored = coinDao.getByCoinId(c.getCoindId());
			if (stored == null) {
				stored = new Coin(c.getCoindId(), tx, k);
			} else {
				// Change owner
				stored.setKey(k);
				// Change justifying transaction
				stored.setTransaction(tx);
			}
			// Save the coin
			coinDao.save(stored);
		}
	}

}
