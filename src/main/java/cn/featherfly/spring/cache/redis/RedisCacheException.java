
package cn.featherfly.spring.cache.redis;

import java.util.Locale;

import cn.featherfly.common.exception.ExceptionCode;
import cn.featherfly.common.exception.LocalizedException;

/**
 * <p>
 * RedisCacheException
 * </p>
 * 
 * @author zhongj
 */
public class RedisCacheException extends LocalizedException {

    /**
     * 
     */
    public RedisCacheException() {
        super();

    }

    /**
     * @param exceptionCode
     * @param locale
     * @param ex
     */
    public RedisCacheException(ExceptionCode exceptionCode, Locale locale, Throwable ex) {
        super(exceptionCode, locale, ex);

    }

    /**
     * @param exceptionCode
     * @param locale
     */
    public RedisCacheException(ExceptionCode exceptionCode, Locale locale) {
        super(exceptionCode, locale);

    }

    /**
     * @param exceptionCode
     * @param argus
     * @param locale
     * @param ex
     */
    public RedisCacheException(ExceptionCode exceptionCode, Object[] argus, Locale locale, Throwable ex) {
        super(exceptionCode, argus, locale, ex);

    }

    /**
     * @param exceptionCode
     * @param argus
     * @param locale
     */
    public RedisCacheException(ExceptionCode exceptionCode, Object[] argus, Locale locale) {
        super(exceptionCode, argus, locale);

    }

    /**
     * @param exceptionCode
     * @param argus
     * @param ex
     */
    public RedisCacheException(ExceptionCode exceptionCode, Object[] argus, Throwable ex) {
        super(exceptionCode, argus, ex);

    }

    /**
     * @param exceptionCode
     * @param argus
     */
    public RedisCacheException(ExceptionCode exceptionCode, Object[] argus) {
        super(exceptionCode, argus);

    }

    /**
     * @param exceptionCode
     * @param ex
     */
    public RedisCacheException(ExceptionCode exceptionCode, Throwable ex) {
        super(exceptionCode, ex);

    }

    /**
     * @param exceptionCode
     */
    public RedisCacheException(ExceptionCode exceptionCode) {
        super(exceptionCode);

    }

    /**
     * @param message
     * @param locale
     * @param ex
     */
    public RedisCacheException(String message, Locale locale, Throwable ex) {
        super(message, locale, ex);

    }

    /**
     * @param message
     * @param locale
     */
    public RedisCacheException(String message, Locale locale) {
        super(message, locale);

    }

    /**
     * @param message
     * @param argus
     * @param locale
     * @param ex
     */
    public RedisCacheException(String message, Object[] argus, Locale locale, Throwable ex) {
        super(message, argus, locale, ex);

    }

    /**
     * @param message
     * @param argus
     * @param locale
     */
    public RedisCacheException(String message, Object[] argus, Locale locale) {
        super(message, argus, locale);

    }

    /**
     * @param message
     * @param argus
     * @param ex
     */
    public RedisCacheException(String message, Object[] argus, Throwable ex) {
        super(message, argus, ex);

    }

    /**
     * @param message
     * @param argus
     */
    public RedisCacheException(String message, Object[] argus) {
        super(message, argus);

    }

    /**
     * @param message
     * @param ex
     */
    public RedisCacheException(String message, Throwable ex) {
        super(message, ex);

    }

    /**
     * @param message
     */
    public RedisCacheException(String message) {
        super(message);

    }

    /**
     * @param ex
     */
    public RedisCacheException(Throwable ex) {
        super(ex);

    }

    private static final long serialVersionUID = -1158963144547857139L;
    
    /**
     * <p>
     * duplicateCacheNameException
     * </p> 
     * @param cacheName cacheName
     */
    public static void duplicateCacheNameException(String cacheName) {
        throw new RedisCacheException("#duplicate_cache_name", new Object[]{cacheName});
    }

}
