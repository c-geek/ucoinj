package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.AmendmentId;

public interface AmendmentDao extends GenericDao<Amendment> {

	Amendment getByNumberAndHash(int number, String hash);

	Amendment getPromoted(int number);

	Amendment getCurrent();

	Long getVotesCount(Amendment am);

	Amendment getByAmendmentId(AmendmentId amId);

	Signature getSignature(AmendmentId natId, String hash);

	Signature getVote(AmendmentId natId, String hash);

}
