package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NotVoterException;
import fr.twiced.ucoinj.exceptions.RefusedDataException;
import fr.twiced.ucoinj.exceptions.UnknownPublicKeyException;
import fr.twiced.ucoinj.pgp.Sha1;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class VoteTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private HDCService hdcService;

	@Before
	public void setupPublicKeys() throws Exception {
		// Store PGP key
		URL pubURL = getClass().getResource("/data/pubkeys/tobi.pub");
		URL sigURL = getClass().getResource("/data/signatures/pubkeys/tobi.asc");
		String armoredPub = readFile(pubURL);
		String armoredSig = readFile(sigURL);
		PublicKey pubkey = pgpService.extractPublicKey(armoredPub);
		Assert.assertNotNull(pksService.add(pubkey, new Signature(armoredSig)));
		Assert.assertNotNull(pksService.lookup("0x7CA0ED9C"));
		Assert.assertTrue(pksService.lookup("0x19A8651E").isEmpty());
	}
	
	@Test
	public void voteUpTo0() throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/am0"));
		String signatureStream = readFile(getClass().getResource("/data/signatures/amendments/am0-tobi.asc"));
		Assert.assertEquals("37F4A72F093475C4EDABBF2E45B161AD21E67813", new Sha1(rawAmendment).toString().toUpperCase());
		Assert.assertEquals("D32E4C5BB0890AD8A7AFCA693BD09C4D097F1B6F", new Sha1(signatureStream).toString().toUpperCase());
		Amendment am = new Amendment(rawAmendment);
		Signature sig = new Signature(signatureStream);
		Assert.assertEquals("37F4A72F093475C4EDABBF2E45B161AD21E67813", am.getHash());
		Assert.assertEquals("37F4A72F093475C4EDABBF2E45B161AD21E67813", new Sha1(am.getRaw()).toString().toUpperCase());
		hdcService.vote(am, sig);
		Assert.assertNotNull(hdcService.current());
		Assert.assertNotNull(hdcService.promoted());
		Assert.assertNotNull(hdcService.promoted(0));
		Assert.assertNull(hdcService.promoted(1));
		Assert.assertNull(hdcService.promoted(2));
		Assert.assertNull(hdcService.promoted(10));
		Assert.assertNull(hdcService.promoted(899));
		Assert.assertNull(hdcService.promoted(-1));
	}
	
	@Test
	public void voteUpTo1() throws Exception {
		vote("am0", "am0-tobi.asc");
		Assert.assertNotNull(hdcService.current());
		Assert.assertNotNull(hdcService.promoted());
		Assert.assertNotNull(hdcService.promoted(0));
		Assert.assertNull(hdcService.promoted(1));
		Assert.assertNull(hdcService.promoted(2));
		Assert.assertNull(hdcService.promoted(10));
		Assert.assertNull(hdcService.promoted(899));
		Assert.assertNull(hdcService.promoted(-1));
		vote("am1", "am1-tobi.asc");
		Assert.assertNotNull(hdcService.current());
		Assert.assertNotNull(hdcService.promoted());
		Assert.assertNotNull(hdcService.promoted(0));
		Assert.assertNotNull(hdcService.promoted(1));
		Assert.assertNull(hdcService.promoted(2));
		Assert.assertNull(hdcService.promoted(10));
		Assert.assertNull(hdcService.promoted(899));
		Assert.assertNull(hdcService.promoted(-1));
	}
	
//	@Test(expected = NotVoterException.class)
//	public void voteUpTo1WithCat() throws Exception {
//		vote("am0", "am0-tobi.asc");
//		vote("am1", "am1-tobi.asc");
//		vote("am1", "am1-cat.asc"); // Not authorized yet
//	}
//	
	private void vote(String amFile, String voteFile) throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/" + amFile));
		String signatureStream = readFile(getClass().getResource("/data/signatures/amendments/" + voteFile));
		Amendment am = new Amendment(rawAmendment);
		Signature sig = new Signature(signatureStream);
		hdcService.vote(am, sig);
	}
	
	@Test(expected = BadSignatureException.class)
	public void voteWithBadSignature() throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/am0"));
		String signatureStream = readFile(getClass().getResource("/data/signatures/wrong-signature-tobi.asc"));
		hdcService.vote(new Amendment(rawAmendment), new Signature(signatureStream));
	}
	
	@Test(expected = UnknownPublicKeyException.class)
	public void voteWithUnknownKey() throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/am0"));
		String signatureStream = readFile(getClass().getResource("/data/signatures/wrong-signature-cat.asc"));
		hdcService.vote(new Amendment(rawAmendment), new Signature(signatureStream));
	}
	
	@Test(expected = RefusedDataException.class)
	public void voteWithUnknownPreviousAmendment() throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/am2.random"));
		String signatureStream = readFile(getClass().getResource("/data/signatures/amendments/am2.random.asc"));
		hdcService.vote(new Amendment(rawAmendment), new Signature(signatureStream));
	}

	public void parseAmendment(String amName, String sigName) throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/" + amName));
		String signatureStream = readFile(getClass().getResource("/data/signatures/amendments/" + sigName));
		Amendment am = new Amendment(rawAmendment);
		Assert.assertNotNull(am);
		Signature sig = new Signature(signatureStream);
		Assert.assertNotNull(sig);
		Assert.assertEquals(rawAmendment, am.getRaw());
	}

	static String readFile(URL path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		return StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
