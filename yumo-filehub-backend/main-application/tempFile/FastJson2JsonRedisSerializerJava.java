package filehub.uncleyumo.cn.mainapplication.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author uncle_yumo
 * @fileName FastJson2JsonRedisSerializerJava
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

public class FastJson2JsonRedisSerializerJava<T> implements RedisSerializer<T> {
    static final Filter autoTypeFilter = JSONReader.autoTypeFilter(
            // 按需加上需要支持自动类型的类名前缀，范围越小越安全
            "org.springframework.security.core.authority.SimpleGrantedAuthority",
            "filehub.uncleyumo.cn.mainapplication.entity.pojo"

    );

    private Class<T> clazz;

    public FastJson2JsonRedisSerializerJava(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
    }

    @Override
    public T deserialize(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }

        return JSON.parseObject(bytes, clazz, autoTypeFilter);
    }
}