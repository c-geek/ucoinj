package fr.twiced.ucoinj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.service.PublicKeyService;

@Controller
public class TestController {
	
	@Autowired
	private PublicKeyService pubkeyService;

	@RequestMapping("/")
	public @ResponseBody PublicKey home() {
		String fingerprint = "67EC0AC5186681A8177BFC006B1F28CA51D4363C";
		PublicKey pk = pubkeyService.getByFingerprint(fingerprint);
		if (pk == null) {
			pk = new PublicKey(fingerprint);
			pubkeyService.save(pk);
		}
		return pk;
	}
}
