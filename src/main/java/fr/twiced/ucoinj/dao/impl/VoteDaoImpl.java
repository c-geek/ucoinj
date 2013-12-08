package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Vote;
import fr.twiced.ucoinj.dao.VoteDao;

@Repository
@Transactional
public class VoteDaoImpl extends GenericDaoImpl<Vote> implements VoteDao {

	@Override
	public Vote getFor(Amendment am, Signature sig) {
		return (Vote) getSession().createQuery("from Vote v "
				+ "where v.amendment.id = :amId "
				+ "and v.signature.id = :sigId")
				.setParameter("amId", am.getId())
				.setParameter("sigId", sig.getId())
				.uniqueResult();
	}

	@Override
	public Vote getFor(Amendment am, PublicKey pubkey) {
		return (Vote) getSession().createQuery("from Vote v "
				+ "where v.publicKey.id = :pubkeyId "
				+ "and v.amendment.id = :amId")
				.setParameter("pubkeyId", pubkey.getId())
				.setParameter("amId", am.getId())
				.uniqueResult();
	}

	@Override
	public Vote getFor(Signature sig, PublicKey pubkey) {
		return (Vote) getSession().createQuery("from Vote v "
				+ "where v.signature.id = :sigId"
				+ "and v.amendment.id = :amId")
				.setParameter("pubkeyId", pubkey.getId())
				.setParameter("sigId", sig.getId())
				.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getCount() {
		return (List<Object[]>) getSession().createQuery("select am.number, am.hash, count(am)"
				+ "from Vote v "
				+ "left join v.amendment am "
				+ "group by am.number, am.hash")
				.list();
	}

	@Override
	protected String getEntityName() {
		return Vote.class.getName();
	}

}
