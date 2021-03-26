package guess.dao.exception;

/**
 * Wrapper runtime exception.
 */
public class WrapperRuntimeException extends RuntimeException {
    public WrapperRuntimeException(Throwable cause) {
        super(cause);
    }
}
