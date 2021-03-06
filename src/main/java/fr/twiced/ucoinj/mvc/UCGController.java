package fr.twiced.ucoinj.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PublicKeyService;
import fr.twiced.ucoinj.service.UCGService;

@Controller
public class UCGController extends UCoinController {

    private static final Logger log = LoggerFactory.getLogger(UCGController.class);
	
	private String armoredPubkey;
	private PublicKey pubkey;
	
	@Autowired
	private UCGService ucgService;
	
	@Autowired
	private HDCService hdcService;
	
	@Autowired
	private PublicKeyService pksService;
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	private AmendmentDao amDao;
	
	@Autowired
	public UCGController(PGPService pgpService) throws PGPException, IOException, NoPublicKeyPacketException {
		super(pgpService);
		if(getPrivateKey() != null){
			// Computes armored public key from private key
			ByteArrayOutputStream armoredOut = new ByteArrayOutputStream();
			OutputStream out = new ArmoredOutputStream(armoredOut);
			out.write(getPrivateKey().getPublicKeyPacket().getEncoded());
			out.close();
			armoredPubkey = armoredOut.toString();
			pubkey = pgpService.extractPublicKey(armoredPubkey);
		}
	}
	
	@RequestMapping("/ucg/pubkey")
	public void pubkey(HttpServletRequest request, HttpServletResponse response) throws NoPublicKeyPacketException, IOException{
		if(armoredPubkey != null){
			sendResult(armoredPubkey, request, response);
		} else {
			sendError(501, "Public key not available", response);
		}
	}
	
	@RequestMapping("/ucg/peering")
	public void peering(HttpServletRequest request, HttpServletResponse response) throws NoPublicKeyPacketException, IOException{
		GlobalConfiguration conf = GlobalConfiguration.getInstance();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> remote = new HashMap<String, Object>();
		Map<String, Object> am = new HashMap<String, Object>();
		Map<String, Object> merkles = new HashMap<String, Object>();
		Amendment current = amDao.getCurrent();
		remote.put("host", conf.getRemoteHost());
		remote.put("ipv4", conf.getRemoteIPv4());
		remote.put("ipv6", conf.getRemoteIPv6());
		remote.put("port", conf.getRemotePort());
		am.put("currentNumber", current == null ? -1 : current.getNumber());
		am.put("hash", current == null ? "" : current.getHash());
		try {
			merkles.put("pks/all", pksService.all(false, null));
		} catch (UnknownLeafException e) {
		}
		try {
			if (current != null) {
				merkles.put("hdc/amendments/current/votes", hdcService.viewVotes(current.getNaturalId(), false, null));
			} else {
				merkles.put("hdc/amendments/current/votes", new Merkle<>().getJSON());
			}
		} catch (UnknownLeafException e) {
		}
		map.put("currency", conf.getCurrency());
		map.put("key", pubkey.getFingerprint());
		map.put("remote", remote);
		map.put("contract", am);
		map.put("merkles", merkles);
		sendResult(map, request, response, true);
	}
	
	@RequestMapping(value = "/ucg/peering/keys", method = RequestMethod.GET)
	public void peeringKeys(
		HttpServletRequest request,
		HttpServletResponse response,
		Boolean leaves,
		String leaf,
		Boolean nice) {
		try {
			objectOrNotFound(ucgService.keys(leaves, leaf), request, response, true);
		} catch (UnknownLeafException e) {
			sendError(404, "Leaf not found", response);
		}
	}
	
	@RequestMapping(value = "/ucg/peering/peer", method = RequestMethod.GET)
	public void peeringPeer(
		HttpServletRequest request,
		HttpServletResponse response) {
		try {
			objectOrNotFound(ucgService.peer().getJSON(), request, response, true);
		} catch (Exception e) {
			e.printStackTrace();
			sendError(500, "Can't produce Peering entry", response);
		}
	}
	
	@RequestMapping(value = "/ucg/peering/peers", method = RequestMethod.POST)
	public void peersPost(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("entry") String entryStream,
		@RequestParam("signature") String signatureStream) {
		try{
			Signature sig = new Signature(signatureStream);
			Peer peer = new Peer(entryStream, sig);
			ucgService.addPeer(peer, sig);
			sendResult(peer.getJSON(), request, response);
		} catch (Exception e) {
			log.warn(e.getMessage());
			sendError(400, e.getMessage(), response);
		}
	}
	
	@RequestMapping(value = "/ucg/peering/peers", method = RequestMethod.GET)
	public void peeringPeers(
		HttpServletRequest request,
		HttpServletResponse response,
		Boolean leaves,
		String leaf,
		Boolean nice) {
		try {
			objectOrNotFound(ucgService.peers(leaves, leaf), request, response, true);
		} catch (UnknownLeafException e) {
			sendError(404, "Leaf not found", response);
		}
	}
	
	@RequestMapping(value = "/ucg/peering/forward", method = RequestMethod.POST)
	public void forwardPost(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("forward") String forwardStream,
		@RequestParam("signature") String signatureStream) {
		try{
			Signature sig = new Signature(signatureStream);
			Forward fwd = new Forward(forwardStream, sig);
			ucgService.addForward(fwd, sig);
			sendResult(fwd.getJSON(), request, response);
		} catch (Exception e) {
			log.warn(e.getMessage());
			sendError(400, e.getMessage(), response);
		}
	}
	
	@RequestMapping(value = "/ucg/peering/peers/upstream", method = RequestMethod.GET)
	public void peeringPeersUpstream(
		HttpServletRequest request,
		HttpServletResponse response) throws PGPException, IOException, NoPublicKeyPacketException {
		KeyId self = new KeyId(GlobalConfiguration.getInstance().getPublicKey().getFingerprint());
		objectOrNotFound(ucgService.upstream(self), request, response, true);
	}
	
	@RequestMapping(value = "/ucg/peering/peers/upstream/{fingerprint}", method = RequestMethod.GET)
	public void peeringPeersUpstream(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint) throws PGPException, IOException, NoPublicKeyPacketException {
		KeyId self = new KeyId(GlobalConfiguration.getInstance().getPublicKey().getFingerprint());
		objectOrNotFound(ucgService.upstreamForKey(self, new KeyId(fingerprint)), request, response, true);
	}
	
	@RequestMapping(value = "/ucg/peering/peers/downstream", method = RequestMethod.GET)
	public void peeringPeersDownstream(
		HttpServletRequest request,
		HttpServletResponse response) throws PGPException, IOException, NoPublicKeyPacketException {
		KeyId self = new KeyId(GlobalConfiguration.getInstance().getPublicKey().getFingerprint());
		objectOrNotFound(ucgService.downstream(self), request, response, true);
	}
	
	@RequestMapping(value = "/ucg/peering/peers/downstream/{fingerprint}", method = RequestMethod.GET)
	public void peeringPeersDownstream(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint) throws PGPException, IOException, NoPublicKeyPacketException {
		KeyId self = new KeyId(GlobalConfiguration.getInstance().getPublicKey().getFingerprint());
		objectOrNotFound(ucgService.downstreamForKey(self, new KeyId(fingerprint)), request, response, true);
	}

	public PublicKey getPubkey() {
		return pubkey;
	}
}
