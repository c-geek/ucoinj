package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.dao.CoinDao;

@Repository
@Transactional
public class CoinDaoImpl extends GenericDaoImpl<Coin> implements CoinDao {

	@Override
	public Coin getByCoinId(CoinId coinId) {
		return getByIssuerAndNumber(coinId.getIssuer(), coinId.getCoinNumber());
	}

	@Override
	public Integer getLastNumber(String issuer) {
		return (Integer) getSession().createQuery("select MAX(cast(coinNumber, integer)) from Coin where issuer = :issuer")
				.setParameter("issuer", issuer)
				.uniqueResult();
	}

	@Override
	public Coin getByIssuerAndNumber(String issuer, Integer number) {
		return (Coin) getSession().createQuery("from Coin where issuer = :issuer and coinNumber = :number")
				.setParameter("issuer", issuer)
				.setParameter("number", number)
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Coin.class.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coin> getByOwner(String owner) {
		return getSession().createQuery("select c from Coin c left join c.key k where k.fingerprint = :owner")
				.setParameter("owner", owner)
				.list();
	}

}
