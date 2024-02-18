public class RedisExecuteException extends RuntimeException {
	public RedisExecuteException() {
	}

	public RedisExecuteException(String message) {
		super(message);
	}

	public RedisExecuteException(String message, Throwable cause) {
		super(message, cause);
	}
}
