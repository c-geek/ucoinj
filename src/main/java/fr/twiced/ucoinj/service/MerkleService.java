package fr.twiced.ucoinj.service;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.id.AmendmentId;

public interface MerkleService {
	
	Merkle<PublicKey> getPubkeyMerkle();
	
	void put(PublicKey pubkey);

	Jsonable searchPubkey(Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
	
	Jsonable searchMembers(AmendmentId amId, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchVoters(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);

	Jsonable searchSignatures(AmendmentId id, Integer lstart, Integer lend, Integer start, Integer end, Boolean extract);
}
