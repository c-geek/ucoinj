package fr.twiced.ucoinj.service.impl;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;

import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Status;
import fr.twiced.ucoinj.bean.THTEntry;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.ForwardDao;
import fr.twiced.ucoinj.dao.PeerDao;
import fr.twiced.ucoinj.dao.SignatureDao;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.MultiplePublicKeyException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PublicKeyService;
import fr.twiced.ucoinj.service.UCGService;

@Service
@Transactional
public class UCGServiceImpl implements UCGService {

	private static final Logger log = LoggerFactory.getLogger(UCGServiceImpl.class);
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	private PeerDao peerDao;
	
	@Autowired
	private ForwardDao forwardDao;
	
	@Autowired
	private SignatureDao sigDao;
	
	@Autowired
	private PublicKeyService pubkeyService;
	
	private Peer peer;

	@Override
	public PublicKey pubkey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object keys(Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchManagedKey(leaves, leaf));
	}

	@Override
	public Peer peer() throws PGPException, IOException, NoPublicKeyPacketException, SignatureException, BadSignatureException, UnknownPublicKeyException, ObsoleteDataException, MultiplePublicKeyException {
		if (peer == null) {
	        // Prepare data
			GlobalConfiguration config = GlobalConfiguration.getInstance();
			// Need to self-publish own pubkey, to check future peering entry
			String armoredPubKey = config.getPublicKey().getArmored();
			Signature sigPK = new Signature(new PGPServiceImpl().sign(armoredPubKey, config.getPGPPrivateKey()));
			pubkeyService.add(config.getPublicKey(), sigPK);
			// Create Peering entry
	        String fingerprint = config.getPublicKey().getFingerprint();
	        Peer stored = peerDao.getByKeyId(new KeyId(fingerprint));
	        Peer computed = new Peer();
	        computed.setCurrency(config.getCurrency());
	        computed.setVersion(1);
	        computed.setFingerprint(fingerprint);
	        computed.setDns(config.getRemoteHost());
	        computed.setIpv4(config.getRemoteIPv4());
	        computed.setIpv6(config.getRemoteIPv6());
	        computed.setPort(config.getPort());
	        Signature sig = new Signature(new PGPServiceImpl().sign(computed.getRaw(), config.getPGPPrivateKey()));
	        // Configuration changed?
	        if (stored == null || !stored.getHash().equals(computed.getHash())) {
	        	addPeer(computed, sig);
	        	peer = computed;
	        } else {
	        	peer = stored;
	        }
		}
		return peer;
	}

	@Override
	public Object peers(Boolean leaves, String leaf) throws UnknownLeafException {
		return jsonIt(merkleService.searchPeer(leaves, leaf));
	}

	@Override
	public void addPeer(Peer peer, Signature sig) throws BadSignatureException, UnknownPublicKeyException, MultiplePublicKeyException, ObsoleteDataException {
		PublicKey pubkey = pubkeyService.getWorking(pubkeyService.getBySignature(sig));
		if (!sig.verify(pubkey, peer.getRaw())) {
			throw new BadSignatureException("Bad signature for peering entry");
		}
		// Already stored?
		Peer stored = peerDao.getByKeyId(peer.getKeyId());
		if(stored != null && stored.getSignature().isMoreRecentThan(sig)){
			throw new ObsoleteDataException("A more recent entry is already stored");
		} else if (stored == null || stored.getSignature().isLessRecentThan(sig)) {
			if (stored != null) {
				// Remove previous entry
				sigDao.delete(stored.getSignature());
				peerDao.delete(stored);
			}
			log.info(String.format("Saving new entry of %s", peer.getFingerprint()));
			sigDao.save(sig);
			peer.setSignature(sig);
			peerDao.save(peer);
			if (stored == null) {
				// Update Merkle
				merkleService.putPeer(peer);
			}
		}
	}

	@Override
	public List<Peer> upstream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Peer> upstream(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Peer> downstream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Peer> downstream(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addForward(Forward forward, Signature sig)  throws BadSignatureException, UnknownPublicKeyException, MultiplePublicKeyException, ObsoleteDataException {
		PublicKey pubkey = pubkeyService.getWorking(pubkeyService.getBySignature(sig));
		if (!sig.verify(pubkey, forward.getRaw())) {
			throw new BadSignatureException("Bad signature for peering entry");
		}
		// Already stored?
		Forward stored = forwardDao.getByKeyIds(forward.getFromKeyId(), forward.getToKeyId());
		if(stored != null && stored.getSignature().isMoreRecentThan(sig)){
			throw new ObsoleteDataException("A more recent entry is already stored");
		} else if (stored == null || stored.getSignature().isLessRecentThan(sig)) {
			if (stored != null) {
				// Remove previous entry
				forwardDao.delete(stored);
				sigDao.delete(stored.getSignature());
			}
			log.info(String.format("Saving new entry of %s", forward.getFingerprint()));
			sigDao.save(sig);
			forward.setSignature(sig);
			forwardDao.save(forward);
			if (stored == null) {
				// Update Merkle
				merkleService.putForward(forward);
			}
		}
	}

	@Override
	public void addStatus(Status status, Signature sig) {
		// TODO Auto-generated method stub

	}

	@Override
	public Merkle<THTEntry> tht() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTHT(THTEntry entry, Signature sig) {
		// TODO Auto-generated method stub

	}

	@Override
	public THTEntry tht(KeyId id) {
		// TODO Auto-generated method stub
		return null;
	}

	private Object jsonIt(Jsonable jsonable) {
		if (jsonable != null) {
			return jsonable.getJSON();
		}
		return null;
	}

}