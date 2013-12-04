package fr.twiced.ucoinj.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Key;
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
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.dao.VoteDao;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.exceptions.RefusedDataException;
import fr.twiced.ucoinj.exceptions.UnhandledKeyException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PublicKeyService;

@Service
@Transactional
public class HDCServiceImpl implements HDCService {

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
	private PGPService pgpService;
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	private MerkleOfHashDao hashMerkleDao;

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
	public Merkle<Signature> currentVotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object viewMembers(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return jsonIt(merkleService.searchMembers(id, lstart, lend, start, end, extract));
	}

	@Override
	public Object viewVoters(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return jsonIt(merkleService.searchVoters(id, lstart, lend, start, end, extract));
	}

	@Override
	public Object viewSelf(AmendmentId id) {
		return jsonIt(amendmentDao.getByAmendmentId(id));
	}

	@Override
	public Merkle<Signature> viewSignatures(AmendmentId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<AmendmentId, Integer> votes() {
		// TODO Auto-generated method stub
		return null;
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
		}
		storedAm = am;
		Vote vote = voteDao.getFor(storedAm, pubkey);
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
				if (current == null || current.getId().equals(previous.getId())) {
					mayBePromoted = true;
				}
			}
			log.info(String.format("Saving new vote n�%d #%s", am.getNumber(), am.getHash()));
			signatureDao.save(sig);
			vote.setAmendment(am);
			vote.setPublicKey(pubkey);
			vote.setSignature(sig);
			voteDao.save(vote);
			// Promotion
			if (mayBePromoted) {
				log.info(String.format("Promoting amendment n�%d #%s as current", am.getNumber(), am.getHash()));
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
	public Merkle<Signature> votes(AmendmentId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Coin> coinList(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coin coinView(CoinId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> coinHistory(CoinId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transactionsProcess(Transaction tx) throws UnhandledKeyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Merkle<Transaction> transactionsAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Key> transactionsKeys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction transactionsLast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> transactionsLasts(int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction transactionsLastOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Transaction> transactionsLastsOfSender(KeyId id, int n) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsTransfertOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsIssuanceOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction transactionsLastIssuanceOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsFusionOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsDividendOfSender(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsDividendOfSender(KeyId id, int amendmentNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Merkle<Transaction> transactionsOfRecipient(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Transaction transaction(TransactionId id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void saveAmendment(Amendment am) {
		log.info(String.format("Saving new amendment n�%d #%s", am.getNumber(), am.getHash()));
		// Save entity
		amendmentDao.save(am);
		// Now need to compute:
		// - members
		// - voters
		// - signatures (votes of previous)
		List<Node> previousMembersNodes;
		List<Node> previousVotersNodes;
		List<Node> previousSignaturesNodes;
		if (am.getNumber() == 0) {
			previousMembersNodes = new ArrayList<Node>();
			previousVotersNodes = new ArrayList<Node>();
			previousSignaturesNodes = new ArrayList<Node>();
		} else {
			Amendment previous = amendmentDao.getByNumberAndHash(am.getNumber() - 1, am.getPreviousHash());
			previousMembersNodes = hashMerkleDao.getLeaves(Merkle.getNameForMembers(previous.getNaturalId()));
			previousVotersNodes = hashMerkleDao.getLeaves(Merkle.getNameForVoters(previous.getNaturalId()));
			previousSignaturesNodes = hashMerkleDao.getLeaves(Merkle.getNameForVotes(am.getNaturalId()));
		}
		// Members
		createMerkleOfHashes(Merkle.getNameForMembers(am.getNaturalId()), am.getMembersChanges(), previousMembersNodes);
		// Voters
		createMerkleOfHashes(Merkle.getNameForVoters(am.getNaturalId()), am.getVotersChanges(), previousVotersNodes);
		// Signatures
		createMerkleOfHashes(Merkle.getNameForVotes(am.getNaturalId()), am.getVotersChanges(), previousSignaturesNodes);
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
	
	private Object jsonIt(Jsonable jsonable) {
		if (jsonable != null) {
			return jsonable.getJSON();
		}
		return null;
	}

}
