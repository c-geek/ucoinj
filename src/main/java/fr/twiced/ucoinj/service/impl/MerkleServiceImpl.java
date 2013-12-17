package fr.twiced.ucoinj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.UniqueMerkle;
import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.NaturalId;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.HashId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.MerkleOfPublicKeyDao;
import fr.twiced.ucoinj.dao.MerkleOfRecipientTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSenderDividendTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSenderFusionTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSenderTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSenderTransferTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSignatureOfAmendmentDao;
import fr.twiced.ucoinj.dao.MerkleOfTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfVoteOfAmendmentDao;
import fr.twiced.ucoinj.dao.MultipleMerkleDao;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PKSService;

@Service
@Transactional
public class MerkleServiceImpl implements MerkleService {
	
	@Autowired
	private MerkleOfPublicKeyDao pubkeyMerkleDao;
	
	@Autowired
	private MerkleOfHashDao hashMerkleDao;
	
	@Autowired
	private MerkleOfSignatureOfAmendmentDao signatureMerkleDao;
	
	@Autowired
	private MerkleOfVoteOfAmendmentDao voteMerkleDao;
	
	@Autowired
	private MerkleOfTransactionDao txMerkleDao;
	
	@Autowired
	private MerkleOfSenderTransactionDao txSenderMerkleDao;
	
	@Autowired
	private MerkleOfSenderDividendTransactionDao txDividendMerkleDao;
	
	@Autowired
	private MerkleOfSenderFusionTransactionDao txFusionMerkleDao;
	
	@Autowired
	private MerkleOfSenderTransferTransactionDao txTransferMerkleDao;
	
	@Autowired
	private MerkleOfRecipientTransactionDao txRecipientMerkleDao;
	
	@Autowired
	private PKSService pksService;

	@Override
	public Merkle<PublicKey> getPubkeyMerkle() {
		return pubkeyMerkleDao.getMerkle(UniqueMerkle.PUBLIC_KEY.name());
	}

	@Override
	public Merkle<PublicKey> searchPubkey(Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(pubkeyMerkleDao, new KeyId(""), UniqueMerkle.PUBLIC_KEY.name(), leaves, leaf);
	}

	@Override
	public Jsonable searchManagedKey(Boolean leaves, String leaf) throws UnknownLeafException {
		if (leaf != null) {
			Node n = hashMerkleDao.getNode(UniqueMerkle.ALL_KEYS_MANAGED.name(), new Hash(leaf));
			if (n == null) {
				throw new UnknownLeafException();
			}
			return new Hash(n.getHash());
		} else {
			return searchMerkle(hashMerkleDao, new HashId(""), UniqueMerkle.ALL_KEYS_MANAGED.name(), leaves, leaf);
		}
	}

	@Override
	public Merkle<Hash> searchMembers(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(hashMerkleDao, new HashId(""), Merkle.getNameForMembers(amId), leaves, leaf);
	}

	@Override
	public Jsonable searchVoters(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(hashMerkleDao, new HashId(""), Merkle.getNameForVoters(amId), leaves, leaf);
	}

	@Override
	public Jsonable searchSignatures(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(signatureMerkleDao, amId, Merkle.getNameForSignatures(amId), leaves, leaf);
	}

	@Override
	public Jsonable searchVotes(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(voteMerkleDao, amId, Merkle.getNameForVotes(amId), leaves, leaf);
	}

	@Override
	public Jsonable searchTxKeys(Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(hashMerkleDao, new HashId(""), UniqueMerkle.ALL_KEYS_WITH_TRANSACTION.name(), leaves, leaf);
	}

