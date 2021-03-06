package fr.twiced.ucoinj.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.CoinEntry;
import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.Vote;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.dao.VoteDao;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.exceptions.RefusedDataException;
import fr.twiced.ucoinj.exceptions.UnhandledKeyException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;
import fr.twiced.ucoinj.pgp.Sha1;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PublicKeyService;
import fr.twiced.ucoinj.service.tx.FusionTransactionProcessor;
import fr.twiced.ucoinj.service.tx.IssuanceTransactionProcessor;
import fr.twiced.ucoinj.service.tx.TransactionProcessor;
import fr.twiced.ucoinj.service.tx.TransfertTransactionProcessor;

@Service
@Transactional
public class HDCServiceImpl extends UCoinServiceImpl implements HDCService {

    private static final Logger log = LoggerFactory.getLogger(HDCServiceImpl.class);
	
	@Autowired
	private AmendmentDao amendmentDao;
	
	@Autowired
	private VoteDao voteDao;

	@Autowired
	private PublicKeyService pubkeyService;

	@Autowired
	private SignatureDao signatureDao;

	@Autowired
	private KeyDao keyDao;

	@Autowired
	private TransactionDao txDao;

	@Autowired
	private CoinDao coinDao;
	
	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	private MerkleOfHashDao hashMerkleDao;
	
	@Autowired
	private IssuanceTransactionProcessor issuanceTxProcessor;
	
	@Autowired
	private TransfertTransactionProcessor transfertTxProcessor;
	
	@Autowired
	private FusionTransactionProcessor fusionTxProcessor;

	@Override
	public Object current() {
		return jsonIt(amendmentDao.getCurrent());
	}

	@Override
	public Object promoted() {
		return jsonIt(amendmentDao.getCurrent());
	}

	@Override
	public Object promoted(int number) {
		return jsonIt(amendmentDao.getPromoted(number));
	}

	@Override
	public Object viewCurrentVoters(Boolean leaves, String leaf) throws UnknownLeafException {
		Amendment current = amendmentDao.getCurrent();
		return viewVotes(current.getNaturalId(), leaves, leaf);
	}

