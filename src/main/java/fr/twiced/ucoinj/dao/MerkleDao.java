package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;

public interface MerkleDao<E extends Merklable> extends GenericDao<Merkle<?>> {

	void delete(Node node);

	void save(Node n);
	
	List<Node> getLeaves(Merkle<?> m, int start, int end);
	
	List<Node> getNodes(Merkle<?> m, int lstart, int lend, int start, int end);
	
	E getLeaf(Merkle<?> m, int position);
	
	E getLeaf(Merkle<?> m, String hash);
}
