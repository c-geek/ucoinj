package fr.twiced.ucoinj.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.TransactionOrigin;
import fr.twiced.ucoinj.TransactionType;
import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.CoinEntry;
import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.bean.id.CoinId;
import fr.twiced.ucoinj.bean.id.TransactionId;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.CoinDao;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.dao.TransactionDao;
import fr.twiced.ucoinj.exceptions.RefusedDataException;
import fr.twiced.ucoinj.pgp.Sha1;
import fr.twiced.ucoinj.service.HDCService;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;
import fr.twiced.ucoinj.service.tx.FusionTransactionProcessor;
import fr.twiced.ucoinj.service.tx.IssuanceTransactionProcessor;
import fr.twiced.ucoinj.service.tx.TransfertTransactionProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
@Transactional
public class TransactionTest {

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;
	
	@Autowired
	private HDCService hdcService;
	
	@Autowired
	private IssuanceTransactionProcessor issuanceTxProcessor;
	
	@Autowired
	private TransfertTransactionProcessor transfertTxProcessor;
	
	@Autowired
	private FusionTransactionProcessor fusionTxProcessor;
	
	@Autowired
	private TransactionDao txDao;
	
	@Autowired
	private KeyDao keyDao;
	
	@Autowired
	private CoinDao coinDao;
	
	@Autowired
	private AmendmentDao amDao;

	@Before
	public void setupPubkeysAndAmendments() throws Exception {
		addPubkey("tobi");
		addPubkey("cat");
		vote("am0", "tobi");
		vote("am1", "tobi");
		vote("am2", "tobi");
		vote("am2", "cat");
	}
	
	private void addPubkey(String name) throws Exception {
		URL pubURL = getClass().getResource("/data/pubkeys/" + name + ".pub");
		URL sigURL = getClass().getResource("/data/signatures/pubkeys/" + name + ".asc");
		String armoredPub = readFile(pubURL);
		String armoredSig = readFile(sigURL);
		PublicKey pubkey = pgpService.extractPublicKey(armoredPub);
		Assert.assertNotNull(pksService.add(pubkey, new Signature(armoredSig)));
		Key key = new Key();
		key.setFingerprint(pubkey.getFingerprint());
		key.setManaged(true);
		keyDao.save(key);
	}
	
	private void vote(String am, String voter) throws Exception {
		String rawAmendment = readFile(getClass().getResource("/data/amendments/" + am));
		String signatureStream = readFile(getClass().getResource("/data/signatures/amendments/" + am + "-" + voter + ".asc"));
		hdcService.vote(new Amendment(rawAmendment), new Signature(signatureStream));
	}
	
