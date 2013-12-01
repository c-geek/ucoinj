package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.dao.MerkleDao;
import fr.twiced.ucoinj.dao.NodeDao;

@Repository
@Transactional
public abstract class MerkleDaoImpl<E extends Merklable> extends GenericDaoImpl<Merkle<?>> implements MerkleDao<E> {
	
	@Autowired
	protected NodeDao nodeDao;

	@Override
	public List<Node> getAll() {
		return nodeDao.getAll(getMerkle());
	}

	@Override
	public List<Node> getLeaves() {
		return nodeDao.getLeaves(getMerkle());
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
	public List<Node> getLeaves(int start, int end) {
		Merkle<?> m = getMerkle();
		return getNodes(m.getDepth(), m.getDepth() + 1, start, end);
	}

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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getNodes(int lstart, int lend, int start, int end) {
		Merkle<?> m = getMerkle();
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
}
