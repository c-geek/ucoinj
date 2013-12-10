package fr.twiced.ucoinj.service.tx;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.CoinEntry;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.exceptions.RefusedDataException;

@Service
@Transactional
public class FusionTransactionProcessor extends TransactionProcessor {

	@Autowired
	private TransactionDao txDao;
	
	@Autowired
	private SignatureDao sigDao;
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private CoinDao coinDao;
	
	@Autowired
	private AmendmentDao amDao;

	@Override
	public void updateMerkles(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldBeStored(Transaction tx) {
		return keyDao.isManaged(new KeyId(tx.getSender()));
	}

	@Override
	public void checkCoinsValidity(String sender, List<CoinEntry> coins) throws RefusedDataException {
		if (coins.size() < 3) {
			throw new RefusedDataException("Fusion transaction must carry at least 3 coins");
		}
		Transaction lastIssuance = txDao.getLastIssuance(sender);
		Integer lastIssuedCoin = lastIssuance == null ? -1 : coinDao.getLastNumber(sender);
		if (coins.get(0).getCoindId().getCoinNumber() != lastIssuedCoin + 1) {
			throw new RefusedDataException(String.format("Fusioned coin number must be %d", lastIssuedCoin + 1));
		}
		ListIterator<CoinEntry> iter = coins.listIterator(1);
		BigInteger finalValue = coins.get(0).getValue();
		BigInteger sum = BigInteger.ZERO;
		while(iter.hasNext()) {
			CoinEntry c = iter.next();
			if (!coinDao.getByCoinId(c.getCoindId()).getOwner().equals(sender)) {
				throw new RefusedDataException(String.format("Coin #%s cannot be fusioned by %s", c.getCoindId().getJSON(), sender));
			}
			sum = sum.add(c.getValue());
		}
		if (finalValue.compareTo(sum) != 0) {
			throw new RefusedDataException("Fusion sum does not match fusioned coin");
		}
	}

	@Override
	protected void saveCoins(Transaction tx, Key k) {
		Iterator<CoinEntry> iter = tx.getCoins().listIterator(1);
		// Fusion coin
		CoinEntry ce = tx.getCoins().get(0);
		coinDao.save(new Coin(ce.getCoindId(), tx, k));
		// Material coins
		while (iter.hasNext()) {
			ce = iter.next();
			Coin c = coinDao.getByCoinId(ce.getCoindId());
			// No more owner
			c.setKey(null);
			// Justifying transaction
			c.setTransaction(tx);
			coinDao.save(c);
		}
	}

}
