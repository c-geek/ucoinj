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
		catPubkey = pgpService.extractPubkey(CAT_PUBKEY);
		tobiPubkey = pgpService.extractPubkey(TOBI_PUBKEY);
		catPrivKey = pgpService.extractPrivateKey(CAT_PRIVKEY, CAT_PRIVKEY_PASSWORD);
	}

	@Test
	public void signAndVerify() throws BadSignatureException, NoSignaturePacketException, Exception {
		String signatureString = pgpService.sign(CAT_SIGNATURE_DATA, catPrivKey);
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
