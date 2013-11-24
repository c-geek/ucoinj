package fr.twiced.ucoinj.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;
import fr.twiced.ucoinj.service.PGPService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
public class PrivateKeyTest {
	
	private static final String CAT_PUBKEY_FINGERPRINT = "C73882B64B7E72237A2F460CE9CAB76D19A8651E";
	private static final String CAT_PUBKEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\n"
			+ "\n"
			+ "mQENBFHHC/EBCADWTLSN7EGP+n30snndS3ZNcB02foL+0opcS6LK2coPDJLg2noo\n"
			+ "keJRHZxF3THmZQrKwZOjiuDBinOc5DWlzIS/gD/RaXwntgPFlGKBlBU+g255fr28\n"
			+ "ziSb5Y1lW4N//nUFdPZzoMmPgRj0b17T0UPCoMR8ZZ/Smk5LINbQwt+A+LEoxEdE\n"
			+ "Vcq+Tyc0OlEabqO6RFqiKDRiPhGPiCwVQA3yPjb6iCp5gTchObCxCnDbxA0Mfj9F\n"
			+ "mHrGbepNHGXxStO4xT0woCb7y02S1E8K08kOc5Bq9e1Yj5I/mdaw4Hn/Wp28lZl1\n"
			+ "mnO1u1z9ZU/rcglhEyaEOTwasheb44QcdGSfABEBAAG0TUxvTCBDYXQgKHVkaWQy\n"
			+ "O2M7Q0FUO0xPTDsyMDAwLTA0LTE5O2UrNDMuNzAtMDc5LjQyOzA7KSA8Y2VtLm1v\n"
			+ "cmVhdUBnbWFpbC5jb20+iQE9BBMBCAAnBQJRxwvxAhsDBQkLR5jvBQsJCAcDBRUK\n"
			+ "CQgLBRYCAwEAAh4BAheAAAoJEOnKt20ZqGUeZYcH/0ItH4b/O0y7V1Jzc1DZAdn4\n"
			+ "iDiI7/SF3fN4f6cJCu/SOVb+ERFIb6JK+HNHdVAcMHKaPW625R0FahHUkcXWkkGm\n"
			+ "Q6+sLIsVZwVN1oeZtlD12cq9A4UJyfJUXkinMKkI8xpdV8J7s5wFRavOS/qaF5be\n"
			+ "ah0Z+IGwQK0nuXxWpT6UZWbpUfXPQB2Mz2/rpjSWKwO3X4FwwOfDiuZExyH2JPDY\n"
			+ "shdPcj/x+gnzYW9XfWCJw3rOK42vtM+aLtUpJO0Jh6X/sj/iqyS4rPB4DVCmEgSX\n"
			+ "Px1P+kqnsz3aNTOIujXS8Faz+TC+eNhn+z3SoTl5gBlNNM171fWFr0BR3nIfIu65\n"
			+ "AQ0EUccL8QEIAPAQaxK6s4DjDHiOwrMotvb479QD5PsHU6S0VG0+naoPlNJb2d5w\n"
			+ "YhnFAn4aYLiXx4IIl38rHnV+yWATOUe2rdCe4enTXkxyWJVaxIcNJLFpUjHYGbrC\n"
			+ "nNwiXpuQfSDuRN/wcVNSBKXhWNUPY9IsbgERWhS5YTFnuQcBjMqDwF6JImQ8O4nZ\n"
			+ "wno811nqK1XaMuLVvXZAsO1Vi1k3NArM5+jdlq9e3BA0NcHJmGEcQdTw0Tk5Oq6r\n"
			+ "mE8ux7pS0bn6OUkkseR5DyRlFtzqi4wp30GeggeFExx7ZCVuctpJX9ZoC3cJoZT0\n"
			+ "s3LuUtV0EW50yCtP+3Vpkek2WtjfVbM6kDkAEQEAAYkBJQQYAQgADwUCUccL8QIb\n"
			+ "DAUJC0eY7wAKCRDpyrdtGahlHg7+B/95xEoSrFQ7/mc7g6sbisvx3s547gUXXYSu\n"
			+ "FHS03IMDWJrfGKqXtBf9ETBx4OLeBXY7z1lL4WCN6/xtrL+mSQ9dbDqdXv/1EhkS\n"
			+ "v0s+IvJ34KYGAkFXSCoTE7rnkPwQjoMYVSFkf5e8g9adyKvndq/QSPNuv+FPL6sH\n"
			+ "m1N9nmus5Ebr0zTVDmmfoqzokuDfHm5h6YrkFscMGjrCKWuXSiTaGj9Hm3MqeZ3T\n"
			+ "Kva5isa/h0h7Ai3wJ5XJpMrFNN6BU/wIt7fM2hsNAOwaG+WUfgjYEkOua8gPPtpL\n"
			+ "ZJJPb/89yrs9F7JkLi/oiAl5VpItm+hlFpLe1TE7oa6k53eZ2a+V\n"
			+ "=rOj9\n"
			+ "-----END PGP PUBLIC KEY BLOCK-----\n";
	
