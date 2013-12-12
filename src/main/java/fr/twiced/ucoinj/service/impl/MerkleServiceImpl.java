package fr.twiced.ucoinj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.UniqueMerkle;
import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Jsonable;
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
	public Merkle<PublicKey> searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(pubkeyMerkleDao, new KeyId(""), UniqueMerkle.PUBLIC_KEY.name(), lstart, lend, start, end, extract);
	}

	@Override
	public Merkle<Hash> searchMembers(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(hashMerkleDao, new HashId(""), Merkle.getNameForMembers(amId), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchVoters(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(hashMerkleDao, new HashId(""), Merkle.getNameForVoters(amId), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchSignatures(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(signatureMerkleDao, amId, Merkle.getNameForSignatures(amId), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchVotes(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(voteMerkleDao, amId, Merkle.getNameForVotes(amId), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxAll(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txMerkleDao, new TransactionId(), UniqueMerkle.ALL_TRANSACTIONS.name(), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxDividendOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txDividendMerkleDao, id, Merkle.getNameForTxDividend(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxDividendOfSenderForAm(KeyId id, int amNum, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txDividendMerkleDao, id, Merkle.getNameForTxDividendOfAm(id, amNum), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txSenderMerkleDao, id, Merkle.getNameForTxOfSender(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxIssuanceOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txSenderMerkleDao, id, Merkle.getNameForTxIssuanceOfSender(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxFusionOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txFusionMerkleDao, id, Merkle.getNameForTxFusionOfSender(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxTransfertOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txTransferMerkleDao, id, Merkle.getNameForTxTransfertOfSender(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxOfRecipient(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txRecipientMerkleDao, id, Merkle.getNameForTxOfRecipient(id), lstart, lend, start, end, extract);
	}
	
	private <E extends Merklable, N extends NaturalId> Merkle<E> searchMerkle(
			MultipleMerkleDao<E,N> merkleDao,
			N natId,
			String name,
			Integer lstart,
			Integer lend,
			Integer start,
			Integer end,
			Boolean extract) {
		Merkle<E> merkle = merkleDao.getMerkle(name);
		if (start == null) {
			start = 0;
		}
		if (end == null) {
			end = merkle.getLeavesCount();
		}
		if (extract != null && extract) {
			List<Node> leaves = merkleDao.getLeaves(name, start, end);
			for (Node n : leaves) {
				merkle.push(merkleDao.getLeaf(n.getHash(), natId), n.getPosition());
			}
		} else {
			if (lstart == null) {
				lstart = 0;
			}
			if (lend == null) {
				lend = lstart + 1;
			}
			List<Node> nodes = merkleDao.getNodes(name, lstart, lend, start, end);
			for (Node n : nodes) {
				merkle.putTree(n);
			}
		}
		return merkle;
	}

	@Override
	public void put(PublicKey pubkey) {
		pubkeyMerkleDao.put(UniqueMerkle.PUBLIC_KEY.name(), pubkey);
	}

	@Override
	public Merkle<PublicKey> getPubkeyMerkle() {
		return pubkeyMerkleDao.getMerkle(UniqueMerkle.PUBLIC_KEY.name());
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
}
