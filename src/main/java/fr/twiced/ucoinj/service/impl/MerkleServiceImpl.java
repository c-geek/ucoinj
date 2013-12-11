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
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.HashId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.MerkleOfPublicKeyDao;
import fr.twiced.ucoinj.dao.MerkleOfSenderTransactionDao;
import fr.twiced.ucoinj.dao.MerkleOfSignatureOfAmendmentDao;
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
	private MerkleOfSenderTransactionDao txMerkleDao;
	
	@Autowired
	private PKSService pksService;

	@Override
	public Merkle<PublicKey> searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(pubkeyMerkleDao, new KeyId(""),UniqueMerkle.PUBLIC_KEY.name(), lstart, lend, start, end, extract);
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
	public Jsonable searchTxDividendOfSender(KeyId id, int amNum, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txMerkleDao, id, Merkle.getNameForTxDividendOfAm(id, amNum), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txMerkleDao, id, Merkle.getNameForTxOfSender(id), lstart, lend, start, end, extract);
	}

	@Override
	public Jsonable searchTxIssuanceOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(txMerkleDao, id, Merkle.getNameForTxIssuanceOfSender(id), lstart, lend, start, end, extract);
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
}
