package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Merklable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Node;

public interface UniqueMerkleDao<E extends Merklable> extends MerkleDao<E> {
	
	Merkle<E> getMerkle();

	List<Node> getAll();

	List<Node> getLeaves();

	List<Node> getLeaves(int start, int end);
	
	List<Node> getNodes(int lstart, int lend, int start, int end);
	
	public abstract E getLeaf(int position);
	
	public abstract E getLeaf(String hash);
}
