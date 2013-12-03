package fr.twiced.ucoinj.dao;

import fr.twiced.ucoinj.bean.Amendment;

public interface AmendmentDao extends GenericDao<Amendment> {

	Amendment getByNumberAndHash(int number, String hash);

	Amendment getPromoted(int number);

	Amendment getCurrent();

	Long getVotesCount(Amendment am);

}
