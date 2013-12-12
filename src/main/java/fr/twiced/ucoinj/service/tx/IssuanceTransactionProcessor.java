package fr.twiced.ucoinj.service.tx;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.CoinEntry;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.exceptions.RefusedDataException;

@Service
@Transactional
public class IssuanceTransactionProcessor extends TransactionProcessor {

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
	
	@Autowired
	private MerkleOfHashDao hashDao;

	@Override
	public void updateMerkles(Transaction tx) {
		merkleService.putTxAll(tx);
		merkleService.putTxOfRecipient(tx, new KeyId(tx.getRecipient()));
		merkleService.putTxOfSender(tx, new KeyId(tx.getSender()));
		merkleService.putTxIssuanceOfSender(tx, new KeyId(tx.getSender()));
		merkleService.putTxDividendOfSender(tx, new KeyId(tx.getSender()));
		List<CoinEntry> entries = tx.getCoins();
		for (CoinEntry ce : entries) {
			merkleService.putTxDividendOfSenderForAm(tx, new KeyId(tx.getSender()), ce.getCoindId().getOriginNumber());
		}
	}

	@Override
	public boolean shouldBeStored(Transaction tx) {
		return keyDao.isManaged(new KeyId(tx.getSender()));
	}

	@Override
	public void checkCoinsValidity(String sender, List<CoinEntry> coins) throws RefusedDataException {
		Transaction lastIssuance = txDao.getLastIssuance(sender);
		Integer lastIssuedCoin = lastIssuance == null ? -1 : coinDao.getLastNumber(sender);
		Map<Integer, BigInteger> valuesIssued= new HashMap<Integer, BigInteger>();
		Map<Integer, BigInteger> valuesAuthorized = new HashMap<Integer, BigInteger>();
		for (CoinEntry c : coins) {
			if (c.getTransactionId() != null) {
				throw new RefusedDataException("Issuance transaction cannot handle already issued coins");
			}
			if (c.getCoindId().getCoinNumber() != lastIssuedCoin + 1) {
				throw new RefusedDataException("Coin numbers must be sequential and follow last issued coin number");
			}
			lastIssuedCoin++;
			// Check if issuer was a member of amendment
			Integer amNumber = c.getCoindId().getOriginNumber();
			Amendment am = amDao.getPromoted(amNumber);
			String membersMerkleName = Merkle.getNameForMembers(new AmendmentId(amNumber, am.getHash()));
			if (!hashDao.hasLeaf(membersMerkleName, sender)) {
				throw new RefusedDataException(String.format("Sender was not part of the Community for amendment #%d-%s", am.getNumber(), am.getHash()));
			}
			// If it was, sums the value
			if (!valuesAuthorized.containsKey(amNumber)) {
				if (am.getDividend() == null) {
					throw new RefusedDataException(String.format("Amendment #%d does not provide any Universal Dividend", amNumber));
				}
				valuesAuthorized.put(amNumber, new BigInteger(am.getDividend().toString()));
			}
			if (!valuesIssued.containsKey(amNumber)) {
				valuesIssued.put(amNumber, BigInteger.ZERO);
				List<Coin> coinsIssued = coinDao.getByIssuerAndAmendment(sender, amNumber);
				for (Coin ci : coinsIssued) {
					BigInteger sum = valuesIssued.get(amNumber).add(ci.getValue());
					valuesIssued.put(amNumber, sum);
				}
			}
			BigInteger sum = valuesIssued.get(amNumber).add(c.getValue());
			if (sum.compareTo(valuesAuthorized.get(amNumber)) == 1) {
				throw new RefusedDataException("Cannot issue more coins than voted trought amendment");
			}
			valuesIssued.put(amNumber, sum);
		}
	}
	
	@Override
	public void checkTransactionCoherence(Transaction tx, Transaction previous) throws RefusedDataException {
		super.checkTransactionCoherence(tx, previous);
		if (!tx.getSender().equals(tx.getRecipient())) {
			throw new RefusedDataException("Issuance transaction's recipient must be equal to sender");
		}
	}

	@Override
	protected void saveCoins(Transaction tx, Key k) {
		List<CoinEntry> txCoins = tx.getCoins();
		for (CoinEntry c : txCoins) {
			coinDao.save(new Coin(c.getCoindId(), tx, k));
		}
	}
	
}
