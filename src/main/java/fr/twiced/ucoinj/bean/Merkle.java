package fr.twiced.ucoinj.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.pgp.Sha1;

@Entity
public class Merkle<E extends Merklable> implements Hashable, Jsonable {
	
	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private Integer id;
	
	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private Integer depth;

	@Column(nullable = false)
	private Integer nodesCount;

	@Column(nullable = false)
	private Integer leavesCount;

	@Column(nullable = false)
	private Integer levelsCount;

	@OneToOne(cascade = CascadeType.ALL)
	private Node root;
	
	@Transient
	private Map<Integer, String> leavesHashList;
	
	@Transient
	private Map<String, E> leavesByHash;
	
	@Transient
	private Map<Integer, Map<Integer, String>> tree;
	
	public Merkle() {
		name = "";
		depth = 0;
		levelsCount = 0;
		leavesCount = 0;
		nodesCount = 0;
		initTrees();
	}
	
	public void initTrees() {
		leavesByHash = new TreeMap<>();
		leavesHashList = new TreeMap<>();
		tree = new TreeMap<>();
	}
	
	public void push(E leaf) {
		leavesByHash.put(leaf.getHash(), leaf);
		leavesHashList.clear();
		List<String> hashesSorted = new ArrayList<>();
		Set<String> hashes = leavesByHash.keySet();
		for (String hash : hashes) {
			hashesSorted.add(hash);
		}
		Collections.sort(hashesSorted);
		for (int i = 0; i < hashesSorted.size(); i++) {
			leavesHashList.put(i, hashesSorted.get(i));
		}
		hashesSorted.clear();
	}
	
	public void push(E leaf, int position) {
		leavesByHash.put(leaf.getHash(), leaf);
		leavesHashList.put(position, leaf.getHash());
	}
	
	public void remove(E leaf) {
		leavesByHash.remove(leaf.getHash());
		removeFromList(leavesHashList, leaf.getHash());
		for (String hash : leavesByHash.keySet()) {
			leavesHashList.put(leavesHashList.size(), hash);
		}
	}
	
	public void pushAll(List<E> leaves) {
		for (E leaf : leaves) {
			leavesByHash.put(leaf.getHash(), leaf);
		}
		leavesHashList.clear();
		for (String hash : leavesByHash.keySet()) {
			leavesHashList.put(leavesHashList.size(), hash);
		}
	}
	
	private void removeFromList(Map<Integer, String> leavesHashList, String hash){
		Set<Integer> keys = leavesHashList.keySet();
		boolean found = false;
		Iterator<Integer> iter = keys.iterator();
		while (!found && iter.hasNext()) {
			Integer next = iter.next();
			if (leavesHashList.get(next).equals(hash)) {
				found = true;
				leavesHashList.remove(next);
			}
		}
	}

	public Map<Integer, Map<Integer, String>> buildMerkle() {
		Map<Integer, Map<Integer, String>> partialTree = new HashMap<>();
		// Initialze tree basis
		partialTree.put(0, new HashMap<Integer, String>());
		for (int a = 0; a < leavesHashList.size(); a++) {
			partialTree.get(0).put(a, leavesHashList.get(a));
		}
		// Level to build
		int i = 1;
		// Number of nodes to be hashed (i - 1)
		this.nodesCount = 0;
		while (partialTree.get(i - 1).size() > 1) {
			// Make the new row
			int nbElementPreceding = partialTree.get(i - 1).size();
			partialTree.put(i, new HashMap<Integer, String>());
			for (int j = 0; j < nbElementPreceding - 1; j = j + 2) {
				// Make the node (i,j/2)
				String hash1 = partialTree.get(i - 1).get(j);
				String hash2 = partialTree.get(i - 1).get(j + 1);
				String hash3 = new Sha1(hash1 + hash2).toString().toUpperCase();
				partialTree.get(i).put(j / 2, hash3);
				this.nodesCount++;
			}
			// Eventually add the remainder (in case % 2 == 1)
			if (nbElementPreceding % 2 == 1) {
				int sizeOfLevel = partialTree.get(i).size();
				partialTree.get(i).put(sizeOfLevel, partialTree.get(i - 1).get(sizeOfLevel * 2));
			}
			// Next level
			i++;
		}
		// Now we have the whole tree, lets inverse it
		Map<Integer, Map<Integer, String>> tree = new HashMap<>();
		for (i = 1; i <= partialTree.size(); i++) {
			tree.put(i - 1, partialTree.get(partialTree.size() - i));
		}
		this.depth = tree.size() - 1;
		this.levelsCount = tree.size();
		this.leavesCount = tree.get(tree.size() - 1).size();
		return tree;
	}

