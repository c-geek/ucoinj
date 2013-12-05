package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.dao.AmendmentDao;

@Repository
@Transactional
public class AmendmentDaoImpl extends GenericDaoImpl<Amendment> implements AmendmentDao {

	@Override
	public Amendment getByNumberAndHash(int number, String hash) {
		return (Amendment) getSession().createQuery("from Amendment a "
				+ "where a.number = :number "
				+ "and a.hash = :hash")
				.setParameter("number", number)
				.setParameter("hash", hash)
				.uniqueResult();
	}

	@Override
	public Amendment getByAmendmentId(AmendmentId amId) {
		return getByNumberAndHash(amId.getNumber(), amId.getHash());
	}

	@Override
	public Amendment getPromoted(int number) {
		return (Amendment) getSession().createQuery("from Amendment a "
				+ "where a.number = :number "
				+ "and a.promoted = 1")
				.setParameter("number", number)
				.uniqueResult();
	}

	@Override
	public Amendment getCurrent() {
		return (Amendment) getSession().createQuery("from Amendment a "
				+ "where a.current = 1")
				.uniqueResult();
	}

	@Override
	public Long getVotesCount(Amendment am) {
		return (Long) getSession().createQuery("select count(v) from Vote v "
				+ "left join v.amendment a "
				+ "where a.number = :number "
				+ "and a.hash = :hash")
				.setParameter("number", am.getNumber())
				.setParameter("hash", am.getHash())
				.uniqueResult();
	}

	@Override
	public Signature getSignature(AmendmentId natId, String hash) {
		Amendment targeted = getByAmendmentId(natId);
		Amendment previous = getByNumberAndHash(targeted.getNumber()-1, targeted.getPreviousHash());
		return (Signature) getSession().createQuery("select s from Vote v "
				+ "left join v.signature s "
				+ "left join v.amendment a "
				+ "where a.number = :number "
				+ "and a.hash = :amHash "
				+ "and s.hash = :sigHash")
				.setParameter("number", previous.getNumber())
				.setParameter("amHash", previous.getHash())
				.setParameter("sigHash", hash)
				.uniqueResult();
	}

}
