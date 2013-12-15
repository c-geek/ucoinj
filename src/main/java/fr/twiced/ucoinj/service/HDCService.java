package fr.twiced.ucoinj.service;

import java.util.List;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.exceptions.RefusedDataException;
import fr.twiced.ucoinj.exceptions.UnhandledKeyException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;

public interface HDCService {

	/**
	 * Get the currently promoted amendment.
	 * @return The currently promoted amendment or null if it does not exist.
	 */
	Object current();
	
	/**
	 * Get the Merkle of signatures justifying current amendment's promotion.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object viewCurrentVoters(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Shortcut for current().
	 * @return The currently promoted amendment or null if it does not exist.
	 */
	Object promoted();
	
	/**
	 * Get the unique promoted amendment of given number.
	 * @param number The amendment number in amendment's chain (monetary contract).
	 * @return The corresponding amendment or null if it does not exist.
	 */
	Object promoted(int number);
	
	/**
	 * Get the Merkle of keys fingerprint which were considered as members for given amendment.
	 * @param id Targeted amendment id.
	 * @param extract 
	 * @param end 
	 * @param start 
	 * @param lend 
	 * @param lstart 
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object viewMembers(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the Merkle of keys fingerprint which were considered as voters for given amendment.
	 * @param id Targeted amendment id.
	 * @param extract 
	 * @param end 
	 * @param start 
	 * @param lend 
	 * @param lstart 
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object viewVoters(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException;

	/**
	 * Get the amendment content.
	 * @param id Targeted amendment id.
	 * @return or null if it does not exist.
	 */
	Object viewSelf(AmendmentId id);
	
	/**
	 * Get the Merkle of signatures justifying previous amendment of targeted amendment.
	 * @param id Targeted amendment id.
	 * @param extract 
	 * @param end 
	 * @param start 
	 * @param lend 
	 * @param lstart 
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object viewSignatures(AmendmentId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get an index giving the number of votes for each amendment id.
	 * @return A map giving number of votes per amendment id.
	 */
	Object votes();
	
	/**
	 * Add a vote for given amendment.
	 * @param am Voted amendment.
	 * @param sig Signature of the amendment.
	 * @throws BadSignatureException 
	 * @throws ObsoleteDataException 
	 * @throws RefusedDataException 
	 * @throws UnknownPublicKeyException 
	 * @throws MultiplePublicKeyException 
	 */
	void vote(Amendment am, Signature sig) throws BadSignatureException, ObsoleteDataException, RefusedDataException, MultiplePublicKeyException, UnknownPublicKeyException;
	
	/**
	 * Get the Merkle of signatures stored for targeted amendment.
	 * @param id Targeted amendment id.
	 * @return Merkle resource of null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object viewVotes(AmendmentId amId, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get a list of coins owned by targeted key.
	 * @param id Targeted key id.
	 * @return A list of coin.
	 */
	Object coinList(KeyId id);
	
	/**
	 * Get the coin of given id.
	 * @param id Coin's id.
	 * @return The full coin.
	 */
	Object coinView(CoinId id);
	
	/**
	 * Get the list of transactions registered for given coin.
	 * @param id Coin's id.
	 * @return The list of transactions this coin went throught.
	 */
	List<Object> coinHistory(CoinId id);
	
	/**
	 * Processes a transaction. May either store it or throw an UnhandledKeyException
	 * if node is not concerned by this transaction.
	 * @param tx Transaction to be processed.
	 * @param sig Signature of the transaction
	 * @throws BadSignatureException 
	 * @throws UnknownPublicKeyException 
	 * @throws MultiplePublicKeyException 
	 * @throws RefusedDataException 
	 */
	void transactionsProcess(Transaction tx, Signature sig) throws UnhandledKeyException, BadSignatureException, MultiplePublicKeyException, UnknownPublicKeyException, RefusedDataException;
	
	/**
	 * Get the Merkle of all transactions stored by this node.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsAll(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the Merkle of keys fingerprint which have some transactions stored by this node.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsKeys(Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the last transaction stored by this node.
	 * @return
	 */
	Object transactionsLast();
	
	/**
	 * Get the list of last n transactions stored by this node.
	 * @param n Last n number of transactions.
	 * @return The last n stored transactions.
	 */
	Object transactionsLasts(int n);
	
	/**
	 * Get the Merkle of transactions stored for targeted key id.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the last transaction stored for targeted key.
	 * @param id Targeted key id.
	 * @return The last stored transaction of targeted key.
	 */
	Object transactionsLastOfSender(KeyId id);

	/**
	 * Get the last n transactions stored for targeted key.
	 * @param id Targeted key id.
	 * @return The lasts n stored transactions of targeted key.
	 */
	Object transactionsLastsOfSender(KeyId id, int n);
	
	/**
	 * Get the Merkle of transfert transactions stored by this node for targeted key. 
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsTransfertOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the Merkle of issuance transactions (dividend and fusion) stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsIssuanceOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the last issuance transaction stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return The last issuance transaction.
	 */
	Object transactionsLastIssuanceOfSender(KeyId id);
	
	/**
	 * Get the Merkle of fusion transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsFusionOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;

	/**
	 * Get the Merkle of dividend transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsDividendOfSender(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the Merkle of dividend transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsDividendOfSenderForAm(KeyId id, int amendmentNumber, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the Merkle of transactions stored by this node for targeted recipient's key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 * @throws UnknownLeafException 
	 */
	Object transactionsOfRecipient(KeyId id, Boolean leaves, String leaf) throws UnknownLeafException;
	
	/**
	 * Get the transaction of given id.
	 * @param id Targeted transaction's id.
	 * @return Transaction or null if it does not exist.
	 */
	Object transaction(TransactionId id);
}
