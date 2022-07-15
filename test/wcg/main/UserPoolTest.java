package wcg.main;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.Serializable;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wcg.WebCardGameTest;
import wcg.shared.CardGameException;

/**
 * Test UserPool methods
 */
class UserPoolTest extends WebCardGameTest {
	static UserPool pool;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		pool = UserPool.getInstance();
	}
	
	@BeforeEach
	void setUp() throws Exception {
	
		pool.reset();
		UserPool.setBackupFile(MY_BACKUP_FILE);
	}
	
	@AfterAll
	static void cleanUpAfterClass() throws Exception {
		cleanupFiles();
	}
	
	/**
	 * Check if backup file can be recovered
	 */
	@Test
	void testGetBackupFile() {
		File file = UserPool.getBackupFile();
		
		assertNotNull( file , "a File instance expected");
	}

	/**
	 * Check if backup can be changed using a file
	 */
	@Test
	void testSetBackupFileFile() {
		
		UserPool.setBackupFile(OTHER_FILE);
		
		assertEquals(OTHER_FILE.getAbsoluteFile(), UserPool.getBackupFile());
	}

	/**
	 * Check if backup can be changed using a filename
	 */
	@Test
	void testSetBackupFileString() {
		
		UserPool.setBackupFile(OTHER_FILENAME);
		
		assertEquals(new File(OTHER_FILENAME).getAbsoluteFile(), UserPool.getBackupFile());
	}

	/**
	 * Save a backup, restore it and check a file was created and its content
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testBackupRestore() throws CardGameException {
		if(MY_BACKUP_FILE.exists())
			MY_BACKUP_FILE.delete();
		
		pool.addUser(NICK, PASSWORD); 
		UserPool.backup(pool);
		
		UserPool other = UserPool.restore();
		
		assertAll(
				() -> assertTrue( MY_BACKUP_FILE.exists() ,"file should exist"),
				() -> assertNotNull(other , "an object should be recovered"),
				() -> assertEquals(NICK,other.getUser(NICK, PASSWORD).getNick(),
						"check if it contains added user"),
				() -> {
					MY_BACKUP_FILE.delete();
					assertNull( UserPool.restore() , 
							"missing file shoudl return null");
				}
			);
	}

	/**
	 * Check if instance exists and is serializable
	 */
	@Test
	void testUserPool() {
		assertNotNull(pool, "instance expected");
		assertTrue( Serializable.class.isInstance(pool) ,"should be serializable");
	}


	/**
	 * Add an user with invalid arguments and look for exceptions
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddUser_invalid() throws CardGameException {
		
		assertAll(
				() -> assertThrows(CardGameException.class,
						() -> pool.addUser(null, null)),	
		
				() -> assertThrows(CardGameException.class,
						() -> pool.addUser(NICK, null)),
		
				() -> assertThrows(CardGameException.class,
						() -> pool.addUser(null, PASSWORD))
		
		);
	}
	
	/**
	 * Try to add the same user twice and an exception should be raised.
	 * 
	 * @throws CardGameException
	 */
	@Test
	void testAddUser_twice() throws CardGameException {
		pool.addUser(NICK, PASSWORD);
		
		assertThrows( CardGameException.class,
				() -> pool.addUser(NICK, PASSWORD), 
				"should raise exception if same user is added twice");
	}
	

	/**
	 * Check if an exception is thrown with {@code null} or wrong arguments. 
	 * With the correct arguments, check if the expected user is retrieved. 
	 *  
	 * @throws CardGameException
	 */
	@Test
	void testGetUser() throws CardGameException {
		
		pool.addUser(NICK, PASSWORD);
		
		assertAll(
				() -> assertThrows(CardGameException.class, 
						() -> pool.getUser(null, null) ),
				() -> assertThrows(CardGameException.class, 
						() -> pool.getUser(NICK, null) ), 
				() -> assertThrows(CardGameException.class,
						() -> pool.getUser(null, PASSWORD) ),
				() -> assertThrows(CardGameException.class,
						() -> pool.getUser(NICK, null) ),
				() -> assertThrows(CardGameException.class,
						() -> pool.getUser("wrong nick",PASSWORD) ),
				() -> assertThrows(CardGameException.class,
						() -> pool.getUser(NICK, "wrong password") ),
				() -> {
					User user = pool.getUser(NICK, PASSWORD);
					
					assertEquals(NICK, user.getNick());
					assertTrue( user.authenticate(PASSWORD) );
				}
		);
	}

}
