package fr.twiced.ucoinj.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PublicKeyService;

@Controller
public class TestController {
	
	@Autowired
	private PublicKeyService pubkeyService;
	
	private PGPService pgpService;
	
	private PGPPrivateKey privateKey;
	
	@Autowired
	public TestController(PGPService pgpService) throws PGPException, IOException {
		super();
		this.pgpService = pgpService;
		String privateKeyStream = GlobalConfiguration.getInstance().getPrivateKey();
		String password = GlobalConfiguration.getInstance().getPGPPassword();
		privateKey = pgpService.extractPrivateKey(privateKeyStream, password);
	}

	@RequestMapping("/")
	public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String fingerprint = "67EC0AC5186681A8177BFC006B1F28CA51D4363C";
		PublicKey pk = pubkeyService.getByFingerprint(fingerprint);
		if (pk == null) {
			pk = new PublicKey(fingerprint);
			pubkeyService.save(pk);
		}
		HTTPSignedProcessor.send(pk, request, response, pgpService, privateKey);
	}
}
