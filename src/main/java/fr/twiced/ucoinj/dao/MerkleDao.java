package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.NaturalId;
import fr.twiced.ucoinj.bean.Node;

public interface MerkleDao<E extends Merklable, N extends NaturalId> extends GenericDao<Merkle<?>> {

	void delete(Node node);

	void save(Node n);
	
	String getName();
	
	Merkle<E> getMerkle();
	
	E getLeaf(String hash, N natId);
	
	List<Node> getAllNodes();

	List<Node> getLeaves();

	List<Node> getLeaves(int start, int end);
	
	List<Node> getNodes(int lstart, int lend, int start, int end);
	
}
