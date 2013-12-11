package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface MerkleService {

	Merkle<PublicKey> getPubkeyMerkle();

	void put(PublicKey pubkey);

	void putTxOfRecipient(Transaction tx, KeyId id);

	void putTxOfSender(Transaction tx, KeyId id);

	void putTxIssuanceOfSender(Transaction tx, KeyId id);

	void putTxDividendOfSender(Transaction tx, KeyId id);

	void putTxDividendOfSenderForAm(Transaction tx, KeyId id, int amNum);

	void putTxFusionOfSender(Transaction tx, KeyId id);

	void putTxTransferOfSender(Transaction tx, KeyId id);

	Jsonable searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
	
	/** Amendments part **/

	Jsonable searchMembers(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchVoters(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchSignatures(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchVotes(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
	
	/** Transactions: sender part **/

	Jsonable searchTxOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchTxIssuanceOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchTxDividendOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
	
	Jsonable searchTxDividendOfSenderForAm(KeyId id, int amNum, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchTxFusionOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchTxTransfertOfSender(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
	
	/** Transactions: recipient part **/

	Jsonable searchTxOfRecipient(KeyId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

}
