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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.AmendmentId;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.PGPService;

@Controller
public class HDCController extends UCoinController {

    private static final Logger log = LoggerFactory.getLogger(HDCController.class);
	
	@Autowired
	private HDCService hdcService;
	
	@Autowired
	public HDCController(PGPService pgpService) throws PGPException, IOException {
		super(pgpService);
	}
	
	@RequestMapping(value = "/hdc/amendments/votes", method = RequestMethod.GET)
	public void amendmentGet(
			HttpServletRequest request,
			HttpServletResponse response) {
		objectOrNotFound(hdcService.votes(), request, response, true);
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
	
	@RequestMapping(value = "/hdc/amendments/current/votes", method = RequestMethod.GET)
	public void viewCurrentVotes(
		HttpServletRequest request,
		HttpServletResponse response,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.viewCurrentVoters(lstart, lend, start, end, extract), request, response, nice);
	}
	
	@RequestMapping(value = "/hdc/amendments/current", method = RequestMethod.GET)
	public void current(
		HttpServletRequest request,
		HttpServletResponse response) {
		objectOrNotFound(hdcService.current(), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/amendments/promoted", method = RequestMethod.GET)
	public void promoted(
		HttpServletRequest request,
		HttpServletResponse response) {
		objectOrNotFound(hdcService.promoted(), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/amendments/promoted/{number}", method = RequestMethod.GET)
	public void promotedNumber(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("number") int number) {
		objectOrNotFound(hdcService.promoted(number), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/amendments/view/{amendment_id}/self", method = RequestMethod.GET)
	public void viewSelf(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId) {
		objectOrNotFound(hdcService.viewSelf(amId), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/amendments/view/{amendment_id}/members", method = RequestMethod.GET)
	public void viewMembers(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.viewMembers(amId, lstart, lend, start, end, extract), request, response, nice);
	}
	
	@RequestMapping(value = "/hdc/amendments/view/{amendment_id}/voters", method = RequestMethod.GET)
	public void viewVoters(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.viewVoters(amId, lstart, lend, start, end, extract), request, response, nice);
	}
	
	@RequestMapping(value = "/hdc/amendments/view/{amendment_id}/signatures", method = RequestMethod.GET)
	public void viewSignatures(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.viewSignatures(amId, lstart, lend, start, end, extract), request, response, nice);
	}
	
	@RequestMapping(value = "/hdc/amendments/votes/{amendment_id}", method = RequestMethod.GET)
	public void viewVotes(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("amendment_id") AmendmentId amId,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.viewVotes(amId, lstart, lend, start, end, extract), request, response, nice);
	}
	
	@RequestMapping(value = "/hdc/transactions/process", method = RequestMethod.POST)
	public void transactionsProcess(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("transaction") String transactionStream,
		@RequestParam("signature") String signatureStream,
		@RequestParam(value = "peer", required = false) String peerFingerprint) {
		try{
			Signature sig = new Signature(signatureStream);
			Transaction tx = new Transaction(transactionStream, sig);
			hdcService.transactionsProcess(tx, sig);
			Object recorded = hdcService.transaction(tx.getNaturalId());
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("signature", sig);
			map.put("raw", tx.getRaw());
			map.put("transaction", recorded);
			sendResult(map, request, response);
		} catch (Exception e) {
			log.warn(e.getMessage());
			sendError(400, e.getMessage(), response);
		}
	}
	
	@RequestMapping(value = "/hdc/coins/{fingerprint}/list", method = RequestMethod.GET)
	public void coinList(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint) {
		objectOrNotFound(hdcService.coinList(new KeyId(fingerprint)), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/coins/{fingerprint}/view/{coin_number}/history", method = RequestMethod.GET)
	public void coinViewHistory(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		@PathVariable("coin_number") Integer coinNumber) {
		objectOrNotFound(hdcService.coinHistory(new CoinId(fingerprint, coinNumber)), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/coins/{fingerprint}/view/{coin_number}", method = RequestMethod.GET)
	public void coinView(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		@PathVariable("coin_number") Integer coinNumber) {
		objectOrNotFound(hdcService.coinView(new CoinId(fingerprint, coinNumber)), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}", method = RequestMethod.GET)
	public void txOfSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.transactionsOfSender(new KeyId(fingerprint), lstart, lend, start, end, extract), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/issuance", method = RequestMethod.GET)
	public void txIssuanceOfSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.transactionsIssuanceOfSender(new KeyId(fingerprint), lstart, lend, start, end, extract), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/issuance/dividend", method = RequestMethod.GET)
	public void txFusionOfSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.transactionsFusionOfSender(new KeyId(fingerprint), lstart, lend, start, end, extract), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/issuance/dividend", method = RequestMethod.GET)
	public void txDividendOfSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.transactionsDividendOfSender(new KeyId(fingerprint), lstart, lend, start, end, extract), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/issuance/dividend/{amendment_number}", method = RequestMethod.GET)
	public void txDividendOfSenderForAm(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint,
		@PathVariable("amendment_number") int amNumber,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		objectOrNotFound(hdcService.transactionsDividendOfSenderForAm(new KeyId(fingerprint), amNumber, lstart, lend, start, end, extract), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/issuance/last", method = RequestMethod.GET)
	public void txLastIssuancefSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint) {
		objectOrNotFound(hdcService.transactionsLastIssuanceOfSender(new KeyId(fingerprint)), request, response, true);
	}
	
	@RequestMapping(value = "/hdc/transactions/sender/{fingerprint}/last", method = RequestMethod.GET)
	public void txLastofSender(
		HttpServletRequest request,
		HttpServletResponse response,
		@PathVariable("fingerprint") String fingerprint) {
		objectOrNotFound(hdcService.transactionsLastOfSender(new KeyId(fingerprint)), request, response, true);
	}
	
	private void objectOrNotFound(Object o, HttpServletRequest request, HttpServletResponse response, Boolean nice) {
		if (o != null)
			sendResult(o, request, response, nice);
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

