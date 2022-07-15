package wcg.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import wcg.shared.CardGameException;

/**
 * A pool of system users with methods to add and retrieve them. Instances of
 * this class are serializable to persist users in the file system. This class
 * is a <strong>singleton</strong>.
 */
public class UserPool implements Serializable {

	private static final long serialVersionUID = 1L;

	private static UserPool userPool = null;
	private static final Map<String, User> userMap = new HashMap<>();

	private static File file = new File("backup.ser");

	/**
	 * Single instance of this class. If a backup is available then the instance is
	 * recovered from a file.
	 * 
	 * @return single instance
	 * @throws CardGameException - on error restoring backup file
	 */
	static UserPool getInstance() throws CardGameException {
		if (userPool == null) {
			if (restore() == null)
				userPool = new UserPool();
			else
				userPool = restore();
		}
		return userPool;
	}

	/**
	 * Get the current file used for backup. It is an absolute pathname
	 * 
	 * @return backup file
	 */
	public static File getBackupFile() {
		return file.getAbsoluteFile();
	}

	/**
	 * Change the file used for backups. This file is persisted as an absolute
	 * pathname, even if given as a relative pathname
	 * 
	 * @param backupFile - to save the serialization
	 */
	public static void setBackupFile(File backupFile) {
		file = backupFile;
	}

	/**
	 * Convenience method to set the backup file as a string
	 * 
	 * @param name - of backup file
	 */
	public static void setBackupFile(String name) {
		file = new File(name);
	}

	/**
	 * Save given pool in file.
	 * 
	 * @param pool - to save
	 * @throws CardGameException - if backup file is not writable or I/O error
	 *                           occurs during writing.
	 */
	static void backup(UserPool pool) throws CardGameException {
		try (FileOutputStream stream = new FileOutputStream(file);
				ObjectOutputStream serializer = new ObjectOutputStream(stream);) {
			serializer.writeObject(pool);
		} catch (IOException cause) {
			cause.printStackTrace();
			throw new CardGameException("I/O error");
		}
	}

	/**
	 * Restore backup file and return saved pool.
	 * 
	 * @return user pool or null if not available
	 * @throws CardGameException - if backup file is not readable or I/O error
	 *                           occurs during reading
	 */
	static UserPool restore() throws CardGameException {
		UserPool userpool = null;
		if (file.canRead()) {
			try (FileInputStream stream = new FileInputStream(file);
					ObjectInputStream deserializer = new ObjectInputStream(stream);) {
				userpool = (UserPool) deserializer.readObject();
			} catch (IOException | ClassNotFoundException cause) {
				cause.printStackTrace();
			}
		} else {
			userpool = null;
		}
		return userpool;
	}

	/**
	 * Retrieve the user with given nick if it authenticates with given password.
	 * 
	 * @param nick     - of user
	 * @param password - of user
	 * @return user
	 * @throws CardGameException - nick or password are invalid.
	 */
	User getUser(String nick, String password) throws CardGameException {
		if (nick == null || password == null)
			throw new CardGameException("Arguments can't be null");

		User userToManage = userMap.get(nick);

		if (userToManage == null)
			throw new CardGameException("This user doesn't exist");

		if (!userToManage.authenticate(password))
			throw new CardGameException("The password doesn't match the user");

		return userToManage;
	}

	/**
	 * Add an user with given nick and password. Neither argument can have a null
	 * value.
	 * 
	 * @param nick     - of user
	 * @param password - of user
	 * @throws CardGameException - if any of the arguments is null
	 */
	void addUser(String nick, String password) throws CardGameException {
		if (nick == null || password == null)
			throw new CardGameException("Arguments can't be null");

		if (userMap.get(nick) != null)
			throw new CardGameException("This user has already been added");

		User userToAdd = new User(nick, password);

		userMap.put(nick, userToAdd);
	}

	/**
	 * Resets UserPool's fields to enable unit testing on the Manager singleton.
	 * This method must only be used during testing and shoudn't be public.
	 */
	void reset() {
		userMap.clear();
	}
}