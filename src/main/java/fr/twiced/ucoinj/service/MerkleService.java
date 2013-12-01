package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;

public interface MerkleService {
	
	Merkle<PublicKey> getPubkeyMerkle();
	
	void put(PublicKey pubkey);

	Jsonable searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
}