	private static final String CAT_PRIVKEY_PASSWORD = "lolcat";
	private static final String CAT_PRIVKEY = "-----BEGIN PGP PRIVATE KEY BLOCK-----\r\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "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\r\n"
			+ "6IgJeVaSLZvoZRaS3tUxO6GupOd3mdmvlQ==\r\n"
			+ "=26j0\r\n"
			+ "-----END PGP PRIVATE KEY BLOCK-----\r\n";
	
	private static final String CAT_PRIVKEY_NO_PASSWD = "-----BEGIN PGP PRIVATE KEY BLOCK-----\r\n"
			+ "Version: GnuPG v1.4.11 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "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\r\n"
			+ "=IaQr\r\n"
			+ "-----END PGP PRIVATE KEY BLOCK-----\r\n";
	
	private static final String CAT_SIGNATURE_DATA = "Version: 1\r\n"
			+ "Currency: beta_brousouf\r\n"
			+ "Number: 0\r\n"
			+ "GeneratedOn: 1380397288\r\n"
			+ "NextRequiredVotes: 2\r\n"
			+ "MembersRoot: F5ACFD67FC908D28C0CFDAD886249AC260515C90\r\n"
			+ "MembersCount: 3\r\n"
			+ "MembersChanges:\r\n"
			+ "+2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n"
			+ "+33BBFC0C67078D72AF128B5BA296CC530126F372\r\n"
			+ "+C73882B64B7E72237A2F460CE9CAB76D19A8651E\r\n"
			+ "VotersRoot: F5ACFD67FC908D28C0CFDAD886249AC260515C90\r\n"
			+ "VotersCount: 3\r\n"
			+ "VotersChanges:\r\n"
			+ "+2E69197FAB029D8669EF85E82457A1587CA0ED9C\r\n"
			+ "+33BBFC0C67078D72AF128B5BA296CC530126F372\r\n"
			+ "+C73882B64B7E72237A2F460CE9CAB76D19A8651E\r\n";
	
	private static final String TOBI_PUBKEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\n"
			+ "\n"
			+ "mQENBFHG4x4BCADi8J4sSpIv5q2gREBwMj1TCUFDvQDx8/WivLKJ+LgmC2zwuuMD\n"
			+ "Yo9YwZBmMe/orZJRSDwslKUARtFzmSui2CR3b4EEr1Hhz9coyqHsF9lYdht2YU7i\n"
			+ "08FOdppRJdk7XuHCABZ+mXeG8WnNMP+9QjRAh3sFDkWpji9iL5ZmzlDx6UMXq3lM\n"
			+ "SvN3VC32X+K2HoQgesm3fTjCBmQik2Ayvp89Ikc2wAWM5/B7RCwdHTTysVOE0Kkx\n"
			+ "IIkeus76p+5pvLzrZOvM18ToLxV7KThxVvHn+dj2iOMuteY3BylN+XL1J/nBYkOC\n"
			+ "fsCigMClbWj7IptqDZWmqRbzYgcrOYa8SezxABEBAAG0VVRvYmkgVWNoaXdhICh1\n"
			+ "ZGlkMjtjO1VDSElXQTtPQklUTzsyMDAwLTA5LTMwO2UrMzUuNjkrMTM5LjY5OzAp\n"
			+ "IDxjZW0ubW9yZWF1QGdtYWlsLmNvbT6JAT0EEwEIACcFAlHG4x4CGwMFCQs75EIF\n"
			+ "CwkIBwMFFQoJCAsFFgIDAQACHgECF4AACgkQJFehWHyg7ZxljwgAkkV+cXO8kXXC\n"
			+ "qXdo9hwSprQwbHHfwsovhIvZI9E4Tpce0TXaebflCecDwsPgGyeP8xPUX8pZAvYY\n"
			+ "w5klREvKn7Vu6NiGrE07k7EUCIs+kkPTXeRAv/NpMfJoUOdh+da0ybs+nTO1lJV+\n"
			+ "cEGqz0OX8yIwrlwhrkvnlFWRE/oe3NoJ9f0N7sMXejhs8aTGClXU87GiUAoQ0XC1\n"
			+ "U8BPLjprkne5RMsSVHj3HbVzVQXmreNVDNsvtJVTskGxUE7IzIK80fBZP7OmC+bi\n"
			+ "ZuwTqXekF6SCqOJjlIsDY4uIL/cCQRkhM1kmeeHcGOQT+6auPtpHXniRxUNOoi7V\n"
			+ "CWg/HS2r9rkBDQRRxuMeAQgA+jCWHHx/kPQxghMBuio36RoR63qQSFxpp2Lx5HIk\n"
			+ "UYhpHE0oGOEEiBG0HrKIv7Qz+4Cs1VHSDCplD+vtRS9PkjOopSxE+ROHjIfT0fcj\n"
			+ "kYT14m2Ftmcqs5/Vw9qBHLhJQPyqIR4TMVuDP5/1LIfnv/EUoisAFcpT86CFK1jO\n"
			+ "GtdTALxDKIk6mlsiPCXD7jlSWw3btJlwydeudTVYBq5OhC/DLREn2vgtvqcWjvi4\n"
			+ "X/ttEWB35EzP11s6gRTv3ldVlXQ13Db+gHrjExRn848bRu0R1RzgcpsXOVeyzlzM\n"
			+ "KFJDXueyCOdiVAyEVPmLHKojwaP+UHlEnwI/v5OMva1VsQARAQABiQElBBgBCAAP\n"
			+ "BQJRxuMeAhsMBQkLO+RCAAoJECRXoVh8oO2cgiQIAKCqxY4LVbHYDywkwI4dAFp4\n"
			+ "bFlqKDURKzEq8nfDliBLmFrDAv9lFEBbNii7Y6b3FxaijUTPlJbU9RX8xtPO6bbA\n"
			+ "ujJPyHsi/hBZjqWCqbajbwoNMYzu9nbtB2DfxZKYnVijjmb15WuXVC+GN4M+ZCtw\n"
			+ "+SNrpFTBPUUl4LjBRvUJ9DhjbD2+FlqXfDiRLKma0658s2PQZhqajiEswDyo1fAh\n"
			+ "OykaWCE5pW0DHl2Fizc77/QDe7iQa8ZRWp8Q/w0FJE2bXb3Paxtd40XURVOGRmtN\n"
			+ "vLPTKXIgxFi2dTCBQz6oTeajOjun6x6BUJVUbnJ40YmlqYbXqHyI9AcVMMEsLdw=\n"
			+ "=ZdXC\n" + "-----END PGP PUBLIC KEY BLOCK-----\n";