	@Transient
	public Map<Integer, Map<Integer, String>> getTree() {
		return tree;
	}

	public void setTree(Map<Integer, Map<Integer, String>> tree) {
		this.tree = tree;
	}

	public void putTree(Node n) {
		putTree(n.getLine(), n.getPosition(), n);
	}

	public void putTree(Integer line, Hashable hash) {
		if (!tree.containsKey(line)) {
			putTree(line, 0, hash);
		} else {
			putTree(line, tree.get(line).size(), hash);
		}
	}

	public void putTree(Integer line, Integer position, Hashable hash) {
		if (!tree.containsKey(line)) {
			tree.put(line, new TreeMap<Integer, String>());
		}
		tree.get(line).put(position, hash.getHash());
	}

	public Node getRoot() {
		return root;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	/* GETTERS AND SETTERS */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public Integer getNodesCount() {
		return nodesCount;
	}

	public void setNodesCount(Integer nodesCount) {
		this.nodesCount = nodesCount;
	}

	public Integer getLeavesCount() {
		return leavesCount;
	}

	public void setLeavesCount(Integer leavesCount) {
		this.leavesCount = leavesCount;
	}

	public Integer getLevelsCount() {
		return levelsCount;
	}

	public void setLevelsCount(Integer levelsCount) {
		this.levelsCount = levelsCount;
	}

	@Transient
	@Override
	public String getHash() {
		return root.getHash();
	}

	@Transient
	@Override
	public Object getJSON() {
		Map<String, Object> map = new HashMap<>();
		map.put("depth", depth);
		map.put("nodesCount", nodesCount);
		map.put("levelsCount", levelsCount);
		map.put("leavesCount", leavesCount);
		if (!leavesHashList.isEmpty()) {
			// Leaves display
			Map<Integer, Map<String, Object>> leaves = new HashMap<>();
			Set<Integer> keys = leavesHashList.keySet();
			for (Integer k : keys) {
				Map<String, Object> values = new HashMap<>();
				String hash = leavesHashList.get(k);
				values.put("hash", hash);
				values.put("value", leavesByHash.get(hash).getJSON());
				leaves.put(k, values);
			}
			map.put("leaves", leaves);
		} else {
			// Levels display
			Map<Integer, List<String>> leaves = new HashMap<>();
			Set<Integer> keys = tree.keySet();
			for (Integer k : keys) {
				List<String> hashes = new ArrayList<>(tree.get(k).values());
				leaves.put(k, hashes);
			}
			map.put("levels", leaves);
		}
		return map;
	}
	
	public static String getNameForMembers(AmendmentId amId) {
		return String.format("am_%d_%s_members", amId.getNumber(), amId.getHash());
	}
	
	public static String getNameForVoters(AmendmentId amId) {
		return String.format("am_%d_%s_voters", amId.getNumber(), amId.getHash());
	}
	
	public static String getNameForVotes(AmendmentId amId) {
		return String.format("am_%d_%s_votes", amId.getNumber(), amId.getHash());
	}
	
	public static String getNameForSignatures(AmendmentId amId) {
		return String.format("am_%d_%s_signatures", amId.getNumber(), amId.getHash());
	}
	
	public static String getNameForTxDividendOfAm(KeyId id, int amNum) {
		return String.format("am_dividend_am%d_issuer_%s", amNum, id.getHash());
	}
}
