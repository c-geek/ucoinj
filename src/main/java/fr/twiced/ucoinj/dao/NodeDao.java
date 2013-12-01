package fr.twiced.ucoinj.dao;

import java.util.List;
import java.util.Map;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;

public interface NodeDao extends GenericDao<Node> {

	Map<Integer, Node> getSomeLeaves(Merkle<?> merkle, int begin, int end);
	Map<Integer, Node> getSomeLeaves(Merkle<?> merkle, int[] positions);
	Node getNode(Merkle<?> merkle, int row, int position);
	List<Node> getLeaves(Merkle<?> merkle);
	List<Node> getAll(Merkle<?> merkle);
}
