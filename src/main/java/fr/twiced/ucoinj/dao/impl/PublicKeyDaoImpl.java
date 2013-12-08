package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.dao.PublicKeyDao;

@Repository
@Transactional
public class PublicKeyDaoImpl extends GenericDaoImpl<PublicKey> implements PublicKeyDao {
	
	@Override
	public PublicKey getByFingerprint(String fpr) {
		return (PublicKey) getSession().createQuery("from PublicKey p where p.fingerprint = :fpr").setString("fpr", fpr).uniqueResult();
	}

	@Override
	public PublicKey getByKeyID(String keyID) {
		return (PublicKey) getSession().createQuery("from PublicKey p where p.fingerprint like :fpr").setString("fpr", "%" + keyID.toUpperCase()).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublicKey> lookup(String search) {
		String from = "from PublicKey p ";
		if (search.startsWith("0x")) {
			return getSession().createQuery(from + "where p.fingerprint like :fpr ")
					.setString("fpr", "%" + search.replace("0x", "") + "%")
					.list();
		} else {
			return getSession().createQuery(from + "where p.name like :search "
					+ "or p.email like :search "
					+ "or p.comment like :search")
					.setString("search", "%" + search + "%")
					.list();
		}
	}

	@Override
	protected String getEntityName() {
		return PublicKey.class.getName();
	}

}
