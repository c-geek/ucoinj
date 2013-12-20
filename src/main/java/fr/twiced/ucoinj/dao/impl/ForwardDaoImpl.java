package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.ForwardType;
import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.ForwardDao;

@Repository
@Transactional
public class ForwardDaoImpl extends GenericDaoImpl<Forward> implements ForwardDao {
	
	@Override
	public Forward getByKeyIds(KeyId from, KeyId to) {
		return (Forward) getSession().createQuery("from Forward f where f.from = :from and f.to = :to")
				.setParameter("from", from.getHash())
				.setParameter("to", to.getHash())
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Forward.class.getName();
	}

	@Override
	public Forward getByHash(String hash) {
		return (Forward) getSession().createQuery("from Forward f where f.hash = :hash")
				.setParameter("hash", hash)
				.uniqueResult();
	}

	@Override
	public List<Forward> getForwardsALLFrom(KeyId issuer) {
		return getSession().createQuery("from Forward f where f.from = :from and f.forward = :type")
		.setParameter("from", issuer.getHash())
		.setParameter("type", ForwardType.ALL)
		.list();
	}

	@Override
	public List<Forward> getForwardsALLTo(KeyId recipient) {
		return getSession().createQuery("from Forward f where f.to = :to and f.forward = :type")
		.setParameter("to", recipient.getHash())
		.setParameter("type", ForwardType.ALL)
		.list();
	}

	@Override
	public List<Forward> getForwardsKEYSFrom(KeyId issuer, KeyId watchedKey) {
		return getSession().createQuery("select f from Forward f left join f.keys k where f.from = :from and f.forward = :type and k = :target")
		.setParameter("from", issuer.getHash())
		.setParameter("type", ForwardType.ALL)
		.setParameter("target", watchedKey.getHash())
		.list();
	}

	@Override
	public List<Forward> getForwardsKEYSTo(KeyId recipient, KeyId watchedKey) {
		return getSession().createQuery("select f from Forward f left join f.keys k where f.to = :to and f.forward = :type and k = :target")
		.setParameter("to", recipient.getHash())
		.setParameter("type", ForwardType.ALL)
		.setParameter("target", watchedKey.getHash())
		.list();
	}

}
