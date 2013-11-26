package fr.twiced.ucoinj.mvc;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.openpgp.PGPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadParametersException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@Controller
public class PKSController extends UCoinController {
	
	@Autowired
	private PKSService pksService;
	
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
}
