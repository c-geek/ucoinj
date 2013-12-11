package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.Transaction;

public interface TransactionDao extends GenericDao<Transaction> {

	public Transaction getByIssuerAndNumber(String issuer, Integer number);
	
	public Transaction getLast(String issuer);
	
	public Transaction getLastIssuance(String issuer);

	public Integer getLastNumber(String issuer);

	public Transaction getByHashAndSender(String hash, String fingerprint);

	public Transaction getByHashSenderAndType(String hash, String fingerprint, TransactionType type);

	public Transaction getByHashAndRecipient(String hash, String fingerprint);
}
