package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Node;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.dao.MerkleDao;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class MerkleTest {

	@Autowired
	private PGPService pgpService;

	@Autowired
	private PKSService pksService;

	@Autowired
	private MerkleService merkleService;

	@Autowired
	private MerkleDao<PublicKey> merkleDao;

	@Test
	public void pksAdd1() throws Exception {
		addKey("tobi");
		assertRoot("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		addKey("cgeek");
		assertRoot("586C27C3776946C942FE0D782FDAA1D494302DAB");
	}

	@Test
	public void pksAdd2() throws Exception {
		addKey("tobi");
		assertRoot("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		addKey("snow");
		assertRoot("DC7A9229DFDABFB9769789B7BFAE08048BCB856F");
		addKey("cat");
		assertRoot("F5ACFD67FC908D28C0CFDAD886249AC260515C90");
		addKey("cgeek");
		List<Node> leaves = merkleDao.getLeaves(merkleService.getPubkeyMerkle(), 0, 4);
		Assert.assertEquals(4, leaves.size());
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", leaves.get(0).getHash());
		Assert.assertEquals("31A6302161AC8F5938969E85399EB3415C237F93", leaves.get(1).getHash());
		Assert.assertEquals("33BBFC0C67078D72AF128B5BA296CC530126F372", leaves.get(2).getHash());
		Assert.assertEquals("C73882B64B7E72237A2F460CE9CAB76D19A8651E", leaves.get(3).getHash());
		assertRoot("F92B6F81C85200250EE51783F5F9F6ACA57A9AFF");
	}

	@Test
	public void pksAdd5() throws Exception {
		addKey("tobi");
		assertRoot("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		addKey("cgeek");
		assertRoot("586C27C3776946C942FE0D782FDAA1D494302DAB");
		addKey("snow");
		addKey("sparkle");
		addKey("cat");
		addKey("ubot1");
		addKey("nukee");
		assertRoot("7E9130D9DDC873EAF13DBF40BDD04D2DD227401D");
	}
	
	private void assertRoot(String root){
		Assert.assertEquals(root, merkleService.getPubkeyMerkle().getRoot().getHash());
	}
	
	private void addKey(String keyName) throws Exception {
		URL pubURL = getClass().getResource("/data/pubkeys/" + keyName + ".pub");
		URL sigURL = getClass().getResource("/data/signatures/pubkeys/" + keyName + ".asc");
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
