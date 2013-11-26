package fr.twiced.ucoinj.tests;

import org.bouncycastle.openpgp.PGPException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.NoSignaturePacketException;
import fr.twiced.ucoinj.pgp.Sha1;
import fr.twiced.ucoinj.service.PGPService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
public class SignatureTest {

	private static final String CAT_DETACHED_SIGNATURE_HASH = "C375AD6F94E0D2E57301E5AE522E64E5EA696139";
	private static final String CAT_DETACHED_SIGNATURE = "-----BEGIN PGP SIGNATURE-----\r\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "iQEcBAABCAAGBQJSRzV7AAoJEOnKt20ZqGUe+zMIAIyWCvpd1BQ4mnJR/BfgQxLL\r\n"
			+ "2lusxOFViZRo4OcPnXJ67UahGWBeChXswmE8zFNxindlJZqzKbQAHSMcPgDm+4Ls\r\n"
			+ "cYMRtKnxp/SBggNFa90uUIoCW5V5ZxCLiCNUHJC+8VJu8MhEAnp4XhYnDkEjiHXL\r\n"
			+ "xwIQlo1S4GvtBcSeLVzU2Qxad59ukbnWwTvnKdGo/cmQudiD9TXrJtfKIKN6bBD9\r\n"
			+ "dWL+JjJ9p4/1tNE6egUvDFMeKyUxP6wTHnc/XfpUty4dTAZh4SghY7IYwKftMl9L\r\n"
			+ "kNlP+H1aLntNkCjv6Liy9RDjubxBys6Y0mK6julZqbeN5BtLjXDE6sAPjr1ybfQ=\r\n"
			+ "=C6z8\r\n"
			+ "-----END PGP SIGNATURE-----\r\n";
	
	private static final String CAT_SIGNATURE = "-----BEGIN PGP MESSAGE-----\r\n"
			+ "Version: GnuPG v1.4.11 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "owGbwMvMwMH48tT2XMkVqXKMa5kkkpgSc4P6+wvDUouKM/PzrBQMebmcS4uKUvOS\r\n"
			+ "K60UklJLEuOTivJLi/NL03i5/Epzk1KLrBQMeLncU/NSixJLUlP8QXqMLQyMLc2N\r\n"
			+ "LCyAalIrSoJSC0szi1JTwvJLUoutFIx4uXxTQTqLg/LzS6wU3Ewdnd1czMzdnC0N\r\n"
			+ "LFyMLJwNgFxHFwsLMyMTS0dnIzMDU0NToBxcm3N+aR5QnzFCICMxLx1oNC+XtpGr\r\n"
			+ "maWhpbmbo5OBkaWLhZmZpaubhamrhZGJqbmjoamFubOjgauLpTNQqbGxk5Mb0DIz\r\n"
			+ "cwNzCxdzI0c3QyMLJ1MnRyNLM2dnU2MDQyMzN2NzoHO1nc2NLSyMnMxMnMxdzY2M\r\n"
			+ "jM0djdxMzAycXS2dHZ3MzVwMLR0tzEwNXXm5QH4k3WMQXQh/QfmDx1udjDIsDIwc\r\n"
			+ "DGysTKDkwcDFKQBLNmxP2P/wn7FK2nPoSNnfkPNcuvulzLk33rjX6Zz5f07WlJSr\r\n"
			+ "dzwnMhyd/WKabv+7c+mrD2WeFflaf8Yk51xpnquuRETh9qVTZh32tUhh27LtwrqP\r\n"
			+ "ZlcjN8Qcrby7aKfRy/yuZ14nnvbvl2dgUlsV0uC1dcKrcy9ytzE5JG5Tic7rOvje\r\n"
			+ "t+/f03zGR5PdDzYcr5y6891fmcc+vTXzlJaXnr72eOfHBqPlLn7ai5T5cstWf18q\r\n"
			+ "m72o3yNl+4fVmq/OhvSJScw07A6JuqEkfVlxAQt7SJit+tETzjtcJdleHanLn5Lr\r\n"
			+ "PzvI7c6ys6djxBrYyn2DHbT3dModjjWcnPt33019v6anF28XbOg6aaW7faqp4Vkj\r\n"
			+ "AA==\r\n"
			+ "=lfTO\r\n"
			+ "-----END PGP MESSAGE-----\r\n".replace("\r\n", "\n");
	
