package fr.twiced.ucoinj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.UniqueMerkle;
import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.dao.MerkleOfHashDao;
import fr.twiced.ucoinj.dao.MerkleOfPublicKeyDao;
import fr.twiced.ucoinj.dao.MultipleMerkleDao;
import fr.twiced.ucoinj.dao.impl.GenericDaoImpl;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PKSService;

@Service
@Transactional
public class MerkleServiceImpl extends GenericDaoImpl<Node> implements MerkleService {
	
	@Autowired
	private MerkleOfPublicKeyDao pubkeyMerkleDao;
	
	@Autowired
	private MerkleOfHashDao hashMerkleDao;
	
	@Autowired
	private PKSService pksService;

	@Override
	public Merkle<PublicKey> searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(pubkeyMerkleDao, UniqueMerkle.PUBLIC_KEY.name(), lstart, lend, start, end, extract);
	}

	@Override
	public Merkle<Hash> searchMembers(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		return searchMerkle(hashMerkleDao, Merkle.getNameForMembers(amId), lstart, lend, start, end, extract);
	}
	
	private <E extends Merklable> Merkle<E> searchMerkle(
			MultipleMerkleDao<E> merkleDao,
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
				merkle.push(merkleDao.getLeaf(n.getHash()), n.getPosition());
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