	@Autowired
	private PGPService pgpService;
	
	private PGPPublicKey catPubkey;
	private PGPPublicKey tobiPubkey;
	private PGPPrivateKey catPrivKey;
	
	@Before
	public void setUp() throws PGPException, IOException, NoPublicKeyPacketException{
		catPubkey = pgpService.extractPublicKey(CAT_PUBKEY).getPGPPublicKey();
		tobiPubkey = pgpService.extractPublicKey(TOBI_PUBKEY).getPGPPublicKey();
		catPrivKey = pgpService.extractPrivateKey(CAT_PRIVKEY, CAT_PRIVKEY_PASSWORD);
	}

	@Test
	public void signAndVerify() throws BadSignatureException, NoSignaturePacketException, Exception {
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, catPrivKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertTrue(pgpService.verify(CAT_SIGNATURE_DATA, signatureString, catPubkey));
	}

	@Test
	public void signAndVerifyNoPassword() throws BadSignatureException, NoSignaturePacketException, Exception {
		PGPPrivateKey privateKey = pgpService.extractPrivateKey(CAT_PRIVKEY_NO_PASSWD, "");
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, privateKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertTrue(pgpService.verify(CAT_SIGNATURE_DATA, signatureString, catPubkey));
	}

	@Test
	public void signAndVerifyNoPasswordWithPassword() throws BadSignatureException, NoSignaturePacketException, Exception {
		PGPPrivateKey privateKey = pgpService.extractPrivateKey(CAT_PRIVKEY_NO_PASSWD, "ABC");
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, privateKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertTrue(pgpService.verify(CAT_SIGNATURE_DATA, signatureString, catPubkey));
	}

	@Test
	public void signAndVerifyBadData() throws BadSignatureException, NoSignaturePacketException, Exception {
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, catPrivKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertFalse(pgpService.verify(CAT_SIGNATURE_DATA + "AAA", signatureString, catPubkey));
	}

	@Test
	public void signBadDataAndVerify() throws BadSignatureException, NoSignaturePacketException, Exception {
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA + "AAA", catPrivKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertFalse(pgpService.verify(CAT_SIGNATURE_DATA, signatureString, catPubkey));
	}

	@Test
	public void signWithPrivateKeyAndVerifyWithBadPublicKey() throws BadSignatureException, NoSignaturePacketException, Exception {
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, catPrivKey);
		assertEquals(CAT_PUBKEY_FINGERPRINT.substring(24), pgpService.extractIssuer(signatureString));
		assertFalse(pgpService.verify(CAT_SIGNATURE_DATA, signatureString, tobiPubkey));
	}

	@Test(expected = PGPException.class)
	public void tryToSignWithBadPassword() throws BadSignatureException, NoSignaturePacketException, Exception {
		// Expect to have an exception with bad private key password
		pgpService.extractPrivateKey(CAT_PRIVKEY, CAT_PRIVKEY_PASSWORD + "SOME_BAD_PASSWORD");
	}
}
