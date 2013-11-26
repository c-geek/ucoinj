package fr.twiced.ucoinj.tests;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.Signature;
import fr.twiced.ucoinj.exceptions.BadSignatureException;
import fr.twiced.ucoinj.exceptions.NoPublicKeyPacketException;
import fr.twiced.ucoinj.exceptions.ObsoleteDataException;
import fr.twiced.ucoinj.service.PGPService;
import fr.twiced.ucoinj.service.PKSService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:test-context.xml" })
public class PKSServiceTest {
	
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
	
	private static final String CAT_PUBKEY_DETACHED_SIGN = "-----BEGIN PGP SIGNATURE-----\r\n"
			+ "Version: GnuPG v1.4.11 (GNU/Linux)\r\n"
			+ "\r\n"
			+ "iQEcBAABCAAGBQJSkiIfAAoJEOnKt20ZqGUeG74H/2uCAuCGQVgftbGSWV80985+\r\n"
			+ "gDyijCaTC2ZBHIBOsr3m8+QqaM/T9hVa5S+K5v0M9LLDh+lc6Tw7D33NOa/5wD0v\r\n"
			+ "mCB2xRKOppLCrzasrcDhUFGTzKyraybYKb72YB9jA4IRkB6Mk+j0Tomd55xlsw+4\r\n"
			+ "gg+3ixthCtSuEBXFIU5NmoamySYndKPv+OU5x7BW7e7TPP/NjfhjU3uR9EuFCwcU\r\n"
			+ "ra5V7BjQW94z2HFVJLKGAuhi2Se2jX9MtQURkihsX3V9T1sJl4Rf2jY9x5JqivO0\r\n"
			+ "Fp4y7txOBneZR5TC9mE58QN2SiR4J/BzZmgQptOySyR0nlTzVhn8NxMWiz1V56A=\r\n"
			+ "=4UKU\r\n"
			+ "-----END PGP SIGNATURE-----\r\n";

	@Autowired
	private PGPService pgpService;
	
	@Autowired
	private PKSService pksService;

	@Test
	@Transactional
	public void pksAdd() throws NoPublicKeyPacketException, IOException, ObsoleteDataException, BadSignatureException {
		PublicKey catPubkey = pgpService.extractPublicKey(CAT_PUBKEY);
		Assert.assertNotNull(pksService.add(catPubkey, new Signature(CAT_PUBKEY_DETACHED_SIGN)));
	}
}
