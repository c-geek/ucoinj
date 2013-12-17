package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Hash;
import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.NaturalId;
import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.exceptions.RefusedDataException;

public interface MultipleMerkleDao<E extends Merklable, N extends NaturalId> extends GenericDao<Merkle<?>> {

	void delete(Node node);

	void save(Node n);
	
	Merkle<E> getMerkle(String name);
	
	E getLeaf(String hash, N natId);
	
	E getNew(String hash);
	
	List<Node> getAll(String name);
	
	boolean hasLeaf(String name, String hash);

	List<Node> getLeaves(String name);

	List<Node> getLeaves(String name, int start, int end);
	
	List<Node> getNodes(String name, int lstart, int lend, int start, int end);

	Merkle<E> put(String name, E pubkey);

	Merkle<E> put(String name, E pubkey, String rootCheck) throws RefusedDataException;

	Merkle<E> put(String name, List<E> newLeaves);

	Merkle<E> put(String name, List<E> newLeaves, String rootCheck) throws RefusedDataException;

	void remove(String name, Hash hash);

	Node getNode(String name, Hash hash);
	
}
