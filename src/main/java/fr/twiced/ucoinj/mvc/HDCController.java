package fr.twiced.ucoinj.mvc;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.id.AmendmentId;
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
		objectOrNotFound(hdcService.current(), request, response);
	}
	
	@RequestMapping(value = "/hdc/amendments/promoted", method = RequestMethod.GET)
	public void promoted(
		HttpServletRequest request,
		HttpServletResponse response) {
		objectOrNotFound(hdcService.promoted(), request, response);
	}
	
	@RequestMapping(value = "/hdc/amendments/promoted/{number}", method = RequestMethod.GET)
	public void promotedNumber(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("number") int number) {
		objectOrNotFound(hdcService.promoted(number), request, response);
	}
	
	@RequestMapping(value = "/hdc/amendments/view/{amendment_id}/self", method = RequestMethod.GET)
	public void viewSelf(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId) {
		objectOrNotFound(hdcService.viewSelf(amId), request, response);
	}
	
	private void objectOrNotFound(Object o, HttpServletRequest request, HttpServletResponse response) {
		if (o != null)
			sendResult(o, request, response, true);
		else
			sendError(404, response);
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(AmendmentId.class, new PropertyEditorSupport() {
	        @Override
	        public String getAsText() {
	            return ((AmendmentId) this.getValue()).toString();
	        }

	        @Override
	        public void setAsText(String text) throws IllegalArgumentException {
	        	Pattern p = Pattern.compile("(\\d+)-([A-Z0-9]{40})");
	        	Matcher m = p.matcher(text);
	        	if (!m.matches()) {
	        		throw new IllegalArgumentException();
	        	}
	        	try {
		            setValue(new AmendmentId(Integer.parseInt(m.group(1)), m.group(2)));
	        	} catch(Exception e) {
	        		throw new IllegalArgumentException();
	        	}
	        }
	    });
	}
}

