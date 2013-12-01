package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.dao.NodeDao;
import fr.twiced.ucoinj.dao.UniqueMerkleDao;

@Repository
@Transactional
public abstract class UniqueMerkleDaoImpl<E extends Merklable> extends MerkleDaoImpl<E> implements UniqueMerkleDao<E> {
	
	@Autowired
	protected NodeDao nodeDao;

	@SuppressWarnings("unchecked")
	@Override
	public Merkle<E> getMerkle() {
		Merkle<E> merkle = (Merkle<E>) getSession().createQuery("from Merkle m where m.name like :name").setParameter("name", getName()).uniqueResult();
		if (merkle == null) {
			merkle = new Merkle<>();
			merkle.setName(getName());
			save(merkle);
		}
		return merkle;
	}

	@Override
	public List<Node> getAll() {
		return nodeDao.getAll(getMerkle());
	}

	@Override
	public List<Node> getLeaves() {
		return nodeDao.getLeaves(getMerkle());
	}
	
	@Override
	public E getLeaf(Merkle<?> m, String hash) {
		return getLeaf(hash);
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
	public E getLeaf(Merkle<?> m, int position) {
		Merkle<E> merkle = getMerkle();
		Node n = nodeDao.getNode(merkle, merkle.getDepth(), position);
		return getLeaf(n.getHash());
	}
	
	@Override
	public E getLeaf(int position) {
		return getLeaf(getMerkle(), position);
	}
	
	@Override
	public List<Node> getLeaves(int start, int end) {
		return getLeaves(getMerkle(), start, end);
	}
	
	@Override
	public List<Node> getNodes(int lstart, int lend, int start, int end) {
		return getNodes(getMerkle(), lstart, lend, start, end);
	}
	
	public abstract String getName();
}
