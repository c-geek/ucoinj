package fr.twiced.ucoinj.dao.impl;

import java.util.List;

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

	@Override
	public Transaction getByHashSenderAndType(String hash, String fingerprint, TransactionType type) {
		return (Transaction) getSession().createQuery("from Transaction tx where tx.sender = :issuer and tx.hash = :hash a,d tx.type = :type")
				.setParameter("issuer", fingerprint)
				.setParameter("hash", hash)
				.setParameter("type", type)
				.uniqueResult();
	}

	@Override
	public Transaction getByHashAndRecipient(String hash, String fingerprint) {
		return (Transaction) getSession().createQuery("from Transaction tx where tx.recipient = :recipient and tx.hash = :hash")
				.setParameter("recipient", fingerprint)
				.setParameter("hash", hash)
				.uniqueResult();
	}

	@Override
	public Transaction getByHash(String hash) {
		return (Transaction) getSession().createQuery("from Transaction tx where tx.hash = :hash")
				.setParameter("hash", hash)
				.uniqueResult(); 
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Transaction> getLasts(int n) {
		List<Transaction> lasts = getSession().createQuery("from Transaction tx order by tx.received DESC")
				.setMaxResults(n)
				.list();
		return lasts;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Transaction> getLasts(String issuer, int n) {
		List<Transaction> lasts = getSession().createQuery("from Transaction tx where tx.issuer = :issuer order by tx.number DESC")
				.setParameter("issuer", issuer)
				.setMaxResults(n)
				.list();
		return lasts;
	}

	@Override
	public Transaction getLast() {
		List<Transaction> lasts = getLasts(1);
		if (lasts.isEmpty()) {
			return null;
		}
		return lasts.get(0);
	}
}
