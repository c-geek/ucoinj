package fr.twiced.ucoinj.service;

import java.util.List;
import java.util.Map;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.exceptions.UnhandledKeyException;

public interface HDCService {

	/**
	 * Get the currently promoted amendment.
	 * @return The currently promoted amendment or null if it does not exist.
	 */
	Amendment current();
	
	/**
	 * Get the Merkle of signatures justifying current amendment's promotion.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Signature> currentVotes();
	
	/**
	 * Shortcut for current().
	 * @return The currently promoted amendment or null if it does not exist.
	 */
	Amendment promoted();
	
	/**
	 * Get the unique promoted amendment of given number.
	 * @param number The amendment number in amendment's chain (monetary contract).
	 * @return The corresponding amendment or null if it does not exist.
	 */
	Amendment promoted(int number);
	
	/**
	 * Get the Merkle of keys fingerprint which were considered as members for given amendment.
	 * @param id Targeted amendment id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Key> viewMembers(AmendmentId id);
	
	/**
	 * Get the Merkle of keys fingerprint which were considered as voters for given amendment.
	 * @param id Targeted amendment id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Key> viewVoters(AmendmentId id);

	/**
	 * Get the amendment content.
	 * @param id Targeted amendment id.
	 * @return or null if it does not exist.
	 */
	Amendment viewSelf(AmendmentId id);
	
	/**
	 * Get the Merkle of signatures justifying previous amendment of targeted amendment.
	 * @param id Targeted amendment id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Signature> viewSignatures(AmendmentId id);
	
	/**
	 * Get an index giving the number of votes for each amendment id.
	 * @return A map giving number of votes per amendment id.
	 */
	Map<AmendmentId, Integer> votes();
	
	/**
	 * Add a vote for given amendment.
	 * @param am Voted amendment.
	 * @param sig Signature of the amendment.
	 */
	void vote(Amendment am, Signature sig);
	
	/**
	 * Get the Merkle of signatures stored for targeted amendment.
	 * @param id Targeted amendment id.
	 * @return Merkle resource of null if it does not exist.
	 */
	Merkle<Signature> votes(AmendmentId id);
	
	/**
	 * Get a list of coins owned by targeted key.
	 * @param id Targeted key id.
	 * @return A list of coin.
	 */
	List<Coin> coinList(KeyId id);
	
	/**
	 * Get the coin of given id.
	 * @param id Coin's id.
	 * @return The full coin.
	 */
	Coin coinView(CoinId id);
	
	/**
	 * Get the list of transactions registered for given coin.
	 * @param id Coin's id.
	 * @return The list of transactions this coin went throught.
	 */
	List<Transaction> coinHistory(CoinId id);
	
	/**
	 * Processes a transaction. May either store it or throw an UnhandledKeyException
	 * if node is not concerned by this transaction.
	 * @param tx Transaction to be processed.
	 */
	void transactionsProcess(Transaction tx) throws UnhandledKeyException;
	
	/**
	 * Get the Merkle of all transactions stored by this node.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsAll();
	
	/**
	 * Get the Merkle of keys fingerprint which have some transactions stored by this node.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Key> transactionsKeys();
	
	/**
	 * Get the last transaction stored by this node.
	 * @return
	 */
	Transaction transactionsLast();
	
	/**
	 * Get the list of last n transactions stored by this node.
	 * @param n Last n number of transactions.
	 * @return The last n stored transactions.
	 */
	List<Transaction> transactionsLasts(int n);
	
	/**
	 * Get the Merkle of transactions stored for targeted key id.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsOfSender(KeyId id);
	
	/**
	 * Get the last transaction stored for targeted key.
	 * @param id Targeted key id.
	 * @return The last stored transaction of targeted key.
	 */
	Transaction transactionsLastOfSender(KeyId id);

	/**
	 * Get the last n transactions stored for targeted key.
	 * @param id Targeted key id.
	 * @return The lasts n stored transactions of targeted key.
	 */
	List<Transaction> transactionsLastsOfSender(KeyId id, int n);
	
	/**
	 * Get the Merkle of transfert transactions stored by this node for targeted key. 
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsTransfertOfSender(KeyId id);
	
	/**
	 * Get the Merkle of issuance transactions (dividend and fusion) stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsIssuanceOfSender(KeyId id);
	
	/**
	 * Get the last issuance transaction stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return The last issuance transaction.
	 */
	Transaction transactionsLastIssuanceOfSender(KeyId id);
	
	/**
	 * Get the Merkle of fusion transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsFusionOfSender(KeyId id);

	/**
	 * Get the Merkle of dividend transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsDividendOfSender(KeyId id);
	
	/**
	 * Get the Merkle of dividend transactions stored by this node for targeted key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsDividendOfSender(KeyId id, int amendmentNumber);
	
	/**
	 * Get the Merkle of transactions stored by this node for targeted recipient's key.
	 * @param id Targeted key id.
	 * @return Merkle resource or null if it does not exist.
	 */
	Merkle<Transaction> transactionsOfRecipient(KeyId id);
	
	/**
	 * Get the transaction of given id.
	 * @param id Targeted transaction's id.
	 * @return Transaction or null if it does not exist.
	 */
	Transaction transaction(TransactionId id);
}