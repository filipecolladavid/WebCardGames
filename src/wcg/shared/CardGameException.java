/**
 * 
 */
package wcg.shared;

import java.io.Serializable;
import java.lang.Throwable;
import java.lang.String;

/**
 * An exception thrown by web card games.
 */
public class CardGameException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;

	public CardGameException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CardGameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CardGameException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CardGameException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CardGameException(Throwable cause) {
		super(cause);
	}
}
