package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.id.CoinId;

public interface CoinDao extends GenericDao<Coin> {

	public Coin getByCoinId(CoinId coinId);
	
	public Coin getByIssuerAndNumber(String issuer, Integer number);
	
	public Integer getLastNumber(String issuer);
}
