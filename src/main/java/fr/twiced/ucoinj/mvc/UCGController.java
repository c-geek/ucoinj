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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.service.PGPService;

@Controller
public class UCGController extends UCoinController {
	
	private String armoredPubkey;
	private PublicKey pubkey;
	
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
		remote.put("host", conf.getRemoteHost());
		remote.put("ipv4", conf.getRemoteIPv4());
		remote.put("ipv6", conf.getRemoteIPv6());
		remote.put("port", conf.getRemotePort());
		map.put("currency", conf.getCurrency());
		map.put("key", pubkey.getFingerprint());
		map.put("remote", remote);
		sendResult(map, request, response);
	}
}
