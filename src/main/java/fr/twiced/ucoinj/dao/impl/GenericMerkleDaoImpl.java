package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merkle;

@Repository
@Transactional
public class GenericMerkleDaoImpl<E> extends GenericDaoImpl<Merkle<?>> {

}