	private static final String CAT_SIGNATURE_DATA_HASH = "58A2700B6CE56E112238FDCD81C8DACE2F2D06DC";
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

	private static final String CAT_PUBKEY_FINGERPRINT = "C73882B64B7E72237A2F460CE9CAB76D19A8651E";
	private static final String CAT_PUBKEY = "-----BEGIN PGP PUBLIC KEY BLOCK-----\r\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "mQENBFHHC/EBCADWTLSN7EGP+n30snndS3ZNcB02foL+0opcS6LK2coPDJLg2noo\r\n"
			+ "keJRHZxF3THmZQrKwZOjiuDBinOc5DWlzIS/gD/RaXwntgPFlGKBlBU+g255fr28\r\n"
			+ "ziSb5Y1lW4N//nUFdPZzoMmPgRj0b17T0UPCoMR8ZZ/Smk5LINbQwt+A+LEoxEdE\r\n"
			+ "Vcq+Tyc0OlEabqO6RFqiKDRiPhGPiCwVQA3yPjb6iCp5gTchObCxCnDbxA0Mfj9F\r\n"
			+ "mHrGbepNHGXxStO4xT0woCb7y02S1E8K08kOc5Bq9e1Yj5I/mdaw4Hn/Wp28lZl1\r\n"
			+ "mnO1u1z9ZU/rcglhEyaEOTwasheb44QcdGSfABEBAAG0TUxvTCBDYXQgKHVkaWQy\r\n"
			+ "O2M7Q0FUO0xPTDsyMDAwLTA0LTE5O2UrNDMuNzAtMDc5LjQyOzA7KSA8Y2VtLm1v\r\n"
			+ "cmVhdUBnbWFpbC5jb20+iQE9BBMBCAAnBQJRxwvxAhsDBQkLR5jvBQsJCAcDBRUK\r\n"
			+ "CQgLBRYCAwEAAh4BAheAAAoJEOnKt20ZqGUeZYcH/0ItH4b/O0y7V1Jzc1DZAdn4\r\n"
			+ "iDiI7/SF3fN4f6cJCu/SOVb+ERFIb6JK+HNHdVAcMHKaPW625R0FahHUkcXWkkGm\r\n"
			+ "Q6+sLIsVZwVN1oeZtlD12cq9A4UJyfJUXkinMKkI8xpdV8J7s5wFRavOS/qaF5be\r\n"
			+ "ah0Z+IGwQK0nuXxWpT6UZWbpUfXPQB2Mz2/rpjSWKwO3X4FwwOfDiuZExyH2JPDY\r\n"
			+ "shdPcj/x+gnzYW9XfWCJw3rOK42vtM+aLtUpJO0Jh6X/sj/iqyS4rPB4DVCmEgSX\r\n"
			+ "Px1P+kqnsz3aNTOIujXS8Faz+TC+eNhn+z3SoTl5gBlNNM171fWFr0BR3nIfIu65\r\n"
			+ "AQ0EUccL8QEIAPAQaxK6s4DjDHiOwrMotvb479QD5PsHU6S0VG0+naoPlNJb2d5w\r\n"
			+ "YhnFAn4aYLiXx4IIl38rHnV+yWATOUe2rdCe4enTXkxyWJVaxIcNJLFpUjHYGbrC\r\n"
			+ "nNwiXpuQfSDuRN/wcVNSBKXhWNUPY9IsbgERWhS5YTFnuQcBjMqDwF6JImQ8O4nZ\r\n"
			+ "wno811nqK1XaMuLVvXZAsO1Vi1k3NArM5+jdlq9e3BA0NcHJmGEcQdTw0Tk5Oq6r\r\n"
			+ "mE8ux7pS0bn6OUkkseR5DyRlFtzqi4wp30GeggeFExx7ZCVuctpJX9ZoC3cJoZT0\r\n"
			+ "s3LuUtV0EW50yCtP+3Vpkek2WtjfVbM6kDkAEQEAAYkBJQQYAQgADwUCUccL8QIb\r\n"
			+ "DAUJC0eY7wAKCRDpyrdtGahlHg7+B/95xEoSrFQ7/mc7g6sbisvx3s547gUXXYSu\r\n"
			+ "FHS03IMDWJrfGKqXtBf9ETBx4OLeBXY7z1lL4WCN6/xtrL+mSQ9dbDqdXv/1EhkS\r\n"
			+ "v0s+IvJ34KYGAkFXSCoTE7rnkPwQjoMYVSFkf5e8g9adyKvndq/QSPNuv+FPL6sH\r\n"
			+ "m1N9nmus5Ebr0zTVDmmfoqzokuDfHm5h6YrkFscMGjrCKWuXSiTaGj9Hm3MqeZ3T\r\n"
			+ "Kva5isa/h0h7Ai3wJ5XJpMrFNN6BU/wIt7fM2hsNAOwaG+WUfgjYEkOua8gPPtpL\r\n"
			+ "ZJJPb/89yrs9F7JkLi/oiAl5VpItm+hlFpLe1TE7oa6k53eZ2a+V\r\n"
			+ "=rOj9\r\n"
			+ "-----END PGP PUBLIC KEY BLOCK-----\r\n";
	
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

