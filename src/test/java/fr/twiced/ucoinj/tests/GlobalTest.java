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
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.Merkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.MerkleService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class GlobalTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private HDCService hdcService;
	
	@Autowired
	private MerkleService merkleService;
	
	@Autowired
	private TransactionDao txDao;
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private CoinDao coinDao;
	
	@Autowired
	private AmendmentDao amDao;
	
	private static String env = "env0";
	
	private static KeyId tobi = new KeyId("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
	
	private static KeyId cat = new KeyId("C73882B64B7E72237A2F460CE9CAB76D19A8651E");
	
	private static Boolean GET_LEAVES = Boolean.TRUE;
	
	private static String NO_PARTICULAR_LEAF = null;

	@Before
	public void setupPubkeysAndAmendments() throws Exception {
		addPubkey("tobi");
		addPubkey("cat");
		vote("0", "tobi");
		vote("1", "tobi");
		vote("2", "tobi");
		Assert.assertNull(merkleService.getRootTxKeys());
		Assert.assertNull(merkleService.getRootTxAll());
		assertNullTxMerkles(tobi);
		assertNullTxMerkles(cat);
	}
	
	@Test
	public void tobiIssuance0() throws Exception {
		sendTx("tobi", 0);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxAll());
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertNull(merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
		// Merkle leaves' tests
		Merkle<?> merkle = (Merkle<?>) merkleService.searchTxAll(GET_LEAVES, NO_PARTICULAR_LEAF);
		Assert.assertEquals(1, merkle.getLeavesHashes().size());
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkle.getLeavesHashes().get(0));
	}
	
	@Test
	public void tobiIssuance1() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertNull(merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void tobiIssuance2() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("E270831C6510149E0D1F95515776C73C5F332A85", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("3BB31F9967AC940EE64593A1C06BAC627011DBA9", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("E270831C6510149E0D1F95515776C73C5F332A85", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("E270831C6510149E0D1F95515776C73C5F332A85", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("E270831C6510149E0D1F95515776C73C5F332A85", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void tobiIssuance3() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		sendTx("tobi", 3);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("17766A3B1C3DDA1EBD1B1AA424E9B2C0154027AA", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("6441F303F1F837F75C8358AAEA4F71967AE61B0A", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("17766A3B1C3DDA1EBD1B1AA424E9B2C0154027AA", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("17766A3B1C3DDA1EBD1B1AA424E9B2C0154027AA", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("17766A3B1C3DDA1EBD1B1AA424E9B2C0154027AA", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void tobiIssuance4() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		sendTx("tobi", 3);
		sendTx("tobi", 4);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("318926D5E554C576414BD23D034E2D832F10289E", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("811602F730CA2F29067D527F23680F83F322331F", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("318926D5E554C576414BD23D034E2D832F10289E", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("318926D5E554C576414BD23D034E2D832F10289E", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("318926D5E554C576414BD23D034E2D832F10289E", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void tobiIssuance5() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		sendTx("tobi", 3);
		sendTx("tobi", 4);
		sendTx("tobi", 5);
		assertNullTxMerkles(cat);
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", merkleService.getRootTxKeys());
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("455A287907BA562CBF510E016CB9F9FEB15E1D47", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void tobiIssuance6() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		sendTx("tobi", 3);
		sendTx("tobi", 4);
		sendTx("tobi", 5);
		sendTx("tobi", 6);
		Assert.assertEquals("48578F03A46B358C10468E2312A41C6BCAB19417", merkleService.getRootTxKeys());
		Assert.assertEquals("A3BCA251FC7CAF5F9652EDA153B71D6758EEB590", merkleService.getRootTxAll());
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("455A287907BA562CBF510E016CB9F9FEB15E1D47", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("A3BCA251FC7CAF5F9652EDA153B71D6758EEB590", merkleService.getRootTxOfSender(tobi));
		Assert.assertEquals("922880E29C04C0462B17A0E8E82626500184B323", merkleService.getRootTxTransferOfSender(tobi));
	}
	
	@Test
	public void catIssuance0() throws Exception {
		sendTx("tobi", 0);
		sendTx("tobi", 1);
		sendTx("tobi", 2);
		sendTx("tobi", 3);
		sendTx("tobi", 4);
		sendTx("tobi", 5);
		sendTx("tobi", 6);
		sendTx("cat", 0);
		Assert.assertEquals("48578F03A46B358C10468E2312A41C6BCAB19417", merkleService.getRootTxKeys());
		Assert.assertEquals("E8B0EC53F010BBF1F7A17AA884B0843EC44EA5A8", merkleService.getRootTxAll());
		// Tobi
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSender(tobi));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 0));
		Assert.assertEquals("3D8E16792CBF16EAA964D4FE13B44102B9650F32", merkleService.getRootTxDividendOfSenderForAm(tobi, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(tobi, 3));
		Assert.assertEquals("455A287907BA562CBF510E016CB9F9FEB15E1D47", merkleService.getRootTxFusionOfSender(tobi));
		Assert.assertEquals("F51DDC52370707AB5734E6A76FECF6F73ED93A3D", merkleService.getRootTxIssuanceOfSender(tobi));
		Assert.assertEquals("7611585DB889D31A12BFC255518BF99914FE747E", merkleService.getRootTxOfRecipient(tobi));
		Assert.assertEquals("A3BCA251FC7CAF5F9652EDA153B71D6758EEB590", merkleService.getRootTxOfSender(tobi));
		Assert.assertEquals("922880E29C04C0462B17A0E8E82626500184B323", merkleService.getRootTxTransferOfSender(tobi));
		// Cat
		Assert.assertNull(merkleService.getRootTxDividendOfSender(cat));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(cat, 0));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(cat, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(cat, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(cat, 3));
		Assert.assertNull(merkleService.getRootTxFusionOfSender(cat));
		Assert.assertNull(merkleService.getRootTxIssuanceOfSender(cat));
		Assert.assertEquals("922880E29C04C0462B17A0E8E82626500184B323", merkleService.getRootTxOfRecipient(cat));
		Assert.assertEquals("DE8D6EE8985C6AB617A918CFCC5705550BA2A7B3", merkleService.getRootTxOfSender(cat));
		Assert.assertEquals("DE8D6EE8985C6AB617A918CFCC5705550BA2A7B3", merkleService.getRootTxTransferOfSender(cat));
		// Merkle leaves' tests
		Merkle<?> merkle = (Merkle<?>) merkleService.searchTxAll(GET_LEAVES, NO_PARTICULAR_LEAF);
		Assert.assertEquals(8, merkle.getLeavesHashes().size());       
		Assert.assertEquals("18D5DD88D1BB3783DFFF410E6F3D2639C961F234", merkle.getLeavesHashes().get(0));
		Assert.assertEquals("2E77D66FFAE997A63799B3A27A803B22A5B07692", merkle.getLeavesHashes().get(1));
		Assert.assertEquals("3BB31F9967AC940EE64593A1C06BAC627011DBA9", merkle.getLeavesHashes().get(2));
		Assert.assertEquals("598F7D431F1B30CE256D4D248A3334A94911C3A5", merkle.getLeavesHashes().get(3));
		Assert.assertEquals("922880E29C04C0462B17A0E8E82626500184B323", merkle.getLeavesHashes().get(4));
		Assert.assertEquals("CDE04A358F6A12058A394B9DF30E55660AA169F0", merkle.getLeavesHashes().get(5));
		Assert.assertEquals("DE8D6EE8985C6AB617A918CFCC5705550BA2A7B3", merkle.getLeavesHashes().get(6));
		Assert.assertEquals("F8FD08CDD489ABBC94F32A7138C8BED4B0CCFEAE", merkle.getLeavesHashes().get(7));
	}
	
	private void assertNullTxMerkles(KeyId k) {
		Assert.assertNull(merkleService.getRootTxDividendOfSender(k));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(k, 0));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(k, 1));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(k, 2));
		Assert.assertNull(merkleService.getRootTxDividendOfSenderForAm(k, 3));
		Assert.assertNull(merkleService.getRootTxFusionOfSender(k));
		Assert.assertNull(merkleService.getRootTxIssuanceOfSender(k));
		Assert.assertNull(merkleService.getRootTxOfRecipient(k));
		Assert.assertNull(merkleService.getRootTxOfSender(k));
		Assert.assertNull(merkleService.getRootTxTransferOfSender(k));
	}
	
	private void addPubkey(String name) throws Exception {
		URL pubURL = getClass().getResource(String.format("/data/%s/pk/%s.pub", env, name));
		URL sigURL = getClass().getResource(String.format("/data/%s/pk/%s.pub.asc", env, name));
		String armoredPub = readFile(pubURL);
		String armoredSig = readFile(sigURL);
		PublicKey pubkey = pgpService.extractPublicKey(armoredPub);
		Assert.assertNotNull(pksService.add(pubkey, new Signature(armoredSig)));
	}
	
	private void vote(String am, String voter) throws Exception {
		String rawAmendment = readFile(getClass().getResource(String.format("/data/%s/am/%s", env, am)));
		String signatureStream = readFile(getClass().getResource(String.format("/data/%s/am/%s.%s", env, am, voter)));
		hdcService.vote(new Amendment(rawAmendment), new Signature(signatureStream));
	}
	
	private void sendTx(String sender, int number) throws Exception {
		String rawTx = readFile(getClass().getResource(String.format("/data/%s/tx/%s.%d", env, sender, number)));
		String rawSig = readFile(getClass().getResource(String.format("/data/%s/tx/%s.%d.asc", env, sender, number)));
		Signature sig = new Signature(rawSig);
		Transaction tx = new Transaction(rawTx, sig);
		hdcService.transactionsProcess(tx, sig);
	}

	static String readFile(URL path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		return StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