	@Override
	public Object viewMembers(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchMembers(id, leaves, leaf));
	}

	@Override
	public Object viewVoters(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchVoters(id, leaves, leaf));
	}

	@Override
	public Object viewSelf(AmendmentId id) {
		return jsonIt(amendmentDao.getByAmendmentId(id));
	}

	@Override
	public Object viewSignatures(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchSignatures(id, leaves, leaf));
	}

	@Override
	public Object viewVotes(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchVotes(amId, leaves, leaf));
	}

	@Override
	public Object votes() {
		List<Object[]> list = voteDao.getCount();
		Map<Integer, Map<String, Long>> result = new HashMap<>();
		for (Object[] o : list) {
			Integer number = (Integer) o[0];
			String hash = (String) o[1];
			Long count = (Long) o[2];
			if (!result.containsKey(number)) {
				result.put(number, new HashMap<String, Long>());
			}
			result.get(number).put(hash, count);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("amendments", result);
		return map;
	}

	@Override
	public void vote(Amendment am, Signature sig) throws BadSignatureException, RefusedDataException, MultiplePublicKeyException, UnknownPublicKeyException {
		Amendment previous = null;
		if (am.getNumber() > 0) {
			previous = amendmentDao.getByNumberAndHash(am.getNumber() - 1, am.getPreviousHash());
			if (previous == null) {
				throw new RefusedDataException("Unknown previous amendment");
			}
		}
		PublicKey pubkey = pubkeyService.getWorking(pubkeyService.getBySignature(sig));
		if (!sig.verify(pubkey, am.getRaw())) {
			throw new BadSignatureException("Bad signature for amendment");
		}
		Amendment storedAm = amendmentDao.getByNumberAndHash(am.getNumber(), am.getHash());
		if (storedAm == null) {
			saveAmendment(am);
		} else {
			am = storedAm;
		}
		Vote vote = voteDao.getFor(am, pubkey);
		if(vote != null && vote.getSignature().isMoreRecentThan(sig)){
			throw new ObsoleteDataException("A more recent vote is already stored");
		} else if (vote == null || vote.getSignature().isLessRecentThan(sig)) {
			boolean mayBePromoted = false;
			if (vote != null) {
				signatureDao.delete(vote.getSignature());
			}
			if (vote == null) {
				vote = new Vote();
				Amendment current = amendmentDao.getCurrent();
				if (current == null || (previous != null && current.getId().equals(previous.getId()))) {
					mayBePromoted = true;
				}
			}
			log.info(String.format("Saving new vote n°%d #%s", am.getNumber(), am.getHash()));
			signatureDao.save(sig);
			vote.setAmendment(am);
			vote.setPublicKey(pubkey);
			vote.setSignature(sig);
			voteDao.save(vote);
			hashMerkleDao.put(Merkle.getNameForVotes(am.getNaturalId()), new Hash(new Sha1(sig.getArmored())));
			// Promotion
			if (mayBePromoted) {
				log.info(String.format("Promoting amendment n°%d #%s as current", am.getNumber(), am.getHash()));
				if (am.getNumber() == 0) {
					// First amendment
					am.setPromoted(true);
					am.setCurrent(true);
					amendmentDao.save(am);
				} else {
					if (previous.getNextRequiredVotes() <= amendmentDao.getVotesCount(am)) {
						// Need required votes
						previous.setCurrent(false);
						am.setPromoted(true);
						am.setCurrent(true);
					}
				}
			}
		}
	}

	@Override
	public Object coinList(KeyId id) {
		List<Coin> coins = coinDao.getByOwner(id.getHash());
		Map<String, Object> map = new HashMap<>();
		Map<String, Set<String>> coinObjects = new TreeMap<>();
		for (Coin c : coins) {
			if (!coinObjects.containsKey(c.getCoindId().getIssuer())) {
				coinObjects.put(c.getCoindId().getIssuer(), new HashSet<String>());
			}
			coinObjects.get(c.getCoindId().getIssuer()).add(c.getCoinIdButIssuer());
		}
		List<Map<String,?>> objCoins = new ArrayList<>();
		Set<String> issuers = coinObjects.keySet();
		for (String issuer : issuers) {
			List<String> coinsOfIssuer = new ArrayList<>(coinObjects.get(issuer));
			Map<String,Object> entry = new HashMap<>();
			entry.put("issuer", issuer);
			entry.put("ids", coinsOfIssuer);
			objCoins.add(entry);
		}
		map.put("owner", id.getHash());
		map.put("coins", objCoins);
		return map;
	}

	@Override
	public Object coinView(CoinId id) {
		return coinDao.getByCoinId(id).view();
	}

	@Override
	public List<Object> coinHistory(CoinId id) {
		Coin c = coinDao.getByCoinId(id);
		if (c != null) {
			List<Object> txHistory = new ArrayList<>();
			Transaction current = c.getTransaction();
			do {
				txHistory.add(current.getJSON());
				CoinEntry ce = current.getCoinEntry(id);
				TransactionId txId = ce.getTransactionId();
				current = null;
				if (txId != null) {
					current = txDao.getByIssuerAndNumber(txId.getSender(), txId.getNumber());
				}
			} while (current != null);
			return txHistory;
		} else {
			return null;
		}
	}

	@Override
	public void transactionsProcess(Transaction tx, Signature sig) throws UnhandledKeyException, BadSignatureException, MultiplePublicKeyException, UnknownPublicKeyException, RefusedDataException {
		PublicKey pubkey = pubkeyService.getWorking(pubkeyService.getBySignature(sig));
		if (!sig.verify(pubkey, tx.getRaw())) {
			throw new BadSignatureException("Bad signature for transaction");
		}
		// Already stored?
		Transaction stored = txDao.getByIssuerAndNumber(tx.getSender(), tx.getNumber());
		if (stored == null) {
			// No, so let's process this transaction
			TransactionProcessor txProcessor = getTransactionProcessor(tx);
			tx.setSignature(sig);
			txProcessor.store(tx);
			// Don't forget to update transaction's Merkles (for indexing it)
			txProcessor.updateMerkles(tx);
		} else {
			if (stored.getHash() != tx.getHash()) {
				throw new RefusedDataException(String.format("Transaction #%d of issuer %s already stored.", tx.getNumber(), tx.getSender()));
			}
		}
	}
	
	private TransactionProcessor getTransactionProcessor(Transaction tx) {
		TransactionProcessor processor;
		switch (tx.getType()) {
		case FUSION:
			processor = fusionTxProcessor;
			break;
		case ISSUANCE:
			processor = issuanceTxProcessor;
			break;
		case TRANSFER:
		default:
			processor = transfertTxProcessor;
			break;
		}
		return processor;
	}

	@Override
	public Object transactionsAll(Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxAll(leaves, leaf));
	}

	@Override
	public Object transactionsKeys(Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxKeys(leaves, leaf));
	}

	@Override
	public Object transactionsLast() {
		Transaction last = txDao.getLast();
		if (last != null) {
			return last.getJSONObject();
		} else {
			return null;
		}
	}

	@Override
	public Object transactionsLasts(int n) {
		List<Transaction> txList = txDao.getLasts(n);
		List<Object> list = new ArrayList<>();
		for (Transaction tx : txList) {
			list.add(tx.getJSON());
		}
		Map<String, List<Object>> map = new HashMap<>();
		map.put("transactions", list);
		return map;
	}

	@Override
	public Object transactionsOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxOfSender(id, leaves, leaf));
	}

	@Override
	public Object transactionsLastOfSender(KeyId id) {
		return jsonIt(txDao.getLast(id.getHash()));
	}

	@Override
	public Object transactionsLastsOfSender(KeyId id, int n) {
		List<Transaction> txList = txDao.getLasts(id.getHash(), n);
		List<Object> list = new ArrayList<>();
		for (Transaction tx : txList) {
			list.add(tx.getJSON());
		}
		Map<String, List<Object>> map = new HashMap<>();
		map.put("transactions", list);
		return map;
	}

	@Override
	public Object transactionsTransfertOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxTransfertOfSender(id, leaves, leaf));
	}

	@Override
	public Object transactionsIssuanceOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxIssuanceOfSender(id, leaves, leaf));
	}

	@Override
	public Object transactionsLastIssuanceOfSender(KeyId id) {
		return jsonIt(txDao.getLastIssuance(id.getHash()));
	}

	@Override
	public Object transactionsFusionOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxFusionOfSender(id, leaves, leaf));
	}

	@Override
	public Object transactionsDividendOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxDividendOfSender(id, leaves, leaf));
	}

	@Override
	public Object transactionsDividendOfSenderForAm(KeyId id, int amendmentNumber, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxDividendOfSenderForAm(id, amendmentNumber, leaves, leaf));
	}

	@Override
	public Object transactionsOfRecipient(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchTxOfRecipient(id, leaves, leaf));
	}

	@Override
	public Object transaction(TransactionId id) {
		return txDao.getByIssuerAndNumber(id.getSender(), id.getNumber()).getJSON();
	}
	
	private void saveAmendment(Amendment am) throws RefusedDataException {
		log.info(String.format("Saving new amendment n°%d #%s", am.getNumber(), am.getHash()));
		// Save entity
		amendmentDao.save(am);
		// Now need to compute:
		// - members
		// - voters
		// - signatures (votes of previous)
		List<Node> previousMembersNodes = new ArrayList<Node>();
		List<Node> previousVotersNodes = new ArrayList<Node>();
		List<Node> previousSignaturesNodes = new ArrayList<Node>();
		if (am.getNumber() > 0) {
			Amendment previous = amendmentDao.getByNumberAndHash(am.getNumber() - 1, am.getPreviousHash());
			previousSignaturesNodes = hashMerkleDao.getLeaves(Merkle.getNameForVotes(previous.getNaturalId()));
			// Signatures (early test)
			createMerkleOfHashes(Merkle.getNameForSignatures(am.getNaturalId()), previousSignaturesNodes, am.getPreviousVotesRoot());
			// Members & Voters
			previousMembersNodes = hashMerkleDao.getLeaves(Merkle.getNameForMembers(previous.getNaturalId()));
			previousVotersNodes = hashMerkleDao.getLeaves(Merkle.getNameForVoters(previous.getNaturalId()));
		}
		// Members
		createMerkleOfHashes(Merkle.getNameForMembers(am.getNaturalId()), am.getMembersChanges(), previousMembersNodes);
		// Voters
		createMerkleOfHashes(Merkle.getNameForVoters(am.getNaturalId()), am.getVotersChanges(), previousVotersNodes);
	}
	
	private void createMerkleOfHashes(String name, List<String> changes, List<Node> previousNodes) {
		List<Hash> leaves = new ArrayList<>();
		for (Node n : previousNodes) {
			leaves.add(new Hash(n.getHash()));
		}
		for (String fingerprint : changes) {
			if (fingerprint.startsWith("+")) {
				leaves.add(new Hash(fingerprint.substring(1)));
			} else {
				int index = leaves.indexOf(fingerprint.substring(1));
				if (index != -1) {
					leaves.remove(index);
				}
			}
		}
		hashMerkleDao.put(name, leaves);
	}
	
	private void createMerkleOfHashes(String name, List<Node> previousNodes, String checkHash) throws RefusedDataException {
		List<Hash> leaves = new ArrayList<>();
		for (Node n : previousNodes) {
			leaves.add(new Hash(n.getHash()));
		}
		hashMerkleDao.put(name, leaves, checkHash);
	}

}
