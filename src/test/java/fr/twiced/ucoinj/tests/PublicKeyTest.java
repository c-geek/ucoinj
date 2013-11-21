package fr.twiced.ucoinj.tests;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.service.PGPService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
public class PublicKeyTest {

	private static final String TOBI_PUBKEY_FINGERPRINT = "2E69197FAB029D8669EF85E82457A1587CA0ED9C";
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

	@Autowired
	PGPService pgpService;

	@Test
	public void isTrue() {
		Assert.assertTrue(true);
	}

	@Test
	public void extractPublicKeys() throws NoPublicKeyPacketException {
		// Testing Tobi's pubkey
		PGPPublicKey tobiPubkey = pgpService.extractPubkey(TOBI_PUBKEY);
		Assert.assertNotNull(tobiPubkey);
		Assert.assertNotNull(tobiPubkey.getAlgorithm());
		Assert.assertNotNull(tobiPubkey.getCreationTime());
		Assert.assertNotNull(tobiPubkey.getFingerprint());
		Assert.assertNotNull(tobiPubkey.getKeyID());
		Assert.assertNotNull(tobiPubkey.getVersion());
		Assert.assertEquals(TOBI_PUBKEY_FINGERPRINT, pgpService.extractFingerprint(tobiPubkey));
		// Testing Cat's pubkey
		PGPPublicKey catPubkey = pgpService.extractPubkey(CAT_PUBKEY);
		Assert.assertNotNull(catPubkey);
		Assert.assertNotNull(catPubkey.getAlgorithm());
		Assert.assertNotNull(catPubkey.getCreationTime());
		Assert.assertNotNull(catPubkey.getFingerprint());
		Assert.assertNotNull(catPubkey.getKeyID());
		Assert.assertNotNull(catPubkey.getVersion());
		Assert.assertEquals(CAT_PUBKEY_FINGERPRINT, pgpService.extractFingerprint(catPubkey));
		// The two should be different
		Assert.assertNotEquals(TOBI_PUBKEY_FINGERPRINT, CAT_PUBKEY_FINGERPRINT);
		Assert.assertNotEquals(pgpService.extractFingerprint(tobiPubkey), pgpService.extractFingerprint(catPubkey));
	}
}
