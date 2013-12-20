package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.ForwardType;
import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.dao.ForwardDao;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;
import fr.twiced.ucoinj.service.UCGService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class ForwardTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private UCGService ucgService;
	
	@Autowired
	private ForwardDao fwdDao;

	@Test
	public void parseForwardALL() throws Exception {
		String rawForward = readFile(getClass().getResource("/data/forwards/cat.ALL.key1"));
		Signature sig = new Signature();
		Forward fw = new Forward(rawForward, sig);
		Assert.assertNotNull(sig);
		Assert.assertNotNull(fw);
		Assert.assertEquals(rawForward, fw.getRaw());
		Assert.assertEquals(new Integer(1), fw.getVersion());
		Assert.assertEquals("beta_brousouf", fw.getCurrency());
		Assert.assertEquals("B029D8669197FAF85E82457A15ED9C87C69E2EA0", fw.getFrom());
		Assert.assertEquals("97FB029D8E2E824576691AFED9C85E7C69A158A0", fw.getTo());
		Assert.assertEquals(ForwardType.ALL, fw.getForward());
		Assert.assertEquals(0, fw.getKeys().size());
	}

	@Test
	public void parseForwardKEYS() throws Exception {
		String rawForward = readFile(getClass().getResource("/data/forwards/KEYS"));
		Signature sig = new Signature();
		Forward fw = new Forward(rawForward, sig);
		Assert.assertNotNull(sig);
		Assert.assertNotNull(fw);
		Assert.assertEquals(rawForward, fw.getRaw());
		Assert.assertEquals(new Integer(1), fw.getVersion());
		Assert.assertEquals("beta_brousouf", fw.getCurrency());
		Assert.assertEquals("B029D8669197FAF85E82457A15ED9C87C69E2EA0", fw.getFrom());
		Assert.assertEquals("97FB029D8E2E824576691AFED9C85E7C69A158A0", fw.getTo());
		Assert.assertEquals(ForwardType.KEYS, fw.getForward());
		Assert.assertEquals(4, fw.getKeys().size());
		Assert.assertEquals("BD90738950D93F0E9A552E77C7CBE17CDA7AF39C", fw.getKeys().get(0));
		Assert.assertEquals("50D346DB019B2A8769815BCAC9F6769A0DB32BD7", fw.getKeys().get(1));
		Assert.assertEquals("5AC3F42C5F60A245A06B71D123AD9D12343F520F", fw.getKeys().get(2));
		Assert.assertEquals("7A81AF3E591AC713F81EA1EFE93DCF36157D8376", fw.getKeys().get(3));
	}

	@Test
	public void submitForwardALL() throws Exception {
		addPubkey("cat");
		sendALLForwardKey1();
	}

	@Test
	public void submitForwardKeys() throws Exception {
		addPubkey("cat");
		sendKEYSForward();
	}

	@Test
	public void submitForwardAllThenKeys() throws Exception {
		addPubkey("cat");
		sendALLForwardKey1();
		sendKEYSForward();
	}

	@Test(expected = ObsoleteDataException.class)
	public void submitForwardKeysThenALL() throws Exception {
		// Obsolete data do NOT override new one
		addPubkey("cat");
		sendKEYSForward();
		sendALLForwardKey1();
	}

	@Test
	public void submitForwardALLSeveralTimes() throws Exception {
		// Should not be a problem, it just does nothing
		addPubkey("cat");
		sendALLForwardKey1();
		sendALLForwardKey1();
		sendALLForwardKey1();
	}

	@Test
	public void submitSeveralForwardSameFrom() throws Exception {
		// Should not be a problem, it just does nothing
		addPubkey("cat");
		sendALLForwardKey1();
		sendALLForwardKey2();
	}
	
	private void sendALLForwardKey1() throws Exception {
		String rawForward = readFile(getClass().getResource("/data/forwards/cat.ALL.key1"));
		String rawSignature = readFile(getClass().getResource("/data/forwards/cat.ALL.key1.asc"));
		Signature sig = new Signature(rawSignature);
		Forward fw = new Forward(rawForward, sig);
		ucgService.addForward(fw, sig);
		fw = fwdDao.getByKeyIds(fw.getFromKeyId(), fw.getToKeyId());
		Assert.assertNotNull(fw);
		Assert.assertEquals(rawForward, fw.getRaw());
		Assert.assertEquals(new Integer(1), fw.getVersion());
		Assert.assertEquals("beta_brousouf", fw.getCurrency());
		Assert.assertEquals("B029D8669197FAF85E82457A15ED9C87C69E2EA0", fw.getFrom());
		Assert.assertEquals("97FB029D8E2E824576691AFED9C85E7C69A158A0", fw.getTo());
		Assert.assertEquals(ForwardType.ALL, fw.getForward());
		Assert.assertEquals(0, fw.getKeys().size());
	}
	
	private void sendALLForwardKey2() throws Exception {
		String rawForward = readFile(getClass().getResource("/data/forwards/cat.ALL.key2"));
		String rawSignature = readFile(getClass().getResource("/data/forwards/cat.ALL.key2.asc"));
		Signature sig = new Signature(rawSignature);
		Forward fw = new Forward(rawForward, sig);
		ucgService.addForward(fw, sig);
		fw = fwdDao.getByKeyIds(fw.getFromKeyId(), fw.getToKeyId());
		Assert.assertNotNull(fw);
	}
	
	private void sendKEYSForward() throws Exception {
		String rawForward = readFile(getClass().getResource("/data/forwards/KEYS"));
		String rawSignature = readFile(getClass().getResource("/data/forwards/KEYS.asc"));
		Signature sig = new Signature(rawSignature);
		Forward fw = new Forward(rawForward, sig);
		ucgService.addForward(fw, sig);
		fw = fwdDao.getByKeyIds(fw.getFromKeyId(), fw.getToKeyId());
		Assert.assertEquals(rawForward, fw.getRaw());
		Assert.assertEquals(new Integer(1), fw.getVersion());
		Assert.assertEquals("beta_brousouf", fw.getCurrency());
		Assert.assertEquals("B029D8669197FAF85E82457A15ED9C87C69E2EA0", fw.getFrom());
		Assert.assertEquals("97FB029D8E2E824576691AFED9C85E7C69A158A0", fw.getTo());
		Assert.assertEquals(ForwardType.KEYS, fw.getForward());
		Assert.assertEquals(4, fw.getKeys().size());
		Assert.assertEquals("BD90738950D93F0E9A552E77C7CBE17CDA7AF39C", fw.getKeys().get(0));
		Assert.assertEquals("50D346DB019B2A8769815BCAC9F6769A0DB32BD7", fw.getKeys().get(1));
		Assert.assertEquals("5AC3F42C5F60A245A06B71D123AD9D12343F520F", fw.getKeys().get(2));
		Assert.assertEquals("7A81AF3E591AC713F81EA1EFE93DCF36157D8376", fw.getKeys().get(3));
	}
	
	private void addPubkey(String name) throws Exception {
		URL pubURL = getClass().getResource("/data/pubkeys/" + name + ".pub");
		URL sigURL = getClass().getResource("/data/signatures/pubkeys/" + name + ".asc");
		String armoredPub = readFile(pubURL);
		String armoredSig = readFile(sigURL);
		PublicKey pubkey = pgpService.extractPublicKey(armoredPub);
		Assert.assertNotNull(pksService.add(pubkey, new Signature(armoredSig)));
	}

	static String readFile(URL path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
