package fr.twiced.ucoinj.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.dao.MultipleMerkleDao;
import fr.twiced.ucoinj.dao.NodeDao;
import fr.twiced.ucoinj.dao.PublicKeyDao;

@Repository
@Transactional
public abstract class GenericMultipleMerkleDaoImpl<E extends Merklable> extends GenericMerkleDaoImpl<E> implements MultipleMerkleDao<E> {

	@Autowired
	private PublicKeyDao pubkeyDao;
	
	@Autowired
	private NodeDao nodeDao;

	@Override
	public List<Node> getAll(String name) {
		return nodeDao.getAll(getMerkle(name));
	}

	@Override
	public List<Node> getLeaves(String name) {
		return nodeDao.getLeaves(getMerkle(name));
	}

	@Override
	public void delete(Node node) {
		nodeDao.delete(node);
	}

	@Override
	public void save(Node n) {
		nodeDao.save(n);
	}

	@Override
	public List<Node> getLeaves(String name, int start, int end) {
		Merkle<?> m = getMerkle(name);
		return getNodes(name, m.getDepth(), m.getDepth() + 1, start, end);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Merkle<E> getMerkle(String name) {
		Merkle<E> merkle = (Merkle<E>) getSession().createQuery("from Merkle m "
				+ "where m.name like :name")
				.setParameter("name", name)
				.uniqueResult();
		if (merkle == null) {
			merkle = new Merkle<>();
			merkle.setName(name);
			save(merkle);
		}
		return merkle;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getNodes(String name, int lstart, int lend, int start, int end) {
		Merkle<?> m = getMerkle(name);
		return getSession().createQuery("select n from Node n "
				+ "where n.merkle.id = :merkleId "
				+ "AND n.line >= :lstart "
				+ "AND n.line < :lend "
				+ "AND n.position >= :start "
				+ "AND n.position < :end")
				.setParameter("merkleId", m.getId())
				.setParameter("lstart", lstart)
				.setParameter("lend", lend)
				.setParameter("start", start)
				.setParameter("end", end)
				.list();
	}

	@Override
	public void put(String name, E newLeaf) {
		List<E> newLeaves = new ArrayList<>();
		newLeaves.add(newLeaf);
		put(name, newLeaves);
	}

	@Override
	public void put(String name, List<E> newLeaves) {
		Merkle<E> merkle = getMerkle(name);
		// Clear merkle data
		merkle.setRoot(null);
		merkle.initTrees();
		save(merkle);
		// Retrieve Merkle's leaves (bottom part)
		List<Node> leaves = getLeaves(name);
		// Retrieve Merkle's nodes (up part)
		List<Node> allNodesOfMerkle = getAll(name);
		// Remove node part, which is to be rebuilt
		for (Node node : allNodesOfMerkle) {
			delete(node);
		}
		// Rebuild merkle leaves
		List<E> allNewLeaves = new ArrayList<>();
		for (Node n : leaves) {
			newLeaves.add(getNew(n.getHash()));
		}
		allNewLeaves.addAll(newLeaves);
		// New leaves is exactly the same + new one leaf
		merkle.pushAll(newLeaves);
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
				save(n);
				if (line == 0 && position == 0) {
					rootNode = n;
				}
			}
		}
		merkle.setRoot(rootNode);
		save(merkle);
	}
}