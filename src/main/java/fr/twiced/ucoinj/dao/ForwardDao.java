package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.id.KeyId;

public interface ForwardDao extends GenericDao<Forward> {

	public Forward getByKeyIds(KeyId from, KeyId to);
	
	public Forward getByHash(String hash);

	/**
	 * Get all Forward rules type ALL by issuer.
	 * @param issuer Issuer of type ALL forwards.
	 * @return A list of corresponding forwards.
	 */
	public List<Forward> getForwardsALLFrom(KeyId issuer);

	/**
	 * Get all Forward rules type ALL by recipient.
	 * @param issuer Recipient of type ALL forwards.
	 * @return A list of corresponding forwards.
	 */
	public List<Forward> getForwardsALLTo(KeyId recipient);


	/**
	 * Get all Forward rules type KEYS for given issuer and watching for given key.
	 * @param from Issuer of KEYS forwards.
	 * @param watchedKey Fingerprint of the watched key.
	 * @return A list of corresponding forwards.
	 */
	public List<Forward> getForwardsKEYSFrom(KeyId issuer, KeyId watchedKey);


	/**
	 * Get all Forward rules type KEYS for given recipient and watching for given key.
	 * @param from Recipient of KEYS forwards.
	 * @param watchedKey Fingerprint of the watched key.
	 * @return A list of corresponding forwards.
	 */
	public List<Forward> getForwardsKEYSTo(KeyId recipient, KeyId watchedKey);
}
