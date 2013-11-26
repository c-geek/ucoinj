package fr.twiced.ucoinj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.dao.PublicKeyDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@Service
@Transactional
public class PublicKeyServiceImpl implements PKSService {

	@Autowired
	private PublicKeyDao dao;

	@Autowired
	private SignatureDao signatureDao;
	
	@Autowired
	private PGPService pgpService;

	@Override
	public PublicKey add(PublicKey pubkey, Signature signature) throws ObsoleteDataException, BadSignatureException {
		signature.verify(pubkey, pubkey.getArmored());
		PublicKey stored = dao.getByKeyID(signature.getIssuerKeyId());
		if(stored != null && stored.getSignature().isMoreRecentThan(signature)){
			throw new ObsoleteDataException("A more recent version of this pubkey is already stored");
		} else if (stored == null || stored.getSignature().isLessRecentThan(signature)) {
			if (stored == null){
				signatureDao.save(signature);
				pubkey.setSignature(signature);
				dao.save(pubkey);
			} else {
				pubkey = stored;
			}
		}
		return pubkey;
	}

	@Override
	public List<PublicKey> lookup(String search) {
		return dao.lookup(search);
	}

	@Override
	public Merkle<PublicKey> all() {
		// TODO Auto-generated method stub
		return null;
	}

}
