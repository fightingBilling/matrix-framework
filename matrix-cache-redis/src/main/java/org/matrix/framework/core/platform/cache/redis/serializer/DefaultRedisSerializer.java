package org.matrix.framework.core.platform.cache.redis.serializer;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class DefaultRedisSerializer implements RedisSerializer<Object> {

    public static final FSTConfiguration CONFIGURATION = FSTConfiguration.createFastBinaryConfiguration();

    static {
        //treat unserializable classes same as if they would be serializable.
        CONFIGURATION.setForceSerializable(true);
    }

    @Override
    public byte[] serialize(Object t) throws SerializationException {
        return CONFIGURATION.asByteArray(t);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return (Object) CONFIGURATION.asObject(bytes);
    }

}
