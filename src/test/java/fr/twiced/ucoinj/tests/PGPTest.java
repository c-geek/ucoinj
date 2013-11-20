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
public class PGPTest {

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

	@Autowired
	PGPService pgpService;

	@Test
	public void isTrue() {
		Assert.assertTrue(true);
	}

	@Test
	public void hasOnePublicKey() throws NoPublicKeyPacketException {
		PGPPublicKey pubkey = pgpService.extractPubkey(TOBI_PUBKEY);
		Assert.assertNotNull(pubkey);
		Assert.assertEquals(TOBI_PUBKEY_FINGERPRINT, pgpService.extractFingerprint(pubkey));
	}
}
