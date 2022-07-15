package wcg.shared;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardGameExceptionTest {
	String message = "some message";
	Throwable cause;
	
	@BeforeEach
	void setUp() throws Exception {
		cause = new Throwable();
		message = "some message";
	}

	/**
	 * Check the creation of exceptions with no arguments.
	 */
	@Test
	void testCardGameException() {
		assertNotNull( new CardGameException() );
	}

	/**
	 * Check the creation of exceptions with a message, a cause and flags.
	 */
	@Test
	void testCardGameExceptionStringThrowableBooleanBoolean() {
		final CardGameException exception = 
				new CardGameException(message,cause,true,true);
		
		assertNotNull(exception);
		assertAll(
				() -> assertEquals( message , exception.getMessage()),
				() -> assertEquals( cause , exception.getCause())
			);
	}

	/**
	 * Check the creation of exceptions with a message and a cause.
	 */
	@Test
	void testCardGameExceptionStringThrowable() {
		final CardGameException exception = 
				new CardGameException(message,cause);
		
		assertNotNull(exception);
		assertAll(
				() -> assertEquals( message , exception.getMessage()),
				() -> assertEquals( cause , exception.getCause())
			);
		
	}

	/**
	 * Check the creation of exceptions with a message
	 */
	@Test
	void testCardGameExceptionString() {
		final CardGameException exception = new CardGameException(message);
		
		assertNotNull(exception);
		assertEquals( message , exception.getMessage() );
	}

	/**
	 * Check the creation of exceptions with a cause
	 */
	@Test
	void testCardGameExceptionThrowable() {
		final CardGameException exception = new CardGameException(cause);
		
		assertNotNull(exception);
		assertEquals( cause , exception.getCause());
	}

}
