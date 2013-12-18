package fr.twiced.ucoinj.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;

import fr.twiced.ucoinj.GlobalConfiguration;
import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.service.PGPService;

public class UCoinController {

	private PGPPrivateKey privateKey;
	
	private PGPService pgpService;
	
	public UCoinController(PGPService pgpService) throws PGPException, IOException {
		this.pgpService = pgpService;
		String privateKeyStream = GlobalConfiguration.getInstance().getPrivateKey();
		if(!(privateKeyStream == null || privateKeyStream.isEmpty())){
			String password = GlobalConfiguration.getInstance().getPGPPassword();
			privateKey = pgpService.extractPrivateKey(privateKeyStream, password);
		}
	}

	public PGPPrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public PGPService getPgpService() {
		return pgpService;
	}
	
	protected void objectOrNotFound(Object o, HttpServletRequest request, HttpServletResponse response, Boolean nice) {
		if (o != null)
			sendResult(o, request, response, nice);
		else
			sendError(404, response);
	}

	protected void sendResult(Jsonable o, HttpServletRequest request, HttpServletResponse response, Boolean nice) {
		HTTPSignedProcessor.send(o, request, response, pgpService, privateKey, nice);
	}
	
	protected void sendResult(Object o, HttpServletRequest request, HttpServletResponse response, Boolean nice) {
		HTTPSignedProcessor.send(o, request, response, pgpService, privateKey, nice);
	}
	
	protected void sendResult(Object o, HttpServletRequest request, HttpServletResponse response) {
		HTTPSignedProcessor.send(o, request, response, pgpService, privateKey);
	}

	protected void sendResult(String s, HttpServletRequest request, HttpServletResponse response) {
		HTTPSignedProcessor.send(s, request, response, pgpService, privateKey);
	}

	protected void sendError(int code, String errorMessage, HttpServletResponse response) {
		try {
			response.sendError(code, errorMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void sendError(int code, HttpServletResponse response) {
		try {
			response.sendError(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
