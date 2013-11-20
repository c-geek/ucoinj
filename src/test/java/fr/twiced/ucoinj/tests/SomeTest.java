package fr.twiced.ucoinj.tests;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fr.twiced.ucoinj.dao.PublicKeyDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"classpath:test-context.xml" })
public class SomeTest {

	@Autowired
	PublicKeyDao pubkeyDao;
	
	@Test
	public void isTrue(){
		Assert.assertTrue(true);
	}
	
	@Test
	public void hasOnePublicKey(){
		Assert.assertNotNull(pubkeyDao.getByFingerprint("6E2685A6799D6525B306B9ACEA116E654FE5B106"));
		Assert.assertNull(pubkeyDao.getByFingerprint("7E2685A6799D6525B306B9ACEA116E654FE5B106"));
	}
}
