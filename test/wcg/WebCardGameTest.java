package wcg;

import java.io.File;

/**
 * Constants for test classes
 * 
 */
public class WebCardGameTest {
	protected static final int MAX_CARDS = 52;
	protected static final int REPETITIONS = 100;
	
	protected static final long DEFAULT_EXPIRATION_TIME = 10*60*1000L;
	protected static final long SMALL_EXPIRATION_TIME = 100L;
	
	protected static final String GAME_ID_EX = "WAR0";
	protected static final String GAME_NAME = "WAR";
	protected static final String NICK = "NICK";
	protected static final String PASSWORD = "PASSWORD";
	
	protected static final String[] NICKS = { "NICK0" , "NICK1" };
	
	protected static final String MY_BACKUP_FILENAME = "backup.ser";
	protected static final String OTHER_FILENAME = "other.ser";
	protected static final File   MY_BACKUP_FILE = new File(MY_BACKUP_FILENAME);
	protected static final File   OTHER_FILE = new File(OTHER_FILENAME);
	
	protected static final File[] TEST_FILES = new File[] { MY_BACKUP_FILE };
	
	
	
	
	protected static void cleanupFiles() {
		for(File file: TEST_FILES)
			if(file.exists())
				file.deleteOnExit();
	}
}