	@Test
	public void verifyDetached() throws BadSignatureException, NoSignaturePacketException, Exception {
		// Working detached signature
		PublicKey catPubkey = pgpService.extractPublicKey(CAT_PUBKEY);
		PublicKey tobiPubkey = pgpService.extractPublicKey(TOBI_PUBKEY);
		String data = CAT_SIGNATURE_DATA;
		String sign = CAT_DETACHED_SIGNATURE;
		Assert.assertEquals(CAT_SIGNATURE_DATA_HASH, new Sha1(data).toString().toUpperCase());
		Assert.assertEquals(CAT_DETACHED_SIGNATURE_HASH, new Sha1(sign).toString().toUpperCase());
		checkSignature(data, sign, CAT_PUBKEY_FINGERPRINT, catPubkey, tobiPubkey);
	}
	
	@Test(expected = BadSignatureException.class)
	public void verifyCompressed() throws BadSignatureException, NoPublicKeyPacketException, NoSignaturePacketException, Exception{
		PublicKey catPubkey = pgpService.extractPublicKey(CAT_PUBKEY);
		PublicKey tobiPubkey = pgpService.extractPublicKey(TOBI_PUBKEY);
		// Working with full signature (compressed literal data + signature)
		String data = CAT_SIGNATURE_DATA;
		String sign = CAT_SIGNATURE;
		checkSignature(data, sign, CAT_PUBKEY_FINGERPRINT, catPubkey, tobiPubkey);
	}
	
	public void checkSignature(String data, String sign, String issuerFingerprint, PublicKey issuerPubkey, PublicKey notIssuerPubkey) throws BadSignatureException, NoPublicKeyPacketException, NoSignaturePacketException, Exception{
		String issuer = pgpService.extractIssuer(sign).toUpperCase();
		Assert.assertTrue(issuerFingerprint.endsWith(issuer));
		Assert.assertTrue(pgpService.verify(data, sign, issuerPubkey));
		Assert.assertFalse(pgpService.verify(data, sign, notIssuerPubkey));
		Assert.assertFalse(pgpService.verify(data + "a", sign, issuerPubkey));
		Assert.assertFalse(pgpService.verify(data + "b", sign, issuerPubkey));
		Assert.assertFalse(pgpService.verify(data.replace("\r\n", "\n"), sign, issuerPubkey));
	}
}
