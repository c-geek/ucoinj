package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Transaction;

public interface TransactionDao extends GenericDao<Transaction> {

	public Transaction getByIssuerAndNumber(String issuer, Integer number);
	
	public Transaction getLast(String issuer);
	
	public Transaction getLastIssuance(String issuer);

	public Integer getLastNumber(String issuer);
}
