package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class PKSServiceTest extends UCoinTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;

	@Test
	public void pksAddCat() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		pksAdd("cat");
	}

	@Test
	public void pksAddCgeek() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		pksAdd("cgeek");
	}

	@Test
	public void pksAddSnow() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		pksAdd("snow");
	}

	@Test
	public void pksAddTobi() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		pksAdd("tobi");
	}

	@Test
	public void pksAddUbot1() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		pksAdd("ubot1");
	}

	public void pksAdd(String keyName) throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException, URISyntaxException {
		String armoredPub = readFile("/data/pubkeys/" + keyName + ".pub");
		String armoredSig = readFile("/data/signatures/pubkeys/" + keyName + ".asc");
		PublicKey pubkey = pgpService.extractPublicKey(armoredPub);
		Assert.assertNotNull(pksService.add(pubkey, new Signature(armoredSig)));
	}
}
