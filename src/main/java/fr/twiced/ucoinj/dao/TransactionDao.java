package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.Transaction;

public interface TransactionDao extends GenericDao<Transaction> {

	public Transaction getByIssuerAndNumber(String issuer, Integer number);
	
	public Transaction getLast(String issuer);
	
	public Transaction getLastIssuance(String issuer);
	
	public List<Transaction> getLasts(int n);
	
	public List<Transaction> getLasts(String issuer, int n);

	public Integer getLastNumber(String issuer);

	public Transaction getByHashAndSender(String hash, String fingerprint);

	public Transaction getByHashSenderAndType(String hash, String fingerprint, TransactionType type);

	public Transaction getByHashAndRecipient(String hash, String fingerprint);

	public Transaction getByHash(String hash);

	public Transaction getLast();
}
