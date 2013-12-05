package fr.twiced.ucoinj.dao;

import java.util.List;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Vote;

public interface VoteDao extends GenericDao<Vote>{

	Vote getFor(Amendment am, Signature sig);
	
	Vote getFor(Amendment am, PublicKey pubkey);

	Vote getFor(Signature sig, PublicKey pubkey);

	List<Object[]> getCount();
}
