package fr.twiced.ucoinj.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.dao.PublicKeyDao;
import fr.twiced.ucoinj.service.PublicKeyService;

@Service
@Transactional
public class PublicKeyServiceImpl implements PublicKeyService {

	@Autowired
	private PublicKeyDao dao;
	
	@Override
	public PublicKey getByFingerprint(String fpr) {
		return dao.getByFingerprint(fpr);
	}

	@Override
	public void save(PublicKey pubkey) {
		dao.save(pubkey);
	}

}
