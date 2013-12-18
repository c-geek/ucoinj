package fr.twiced.ucoinj.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.PublicKeyDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PublicKeyService;

@Service
@Transactional
public class PublicKeyServiceImpl extends UCoinServiceImpl implements PublicKeyService {

	@Autowired
	private PublicKeyDao dao;

	@Autowired
	private SignatureDao signatureDao;

	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private MerkleService merkleService;

	@Override
	public PublicKey add(PublicKey pubkey, Signature signature) throws ObsoleteDataException, BadSignatureException {
		if (!signature.verify(pubkey, pubkey.getArmored())) {
			throw new BadSignatureException();
		}
		PublicKey stored = dao.getByKeyID(signature.getIssuerKeyId());
		if(stored != null && stored.getSignature().isMoreRecentThan(signature)){
			throw new ObsoleteDataException("A more recent version of this pubkey is already stored");
		} else if (stored == null || stored.getSignature().isLessRecentThan(signature)) {
			if (stored == null){
				signatureDao.save(signature);
				pubkey.setSignature(signature);
				dao.save(pubkey);
				merkleService.put(pubkey);
				if (GlobalConfiguration.getInstance().isManagingALLKeys()) {
					Key k = keyDao.getByKeyId(new KeyId(pubkey.getFingerprint()));
					if (k == null) {
						k = new Key(pubkey.getFingerprint());
						k.setManaged(true);
						keyDao.save(k);
					}
				}
			} else {
				signatureDao.save(signature);
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
		return merkleService.getPubkeyMerkle();
	}

	@Override
	public PublicKey getByFingerprint(String fingerprint) {
		return dao.getByFingerprint(fingerprint);
	}

	@Override
	public PublicKey getBySignature(Signature sig) throws MultiplePublicKeyException, UnknownPublicKeyException {
		List<PublicKey> pubkeys = dao.lookup("0x" + sig.getIssuer());
		if (pubkeys != null && pubkeys.size() > 1) {
			throw new MultiplePublicKeyException("Several keys found for keyID 0x" + sig.getIssuer());
		}
		if (pubkeys != null && pubkeys.size() == 0) {
			throw new UnknownPublicKeyException("Unkown keyID 0x" + sig.getIssuer());
		}
		return pubkeys.get(0);
	}

	@Override
	public PublicKey getWorking(PublicKey pubkey) {
		PublicKey complete;
		try {
			complete = pgpService.extractPublicKey(pubkey.getArmored());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		pubkey.setPGPPublicKey(complete.getPGPPublicKey());
		return pubkey;
	}

	@Override
	public Object all(Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchPubkey(leaves, leaf));
	}

}
