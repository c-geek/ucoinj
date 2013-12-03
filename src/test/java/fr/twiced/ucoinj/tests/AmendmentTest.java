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

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class AmendmentTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private HDCService hdcService;

	@Test
	public void parseAmendmentFromRaw() throws Exception {
		parseAmendment("am0", "am0-tobi.asc");
	}

	@Test
	public void parseAmendmentFromRawRandom1() throws Exception {
		parseAmendment("am1", "am0-tobi.asc");
	}

	@Test
	public void parseAmendmentFromRawRandom2() throws Exception {
		parseAmendment("am2.random", "am0-tobi.asc");
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
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
