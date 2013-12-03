package fr.twiced.ucoinj.mvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.pgp.Sha1;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.PGPService;

@Controller
public class HDCController extends UCoinController {
	
	@Autowired
	private HDCService hdcService;
	
	@Autowired
	public HDCController(PGPService pgpService) throws PGPException, IOException {
		super(pgpService);
	}
	
	@RequestMapping(value = "/hdc/amendments/votes", method = RequestMethod.GET)
	public void amendmentGet(
			HttpServletResponse response) {
		sendError(404, response);
	}
	
	@RequestMapping(value = "/hdc/amendments/votes", method = RequestMethod.POST)
	public void amendmentPost(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("amendment") String amendment,
		@RequestParam("signature") String signatureStream,
		@RequestParam(value = "peer", required = false) String peerFingerprint) {
		try{
			Amendment am = new Amendment(amendment);
			Signature sig = new Signature(signatureStream);
			hdcService.vote(am, sig);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("signature", sig);
			map.put("amendment", amendment);
			sendResult(map, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			sendError(400, response);
		}
	}
	
	@RequestMapping(value = "/hdc/amendments/current", method = RequestMethod.GET)
	public void current(
		HttpServletRequest request,
		HttpServletResponse response) {
		sendResult(hdcService.current(), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/amendments/promoted", method = RequestMethod.GET)
	public void promoted(
		HttpServletRequest request,
		HttpServletResponse response) {
		sendResult(hdcService.promoted(), request, response, true);
	}
}

