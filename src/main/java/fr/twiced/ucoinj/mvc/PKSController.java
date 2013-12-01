package fr.twiced.ucoinj.mvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadParametersException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@Controller
public class PKSController extends UCoinController {
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	public PKSController(PGPService pgpService) throws PGPException, IOException {
		super(pgpService);
	}
	
	@RequestMapping("/pks/add")
	public void pubkey(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("keytext") String keyStream,
		@RequestParam("keysign") String signatureStream) {
		try{
			if(keyStream == null){
				throw new BadParametersException("keytext is required");
			}
			if(signatureStream == null){
				throw new BadParametersException("keysign is required");
			}
			PublicKey pubkey = getPgpService().extractPublicKey(keyStream);
			Signature signature = new Signature(signatureStream);
			pubkey = pksService.add(pubkey, signature);
			sendResult(pubkey.getJSONObject(), request, response);
		} catch(NoPublicKeyPacketException | BadParametersException e){
			sendError(501, e.getMessage(), response);
		} catch (ObsoleteDataException e) {
			sendError(400, e.getMessage(), response);
		} catch (Exception e) {
			e.printStackTrace();
			sendError(400, response);
		}
	}
	
	@RequestMapping(value = "/pks/lookup", method = RequestMethod.GET)
	public void lookup(
		HttpServletRequest request,
		HttpServletResponse response,
		@RequestParam("search") String search) {
		List<Object> keys = new ArrayList<>();
		for (PublicKey pk : pksService.lookup(search)) {
			keys.add(pk.getJSONObject());
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("keys", keys);
		sendResult(map, request, response);
	}

	@RequestMapping(value = "/pks/all", method = RequestMethod.GET)
	public void lookup(
		HttpServletRequest request,
		HttpServletResponse response,
		Integer lstart,
		Integer lend,
		Integer start,
		Integer end,
		Boolean extract,
		Boolean nice) {
		Jsonable merkle = merkleService.searchPubkey(lstart, lend, start, end, extract);
		sendResult(merkle, request, response, nice);
	}
}