	@Override
	public Jsonable searchTxAll(Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txMerkleDao, new TransactionId(), UniqueMerkle.ALL_TRANSACTIONS.name(), leaves, leaf);
	}

	@Override
	public Jsonable searchTxDividendOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txDividendMerkleDao, id, Merkle.getNameForTxDividend(id), leaves, leaf);
	}

	@Override
	public Jsonable searchTxDividendOfSenderForAm(KeyId id, int amNum, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txDividendMerkleDao, id, Merkle.getNameForTxDividendOfAm(id, amNum), leaves, leaf);
	}

	@Override
	public Jsonable searchTxOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txSenderMerkleDao, id, Merkle.getNameForTxOfSender(id), leaves, leaf);
	}

	@Override
	public Jsonable searchTxIssuanceOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txSenderMerkleDao, id, Merkle.getNameForTxIssuanceOfSender(id), leaves, leaf);
	}

	@Override
	public Jsonable searchTxFusionOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txFusionMerkleDao, id, Merkle.getNameForTxFusionOfSender(id), leaves, leaf);
	}

	@Override
	public Jsonable searchTxTransfertOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txTransferMerkleDao, id, Merkle.getNameForTxTransfertOfSender(id), leaves, leaf);
	}

	@Override
	public Jsonable searchTxOfRecipient(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException {
		return searchMerkle(txRecipientMerkleDao, id, Merkle.getNameForTxOfRecipient(id), leaves, leaf);
	}
	
	private <E extends Merklable, N extends NaturalId> Merkle<E> searchMerkle(MultipleMerkleDao<E,N> merkleDao, N natId, String name, Boolean getLeaves, String leaf) throws UnknownLeafException {
		Merkle<E> merkle = merkleDao.getMerkle(name);
		if (leaf != null && leaf.matches("[A-Z0-9]{40}")) {
			E theLeaf = merkleDao.getLeaf(leaf, natId);
			if (theLeaf == null) {
				throw new UnknownLeafException();
			}
			merkle.setLeaf(theLeaf);
		} else {
			if (getLeaves != null && getLeaves) {
				List<Node> leaves = merkleDao.getLeaves(name);
				for (Node n : leaves) {
					merkle.push(merkleDao.getLeaf(n.getHash(), natId), n.getPosition());
				}
			}
		}
		return merkle;
	}

	@Override
	public void removeManagedKey(Key k) {
		hashMerkleDao.remove(UniqueMerkle.ALL_KEYS_MANAGED.name(), new Hash(k.getFingerprint()));
	}

	@Override
	public void put(PublicKey pubkey) {
		pubkeyMerkleDao.put(UniqueMerkle.PUBLIC_KEY.name(), pubkey);
	}

	@Override
	public void putManagedKey(Key k) {
		hashMerkleDao.put(UniqueMerkle.ALL_KEYS_MANAGED.name(), new Hash(k.getFingerprint()));
	}

	@Override
	public void putTxKey(Key k) {
		hashMerkleDao.put(UniqueMerkle.ALL_KEYS_WITH_TRANSACTION.name(), new Hash(k.getFingerprint()));
	}

	@Override
	public void putTxAll(Transaction tx) {
		txMerkleDao.put(UniqueMerkle.ALL_TRANSACTIONS.name(), tx);
	}

	@Override
	public void putTxOfSender(Transaction tx, KeyId id) {
		txSenderMerkleDao.put(Merkle.getNameForTxOfSender(id), tx);
	}

	@Override
	public void putTxIssuanceOfSender(Transaction tx, KeyId id) {
		txSenderMerkleDao.put(Merkle.getNameForTxIssuanceOfSender(id), tx);
	}

	@Override
	public void putTxDividendOfSender(Transaction tx, KeyId id) {
		txDividendMerkleDao.put(Merkle.getNameForTxDividend(id), tx);
	}

	@Override
	public void putTxDividendOfSenderForAm(Transaction tx, KeyId id, int amNum) {
		txDividendMerkleDao.put(Merkle.getNameForTxDividendOfAm(id, amNum), tx);
	}

	@Override
	public void putTxFusionOfSender(Transaction tx, KeyId id) {
		txFusionMerkleDao.put(Merkle.getNameForTxFusionOfSender(id), tx);
	}

	@Override
	public void putTxTransferOfSender(Transaction tx, KeyId id) {
		txTransferMerkleDao.put(Merkle.getNameForTxTransfertOfSender(id), tx);
	}

	@Override
	public void putTxOfRecipient(Transaction tx, KeyId id) {
		txSenderMerkleDao.put(Merkle.getNameForTxOfRecipient(id), tx);
	}

	@Override
	public String getRootPksAll() {
		return getRoot(pubkeyMerkleDao, UniqueMerkle.PUBLIC_KEY.name());
	}

	@Override
	public String getRootManagedKeys() {
		return getRoot(hashMerkleDao, UniqueMerkle.ALL_KEYS_MANAGED.name());
	}

	@Override
	public String getRootTxKeys() {
		return getRoot(hashMerkleDao, UniqueMerkle.ALL_KEYS_WITH_TRANSACTION.name());
	}

	@Override
	public String getRootTxAll() {
		return getRoot(txMerkleDao, UniqueMerkle.ALL_TRANSACTIONS.name());
	}

	@Override
	public String getRootTxOfRecipient(KeyId id) {
		return getRoot(txRecipientMerkleDao, Merkle.getNameForTxOfRecipient(id));
	}

	@Override
	public String getRootTxOfSender(KeyId id) {
		return getRoot(txSenderMerkleDao, Merkle.getNameForTxOfSender(id));
	}

	@Override
	public String getRootTxIssuanceOfSender(KeyId id) {
		return getRoot(txSenderMerkleDao, Merkle.getNameForTxIssuanceOfSender(id));
	}

	@Override
	public String getRootTxDividendOfSender(KeyId id) {
		return getRoot(txDividendMerkleDao, Merkle.getNameForTxDividend(id));
	}

	@Override
	public String getRootTxDividendOfSenderForAm(KeyId id, int amNum) {
		return getRoot(txDividendMerkleDao, Merkle.getNameForTxDividendOfAm(id, amNum));
	}

	@Override
	public String getRootTxFusionOfSender(KeyId id) {
		return getRoot(txFusionMerkleDao, Merkle.getNameForTxFusionOfSender(id));
	}

	@Override
	public String getRootTxTransferOfSender(KeyId id) {
		return getRoot(txTransferMerkleDao, Merkle.getNameForTxTransfertOfSender(id));
	}

	private String getRoot(MultipleMerkleDao<?, ?> merkleDao, String name) {
		Node n = merkleDao.getMerkle(name).getRoot();
		return n == null ? null : n.getHash();
	}
}
