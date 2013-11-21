package fr.twiced.ucoinj.tests;

import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

	private static final String CAT_SIGNATURE_HASH = "5ED0478377E717B8F35F6A945505AA9435DC2B38";
	private static final String CAT_SIGNATURE = "-----BEGIN PGP MESSAGE-----\r\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "owGbwMvMwMH48tT2XMkVqXKMaxnlkjiTE0v0UvNKiiqDrD7Fh6UWFWfm51kpGPJy\r\n"
			+ "OZcWFaXmJVdaKSSlliTGJxXllxbnl6bxcnmnAsWczY0tLIyczEyczF3NjYyMzR2N\r\n"
			+ "3EzMDJxdLZ0dnczNXAwtHS3MTA1debk88otLgMZaAU0kWo+LgYmlgYGRo5m5kYmL\r\n"
			+ "sambhZm5m5mJs7OBhbmTo7GpobOBo6uTmYsbL1dIUWlxCa0M72SUYWFg5GBgY2UC\r\n"
			+ "hQ4DF6cALPCyuDgYzrobG1XMi1+maDOj0Jjvd0KJovun7O6aueyW8VlC2m/entz3\r\n"
			+ "wFprxwQJQ8NTSkHHnlf0Z3dbyV31m/l9/pdbmYczC2zUF5980/71Pctr4cP1OZGq\r\n"
			+ "xu8CvqgmbzTo+aSzvEbEptJSVCBc83jFh5BfU41UzwsraYrKr5jkdWODcqSLhPqT\r\n"
			+ "i2dTWi5+CuMVW3AgbLfhL42Fx61P254MPi+hr7O87Ufcz9sf5WU3nd6gdm9Z8I4v\r\n"
			+ "8h/jdmx2nl1gp6n1+ADHP63JF0WCg566HrLbIbFaM2Fd8fxT9S7Vm3uU1eyYG/dn\r\n"
			+ "CJ22+H7n2Eu7p0pL9EXn/M5f8OasEPOfyVtitHR9c1co/jT/3bP7xo5aAA==\r\n"
			+ "=lI/K\r\n"
			+ "-----END PGP MESSAGE-----\r\n";
	private static final String CAT_SIGNATURE_DATA = "Version: 1\r\n"
			+ "Currency: beta_brousouf\r\n"
			+ "Key: C73882B64B7E72237A2F460CE9CAB76D19A8651E\r\n"
			+ "Hosters:\r\n"
			+ "C73882B64B7E72237A2F460CE9CAB76D19A8651E\r\n"
			+ "D049002A6724D35F867F64CC087BA351C0AEB6DF\r\n"
			+ "Trusts:\r\n"
			+ "C73882B64B7E72237A2F460CE9CAB76D19A8651E\r\n"
			+ "D049002A6724D35F867F64CC087BA351C0AEB6DF\r\n";
	
	private static final String CAT_SIGNATURE2_HASH = "5ED0478377E717B8F35F6A945505AA9435DC2B38";
	private static final String CAT_SIGNATURE2 = "-----BEGIN PGP MESSAGE-----\r\n"
			+ "Version: GnuPG v1.4.12 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "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\r\n"
			+ "jwe53ewbDrF7rWwujewlkFOtWHQZM2PfH01cdxbEfJbV4IBPbF2gZAmqoscwrUDv\r\n"
			+ "95ZTV6Ub48EJTQ9bbV8exhwYfHU8J0RX8NPWGo7gKSm0+SyM/dbXFWrAxjIF7ViU\r\n"
			+ "qoYbfUBKGOyybSoMR+VNJ6ax3xng7kQiteuJ6TZ1FSLG6vHqWVyt08hh19PRtdEF\r\n"
			+ "qMcKQCI4oNI+QXUJ0i/FhsSInhoIwBHP3QGpTH2y1rJlHCIpRvhrBl8WYFmNrpKQ\r\n"
			+ "+4BmIwjp7dRTZe5FssgaeYL4HA/OwGGpnvwuFU3ecJEdzbqcSqMXipnP+nUp4VlZ\r\n"
			+ "xsPR+T9vEkjNYDmSvQrMak3mr+BPc+O4plXtJFYkSUeZIjnYRTHH9IoDKinN1Uqi\r\n"
			+ "GKyKqL/WWSeXs2ZIiNcA/lKis9xst+LqXIRFvskAimvIPvRQ0U2w/sBfldYD/DrL\r\n"
			+ "EADf6IkGqtge4a0enRAVCs4+LoTs6Msw3sADpC3Pp7wDNQgRPGEDnwTeJLmr0q4s\r\n"
			+ "i0PAGJSXy0tqWbCAoOsAuGbYMG5zZJDo0gGK1u7JpLULIBVJ1ok7n1oH2EGBjrkx\r\n"
			+ "n7mtZ6bIJjSHxMyAAtO3U4Bi0a40TtJy44gAA6IZTspWnxqdx6S2KuJmbFDOFmTH\r\n"
			+ "MwfMZwdCcq5OyuS8HdmGqW5XxoQMhDkOhhhzzJSUcQ9X/KFgsFNqJ2Ps6RV5PJ71\r\n"
			+ "i2/XknH1rGO18pSwbrClYJ64fRew6p52ZYdNAC6KHn10Pns0G+lpPntlv2NF5tcv\r\n"
			+ "f++/642/ceOu+27ce8/dL++LN2av+c3/XycfeOK+G8/+HfIP+ufedM+fP9999J6X\r\n"
			+ "ePb2rX/+6kx/8rO/uO+3P/m0+uofwBnxwJc/99it737g++/rKeVdT9iP/xf9u9vq\r\n"
			+ "q3/zlCI++yf/9t/v+MVPbt0buk98604a8v+CJz+8/cD0xmd+50PfGj/+4oOvTZ8L\r\n"
			+ "PvaXb//5L9/02r+/c/+nrDvPwk9+r/nObfaLD774hfdQzzz11K2f/uf/3P7317+K\r\n"
			+ "f/rb4vEb/t8+Nyuk+x/c//ifvjt95cVP3//OvxaefuHdv8X80eZ1j9y7rv5gzT/+\r\n"
			+ "sx//K6+/9GEXK9/5rucr6u5b9tc+8t5v//Hdl+fQH/3jG/7MGr75H1/6q1r8zu3H\r\n"
			+ "2Lf+8BPPvPSW8pPvfeT5P80fIl/4gvqxz//hZ9/8Nf09L/zFZz709ff/3iO//78=\r\n"
			+ "=gOJJ\r\n"
			+ "-----END PGP MESSAGE-----\r\n";
	
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

	@Autowired
	PGPService pgpService;

	@Test
	public void isTrue() {
		Assert.assertTrue(true);
	}

	@Test
	public void verify() throws BadSignatureException, NoSignaturePacketException, Exception {
		PGPPublicKey catPubkey = pgpService.extractPubkey(CAT_PUBKEY);
//		Assert.assertEquals(CAT_SIGNATURE_HASH, new Sha1(CAT_DETACHED_SIGNATURE_HASH).toString().toUpperCase());
		Assert.assertTrue(pgpService.verify(CAT_SIGNATURE_DATA, CAT_DETACHED_SIGNATURE, catPubkey));
	}
}
