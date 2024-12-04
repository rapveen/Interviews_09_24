package cache.exception;

/**
 * Base exception for cache-related errors.
 */
public class CacheException extends RuntimeException {
    public CacheException(String message) {
        super(message);
    }
}
