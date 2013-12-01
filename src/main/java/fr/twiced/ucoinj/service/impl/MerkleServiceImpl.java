package fr.twiced.ucoinj.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.dao.PublicKeyMerkleDao;
import fr.twiced.ucoinj.dao.impl.GenericDaoImpl;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PKSService;

@Service
@Transactional
public class MerkleServiceImpl extends GenericDaoImpl<Node> implements MerkleService {
	
	@Autowired
	private PublicKeyMerkleDao pubkeyMerkleDao;
	
	@Autowired
	private PKSService pksService;

	@Override
	public Merkle<PublicKey> searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract) {
		Merkle<PublicKey> merkle = pubkeyMerkleDao.getMerkle();
		if (start == null) {
			start = 0;
		}
		if (end == null) {
			end = merkle.getLeavesCount();
		}
		if (extract != null && extract) {
			List<Node> leaves = pubkeyMerkleDao.getLeaves(start, end);
			for (Node n : leaves) {
				merkle.push(pubkeyMerkleDao.getLeaf(n.getHash()), n.getPosition());
			}
		} else {
			if (lstart == null) {
				lstart = 0;
			}
			if (lend == null) {
				lend = lstart + 1;
			}
			List<Node> nodes = pubkeyMerkleDao.getNodes(lstart, lend, start, end);
			for (Node n : nodes) {
				merkle.putTree(n);
			}
		}
		return merkle;
	}

	@Override
	public void put(PublicKey pubkey) {
		Merkle<PublicKey> merkle = pubkeyMerkleDao.getMerkle();
		merkle.setRoot(null);
		pubkeyMerkleDao.save(merkle);
		List<Node> leaves = pubkeyMerkleDao.getLeaves();
		List<Node> allNodesOfPubkeyMerkle = pubkeyMerkleDao.getAll();
		for (Node node : allNodesOfPubkeyMerkle) {
			pubkeyMerkleDao.delete(node);
		}
		for (Node n : leaves) {
			PublicKey pk = new PublicKey();
			pk.setFingerprint(n.getHash());
			merkle.push(pk);
		}
		merkle.push(pubkey);
		Map<Integer, Map<Integer, String>> newTree = merkle.buildMerkle();
		Node rootNode = null;
		Set<Integer> lines = newTree.keySet();
		for (Integer line : lines) {
			Set<Integer> positions = newTree.get(line).keySet();
			for (Integer position : positions) {
				Node n = new Node();
				n.setLine(line);
				n.setPosition(position);
				n.setMerkle(merkle);
				n.setHash(newTree.get(line).get(position));
				pubkeyMerkleDao.save(n);
				if (line == 0 && position == 0) {
					rootNode = n;
				}
			}
		}
		merkle.setRoot(rootNode);
		pubkeyMerkleDao.save(merkle);
	}

	@Override
	public Merkle<PublicKey> getPubkeyMerkle() {
		return pubkeyMerkleDao.getMerkle();
	}
}