	@Test
	public void parseIssuance0() throws Exception {
		String issuanceTx = "Version: 1\r\n" + 
			"Currency: beta_brousouf\r\n" +
			"Sender: 2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n"+
			"Number: 0\r\n"+
			"Recipient: 2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n" +
			"Type: ISSUANCE\r\n" +
			"Coins:\r\n" +
			"2E69197FAB029D8669EF85E82457A1587CA0ED9C-0-1-1-A-1\r\n" +
			"Comment:\r\n";

		Transaction tx = new Transaction(issuanceTx, new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		Assert.assertNotNull(tx);
		Assert.assertEquals(new Integer(1), tx.getVersion());
		Assert.assertEquals(new Integer(0), tx.getNumber());
		Assert.assertEquals("beta_brousouf", tx.getCurrency());
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", tx.getSender());
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", tx.getRecipient());
		Assert.assertEquals("", tx.getComment());
		Assert.assertEquals(TransactionType.ISSUANCE, tx.getType());
		Assert.assertEquals("18F353A44BD8C223F5D943AE6FF9ABB7C9F16DFB", tx.getHash());
		
		String issuanceTx2 = "Version: 1\r\n"
				+ "Currency: beta_brousouf\r\n"
				+ "Sender: 2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n"
				+ "Number: 1\r\n"
				+ "PreviousHash: 18F353A44BD8C223F5D943AE6FF9ABB7C9F16DFB\r\n"
				+ "Recipient: 2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n"
				+ "Type: ISSUANCE\r\n"
				+ "Coins:\r\n"
				+ "2E69197FAB029D8669EF85E82457A1587CA0ED9C-1-1-2-A-1\r\n"
				+ "Comment:\r\nSome second transaction !";

		Transaction tx2 = new Transaction(issuanceTx2, new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		Assert.assertNotNull(tx2);
		Assert.assertEquals(new Integer(1), tx2.getVersion());
		Assert.assertEquals(new Integer(1), tx2.getNumber());
		Assert.assertEquals("beta_brousouf", tx2.getCurrency());
		Assert.assertEquals("18F353A44BD8C223F5D943AE6FF9ABB7C9F16DFB", tx2.getPreviousHash());
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", tx2.getSender());
		Assert.assertEquals("2E69197FAB029D8669EF85E82457A1587CA0ED9C", tx2.getRecipient());
		Assert.assertEquals("Some second transaction !", tx2.getComment());
		Assert.assertEquals(TransactionType.ISSUANCE, tx2.getType());
	}
	
	@Test
	public void goodTransaction1() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3, 1, 0, TransactionOrigin.AMENDMENT, 2))
		}));
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		t1.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		issuanceTxProcessor.store(t1);
		Assert.assertNotNull(txDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0));
		Assert.assertNull(txDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 4));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 5));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 20));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 154998));
	}
	
	@Test
	public void badCurrency() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf_bad");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3, 1, 0, TransactionOrigin.AMENDMENT, 2))
		}));
		t1.setComment("");
		// Random signature will be OK
		t1.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		issuanceTxProcessor.store(t1);
	}
	
	@Test(expected = RefusedDataException.class)
	public void amendmentWithoutDividend() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 1))
		}));
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		issuanceTxProcessor.store(t1);
	}
	
	@Test(expected = RefusedDataException.class)
	public void notCompleteTransaction() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf");
		issuanceTxProcessor.store(t1);
	}
	
	@Test(expected = RefusedDataException.class)
	public void createWayTooMuch() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf_bad");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3, 1, 3, TransactionOrigin.AMENDMENT, 2))
		}));
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		issuanceTxProcessor.store(t1);
	}
	
	@Test
	public void createExactDU() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf_bad");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 1, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 2, TransactionOrigin.AMENDMENT, 2))
		}));
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		t1.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		issuanceTxProcessor.store(t1);
		Assert.assertNotNull(txDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0));
		Assert.assertNull(txDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 20));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 154998));
	}
	
	@Test(expected = RefusedDataException.class)
	public void createExactDUPlusOne() throws Exception {
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf_bad");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 1, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 2, TransactionOrigin.AMENDMENT, 2))
		}));
		Amendment am = amDao.getPromoted(2);
		Assert.assertEquals(t1.getValue().intValue(), am.getDividend() + 1);
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		issuanceTxProcessor.store(t1);
	}
	
	@Test
	public void issueTransfertAndFusion() throws Exception {
		// Issue 4 coins
		Transaction t1 = new Transaction();
		t1.setVersion(1);
		t1.setCurrency("beta_brousouf");
		t1.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setNumber(0);
		t1.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t1.setType(TransactionType.ISSUANCE);
		t1.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3, 1, 0, TransactionOrigin.AMENDMENT, 2))
		}));
		t1.setComment("");
		t1.setHash(new Sha1(t1.getRaw()).getHash());
		t1.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		issuanceTxProcessor.store(t1);
		// Transfers 2 to Cat
		Transaction t2 = new Transaction();
		t2.setVersion(1);
		t2.setCurrency("beta_brousouf");
		t2.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t2.setNumber(1);
		t2.setPreviousHash(t1.getHash());
		t2.setRecipient("C73882B64B7E72237A2F460CE9CAB76D19A8651E");
		t2.setType(TransactionType.TRANSFER);
		t2.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0, 1, 0, TransactionOrigin.AMENDMENT, 2),
						new TransactionId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1, 1, 0, TransactionOrigin.AMENDMENT, 2),
						new TransactionId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1))
		}));
		t2.setComment("Paying Cat");
		t2.setHash(new Sha1(t2.getRaw()).getHash());
		t2.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		transfertTxProcessor.store(t2);
		// Fusion of the 2 lasts
		Transaction t3 = new Transaction();
		t3.setVersion(1);
		t3.setCurrency("beta_brousouf");
		t3.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t3.setNumber(2);
		t3.setPreviousHash(t2.getHash());
		t3.setRecipient("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t3.setType(TransactionType.FUSION);
		t3.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 4, 2, 0, TransactionOrigin.FUSION, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2, 1, 0, TransactionOrigin.AMENDMENT, 2)),
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3, 1, 0, TransactionOrigin.AMENDMENT, 2))
		}));
		t3.setComment("Paying Cat");
		t3.setHash(new Sha1(t3.getRaw()).getHash());
		t3.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		fusionTxProcessor.store(t3);
		// Transfer of the fusioned coin
		Transaction t4 = new Transaction();
		t4.setVersion(1);
		t4.setCurrency("beta_brousouf");
		t4.setSender("2E69197FAB029D8669EF85E82457A1587CA0ED9C");
		t4.setNumber(3);
		t4.setPreviousHash(t3.getHash());
		t4.setRecipient("C73882B64B7E72237A2F460CE9CAB76D19A8651E");
		t4.setType(TransactionType.FUSION);
		t4.setCoins(Arrays.asList(new CoinEntry[]{
				new CoinEntry(new CoinId("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 4, 2, 0, TransactionOrigin.FUSION, 2))
		}));
		t4.setComment("Paying Cat again");
		t4.setHash(new Sha1(t4.getRaw()).getHash());
		t4.setSignature(new Signature(readFile(getClass().getResource("/data/signatures/pubkeys/tobi.asc"))));
		transfertTxProcessor.store(t4);
		// Tests existing coins
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3));
		Assert.assertNotNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 4));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 5));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 20));
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 154998));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 0));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 1));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 2));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 3));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 4));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 20));
		Assert.assertNull(coinDao.getByIssuerAndNumber("C73882B64B7E72237A2F460CE9CAB76D19A8651E", 154998));
		// Ownership
		Assert.assertEquals(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 0).getOwner(), "C73882B64B7E72237A2F460CE9CAB76D19A8651E");
		Assert.assertEquals(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 1).getOwner(), "C73882B64B7E72237A2F460CE9CAB76D19A8651E");
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 2).getOwner());
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 3).getOwner());
		Assert.assertEquals(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 4).getOwner(), "C73882B64B7E72237A2F460CE9CAB76D19A8651E");
		Assert.assertNull(coinDao.getByIssuerAndNumber("2E69197FAB029D8669EF85E82457A1587CA0ED9C", 5));
	}

	static String readFile(URL path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(path.toURI()));
		return StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(encoded)).toString();
	}
}
