package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;

public interface MerkleService {

	Merkle<PublicKey> getPubkeyMerkle();

	String getRootPksAll();

	String getRootTxKeys();

	String getRootTxAll();

	String getRootTxOfRecipient(KeyId id);

	String getRootTxOfSender(KeyId id);

	String getRootTxIssuanceOfSender(KeyId id);

	String getRootTxDividendOfSender(KeyId id);

	String getRootTxDividendOfSenderForAm(KeyId id, int amNum);

	String getRootTxFusionOfSender(KeyId id);

	String getRootTxTransferOfSender(KeyId id);

	void put(PublicKey pubkey);

	void putTxKey(Key k);

	void putTxAll(Transaction tx);

	void putTxOfRecipient(Transaction tx, KeyId id);

	void putTxOfSender(Transaction tx, KeyId id);

	void putTxIssuanceOfSender(Transaction tx, KeyId id);

	void putTxDividendOfSender(Transaction tx, KeyId id);

	void putTxDividendOfSenderForAm(Transaction tx, KeyId id, int amNum);

	void putTxFusionOfSender(Transaction tx, KeyId id);

	void putTxTransferOfSender(Transaction tx, KeyId id);

	Jsonable searchPubkey(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/** Amendments part **/

	Jsonable searchMembers(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchVoters(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchSignatures(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchVotes(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException;

	/** Keys for whom we have transactions **/
	
	Jsonable searchTxKeys(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/** Transactions : all **/

	Jsonable searchTxAll(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/** Transactions: sender part **/

	Jsonable searchTxOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchTxIssuanceOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchTxDividendOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	Jsonable searchTxDividendOfSenderForAm(KeyId id, int amNum, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchTxFusionOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;

	Jsonable searchTxTransfertOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/** Transactions: recipient part **/

	Jsonable searchTxOfRecipient(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
}
