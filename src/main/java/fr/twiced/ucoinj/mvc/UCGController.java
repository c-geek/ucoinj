package fr.twiced.ucoinj.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.service.PGPService;

@Controller
public class UCGController extends UCoinController {
	
	private String armoredPubkey;
	
	@Autowired
	public UCGController(PGPService pgpService) throws PGPException, IOException {
		super(pgpService);
		if(getPrivateKey() != null){
			// Computes armored public key from private key
			ByteArrayOutputStream armoredOut = new ByteArrayOutputStream();
			OutputStream out = new ArmoredOutputStream(armoredOut);
			out.write(getPrivateKey().getPublicKeyPacket().getEncoded());
			out.close();
			armoredPubkey = armoredOut.toString();
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
}
