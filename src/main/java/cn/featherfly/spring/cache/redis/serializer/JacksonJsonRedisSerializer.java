package cn.featherfly.spring.cache.redis.serializer;

import cn.featherfly.common.lang.ClassUtils;
import cn.featherfly.common.lang.LangUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * JacksonJsonRedisSerializer
 * @author zhongj
 */
public class JacksonJsonRedisSerializer implements RedisSerializer<Object> {

    private final ConcurrentMap<String, JavaType> javaTypeMap = new ConcurrentHashMap<>();

    private static final byte[] EMPTY_ARRAY = new byte[0];

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private byte[] cBytes = new byte[] {'@', 'c', 'l', 'a', 's', 's', '\"', ':', '\"'};

    private boolean isClassAt(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != cBytes[i]) {
                return false;
            }
        }
        return true;
    }

    private byte[] getClassName(byte[] bytes, int start) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = start; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b == '"') {
                return baos.toByteArray();
            }
            baos.write(b);
        }
        return baos.toByteArray();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     *
     */
    public JacksonJsonRedisSerializer() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (LangUtils.isEmpty(bytes)) {
            return null;
        }
        try {
            String name = null;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                if (b == '@') {
                    byte[] bs = Arrays.copyOfRange(bytes, i, i + cBytes.length);
                    if (isClassAt(bs)) {
                        name = new String(getClassName(bytes, i + cBytes.length));
                        if (LangUtils.isNotEmpty(name)) {
                            break;
                        }
                    }
                }
            }
            JavaType javaType = getJavaType(name);
            return javaType == null ? null : this.objectMapper.readValue(bytes, 0, bytes.length, javaType);
            // return this.objectMapper.readValue(bytes, 0, bytes.length,
            // Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] serialize(Object t) throws SerializationException {

        if (t == null) {
            return EMPTY_ARRAY;
        }
        try {
            return this.objectMapper.writeValueAsBytes(t);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets the {@code ObjectMapper} for this view. If not set, a default
     * {@link ObjectMapper#ObjectMapper() ObjectMapper} is used.
     * <p>
     * Setting a custom-configured {@code ObjectMapper} is one way to take
     * further control of the JSON serialization process. For example, an
     * extended {@link SerializerFactory} can be configured that provides custom
     * serializers for specific types. The other option for refining the
     * serialization process is to use Jackson's provided annotations on the
     * types to be serialized, in which case a custom-configured ObjectMapper is
     * unnecessary.
     */
    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "'objectMapper' must not be null");
        this.objectMapper = objectMapper;
    }

    protected JavaType getJavaType(Class<?> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }

    protected JavaType getJavaType(String name) {
        if (name == null)
            return null;
        JavaType javaType = javaTypeMap.get(name);
        if (javaType == null) {
            javaType = getJavaType(ClassUtils.forName(name));
            javaTypeMap.put(name, javaType);
        }
        return javaType;
    }
}
