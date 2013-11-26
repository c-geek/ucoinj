package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.dao.SignatureDao;

@Repository
@Transactional
public class SignatureDaoImpl extends GenericDaoImpl<Signature> implements SignatureDao {

}
