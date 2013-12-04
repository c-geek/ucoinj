package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;

public interface MultipleMerkleDao<E extends Merklable> extends GenericDao<Merkle<?>> {

	void delete(Node node);

	void save(Node n);
	
	Merkle<E> getMerkle(String name);
	
	E getLeaf(String hash);
	
	E getNew(String hash);
	
	List<Node> getAll(String name);

	List<Node> getLeaves(String name);

	List<Node> getLeaves(String name, int start, int end);
	
	List<Node> getNodes(String name, int lstart, int lend, int start, int end);

	void put(String name, E pubkey);

	void put(String name, List<E> newLeaves);
	
}
