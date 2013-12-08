package fr.twiced.ucoinj.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.dao.NodeDao;

@Repository
@Transactional
public class NodeDaoImpl extends GenericDaoImpl<Node> implements NodeDao {

	@Override
	public Map<Integer, Node> getSomeLeaves(Merkle<?> merkle, int begin, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Node> getSomeLeaves(Merkle<?> merkle, int[] positions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Node getNode(Merkle<?> merkle, int row, int position) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getLeaves(Merkle<?> merkle) {
		return getSession().createQuery("select n from Node n left join n.merkle as m where m.name like :name "
				+ "and n.line = m.depth")
				.setParameter("name", merkle.getName())
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Node> getAll(Merkle<?> merkle) {
		return getSession().createQuery("select n from Node n left join n.merkle as m where m.name like :name")
				.setParameter("name", merkle.getName())
				.list();
	}

	@Override
	protected String getEntityName() {
		return Node.class.getName();
	}

}
