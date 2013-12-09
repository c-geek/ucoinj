package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.dao.TransactionDao;

@Repository
@Transactional
public class TransactionDaoImpl extends GenericDaoImpl<Transaction> implements TransactionDao {

	@Override
	public Transaction getByIssuerAndNumber(String issuer, Integer number) {
		if (number == null) {
			return null;
		}
		return (Transaction) getSession().createQuery("from Transaction tx where tx.sender = :issuer and tx.number = :number")
				.setParameter("issuer", issuer)
				.setParameter("number", number)
				.uniqueResult();
	}

	@Override
	public Transaction getLast(String issuer) {
		return getByIssuerAndNumber(issuer, getLastNumber(issuer));
	}

	@Override
	public Integer getLastNumber(String issuer) {
		return (Integer) getSession().createQuery("select MAX(tx.number) from Transaction tx where tx.sender = :issuer")
				.setParameter("issuer", issuer)
				.uniqueResult();
	}

	@Override
	public Transaction getLastIssuance(String issuer) {
		return getByIssuerAndNumber(issuer, getLastIssuanceNumber(issuer));
	}

	private Integer getLastIssuanceNumber(String issuer) {
		return (Integer) getSession().createQuery("select MAX(tx.number) from Transaction tx where tx.sender = :issuer and tx.type != :type")
				.setParameter("issuer", issuer)
				.setParameter("type", TransactionType.TRANSFER)
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Transaction.class.getName();
	}

	@Override
	public Transaction getByHashAndSender(String hash, String fingerprint) {
		return (Transaction) getSession().createQuery("from Transaction tx where tx.sender = :issuer and tx.hash = :hash")
				.setParameter("issuer", fingerprint)
				.setParameter("hash", hash)
				.uniqueResult();
	}

}
