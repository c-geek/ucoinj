package fr.twiced.ucoinj.dao.impl;

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

}
