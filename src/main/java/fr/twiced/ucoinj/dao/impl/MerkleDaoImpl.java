package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.dao.MerkleDao;

@Repository
@Transactional
public abstract class MerkleDaoImpl<E extends Merklable> extends GenericDaoImpl<Merkle<?>> implements MerkleDao<E> {

	@Override
	public List<Node> getLeaves(Merkle<?> m, int start, int end) {
		return getNodes(m, m.getDepth(), m.getDepth() + 1, start, end);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getNodes(Merkle<?> m, int lstart, int lend, int start, int end) {
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
