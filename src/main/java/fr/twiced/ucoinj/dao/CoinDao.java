package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Coin;
import fr.twiced.ucoinj.bean.id.CoinId;

public interface CoinDao extends GenericDao<Coin> {

	public Coin getByCoinId(CoinId coinId);
	
	public Coin getByIssuerAndNumber(String issuer, Integer number);
	
	public List<Coin> getByOwner(String owner);
	
	public List<Coin> getByIssuerAndAmendment(String issuer, Integer amNumber);
	
	public Integer getLastNumber(String issuer);
}
