package fr.twiced.ucoinj.service;

import java.util.List;

import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Status;
import fr.twiced.ucoinj.bean.THTEntry;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;

public interface UCGService {

	/**
	 * Get the public key of this node.
	 * @return Host's public key.
	 */
	PublicKey pubkey();

	/**
	 * Get the Merkle resource of all managed keys of this node.
	 * @param leaf 
	 * @param leaves 
	 * @return Merkle resource.
	 * @throws UnknownLeafException 
	 */
	Object keys(Boolean leaves, String leaf) throws UnknownLeafException;

	/**
	 * Get peering informations of this node.
	 * @return Peering informations.
	 */
	Peer peer();

	/**
	 * Get the Mekle resource of all known peers of this node.
	 * @return Merkle resource.
	 */
	Merkle<Peer> peers();

	/**
	 * Add a peering entry in pool of known peers.
	 * @param peer Peer to add.
	 * @param sig Peering entry signature.
	 */
	void addPeer(Peer peer, Signature sig);

	/**
	 * List all peers this node is listening to for ANY incoming transaction.
	 * @return Peers list.
	 */
	List<Peer> upstream();

	/**
	 * List all peers this node is listening to for this key's incoming transactions.
	 * @param id Id of the filtering Key.
	 * @return Peers list.
	 */
	List<Peer> upstream(KeyId id);

	/**
	 * List all peers this node is listening by for ANY incoming transaction.
	 * @return Peers list.
	 */
	List<Peer> downstream();

	/**
	 * List all peers this node is listening by for this key's incoming transactions.
	 * @param id Id of the filtering Key.
	 * @return Peers list.
	 */
	List<Peer> downstream(KeyId id);

	/**
	 * Add a forward rule for incoming transactions.
	 * @param forward Forward rule to be added.
	 * @param sig Forward signature.
	 */
	void addForward(Forward forward, Signature sig);

	/**
	 * Add a status information for peering purposes.
	 * @param status Status to be updated.
	 * @param sig Status signature.
	 */
	void addStatus(Status status, Signature sig);

	/**
	 * Get the Merkle resource for the whole Trust Hash Table.
	 * @return Merkle resource.
	 */
	Merkle<THTEntry> tht();

	/**
	 * Add a THT entry in the local THT.
	 * @param entry The THT entry to add.
	 * @param sig THT entry signature.
	 */
	void addTHT(THTEntry entry, Signature sig);

	/**
	 * Get the current THT entry of given key.
	 * @param id Key's id.
	 * @return The THT entry
	 */
	THTEntry tht(KeyId id);
}
