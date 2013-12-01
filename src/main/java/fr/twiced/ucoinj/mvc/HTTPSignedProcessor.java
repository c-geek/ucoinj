package fr.twiced.ucoinj.mvc;

import java.io.IOException;
import java.security.SignatureException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.bcpg.PublicKeyPacket;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPUtil;
import org.codehaus.jackson.map.ObjectMapper;

import fr.twiced.ucoinj.bean.Jsonable;
import fr.twiced.ucoinj.service.PGPService;

public class HTTPSignedProcessor {
	
	private static char[] ALPHANUM = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	private static final int boundary_length = 15;
	private static final char NEXT_LINE = '\n';
	private static final String AS_JSON = "application/json";
	private static final String AS_TEXT = "text/plain";

	public static void send(Jsonable o, HttpServletRequest request, HttpServletResponse response, PGPService pgpService, PGPPrivateKey privateKey, Boolean nice) {
		try {
			String jsonResponse;
			if (nice != null && nice) {
				jsonResponse = new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(o.getJSON());
			} else {
				jsonResponse = new ObjectMapper().writeValueAsString(o.getJSON());
			}
			send(jsonResponse, request, response, pgpService, privateKey, AS_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				response.sendError(501, "JSON serialization error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void send(Object obj, HttpServletRequest request, HttpServletResponse response, PGPService pgpService, PGPPrivateKey privateKey){
		try {
			String jsonResponse = new ObjectMapper().writeValueAsString(obj);
			send(jsonResponse, request, response, pgpService, privateKey, AS_JSON);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				response.sendError(501, "JSON serialization error");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void send(String data, HttpServletRequest request, HttpServletResponse response, PGPService pgpService, PGPPrivateKey privateKey){
		send(data, request, response, pgpService, privateKey, AS_TEXT);
	}
	
	public static void send(String data, HttpServletRequest request, HttpServletResponse response, PGPService pgpService, PGPPrivateKey privateKey, String contentType){
		try {
			String accept = request.getHeader("Accept");
			if (accept != null && accept.equals("multipart/signed")) {
				String boundary = getRandomBoundary();
				PublicKeyPacket pubKeyPacket = privateKey.getPublicKeyPacket();
				int hashAlgo = pubKeyPacket.getAlgorithm();
				// Change header
				setHeader(response, boundary, hashAlgo);
				// Send signed response
				String body = signedResponse(data, boundary, privateKey, pgpService);
				response.getWriter().write(body);
			} else {
				response.setHeader("Content-Type", contentType);
				response.getWriter().write(data);
			}
		} catch (IOException | SignatureException | PGPException e) {
			try {
				response.sendError(501, "Unhandled exception");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private static String getRandomBoundary(){
		return getRandomBoundary(boundary_length);
	}
	
	/**
	 * Computes a random boundary
	 * @param length Length of the boundary
	 * @return The boundary string
	 */
	private static String getRandomBoundary(int length){
		char[] boundary = new char[length];
		for (int i = 0; i < length; i++) {
			int index = (int) (Math.floor(Math.random() * 100) % ALPHANUM.length);
			boundary[i] = ALPHANUM[index];
		}
		return new String(boundary);
	}
	
	/**
	 * Write HTTP header according to RFC3156
	 * @param response HTTP response object to be written PGP header
	 * @param boundary The delimiter string used in response body
	 * @param hashAlgorithm The hash algorithm used to compute signature's hash
	 */
	private static void setHeader(HttpServletResponse response, String boundary, int hashAlgorithm){
		String contentType = String.format("multipart/signed; boundary=%s; protocol=\"application/pgp-signature\"; micalg=%s; ", boundary, getHashAlgorithm(hashAlgorithm));
		response.setHeader("Content-Type", contentType);
	}
	
	/**
	 * Get the micalg parameter according to RFC3156
	 * @param hashAlgo Bouncy Castle algo int value.
	 * @return RFC3156 micalg parameter value
	 */
	private static String getHashAlgorithm(int hashAlgo){
		switch (hashAlgo) {
		case PGPUtil.MD5: return "pgp-md5";
		case PGPUtil.SHA1: return "pgp-sha1";
		case PGPUtil.RIPEMD160: return "pgp-ripemd160";
		case PGPUtil.MD2: return "pgp-md2";
		case PGPUtil.TIGER_192: return "pgp-tiger192";
		case PGPUtil.HAVAL_5_160: return "pgp-haval-5-160";
		default:
			return "";
		}
	}
	
	/**
	 * Create a signed HTTP response body
	 * @param data The data to be signed
	 * @param boundary The delimiter boundary string
	 * @param privateKey The private key used for signing
	 * @param pgpService The PGP implementation that create signature using signing key
	 * @return HTTP body response
	 * @throws SignatureException
	 * @throws PGPException
	 * @throws IOException
	 */
	private static String signedResponse(String data, String boundary, PGPPrivateKey privateKey, PGPService pgpService) throws SignatureException, PGPException, IOException {
		StringBuffer sBuff = new StringBuffer();
		String truncatedData = data;
		sBuff.append("--" + boundary + NEXT_LINE);
		sBuff.append(truncatedData + NEXT_LINE);
		sBuff.append(NEXT_LINE);
		sBuff.append("--" + boundary + NEXT_LINE);
		sBuff.append("Content-Type: application/pgp-signature" + NEXT_LINE);
		sBuff.append(NEXT_LINE);
		sBuff.append(pgpService.sign(truncatedData, privateKey));
		sBuff.append(NEXT_LINE);
		sBuff.append("--" + boundary + "--");
		return sBuff.toString();
	}
}
