package filehub.uncleyumo.cn.mainapplication.utils

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONWriter
import org.springframework.data.redis.serializer.RedisSerializer
import java.nio.charset.Charset

/**
 * @author uncle_yumo
 * @fileName FastJson2JsonRedisSerializer
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

class FastJson2JsonRedisSerializer<T>(private val clazz: Class<T>) : RedisSerializer<T> {

    private val charset = Charset.forName("UTF-8")

    override fun serialize(value: T?): ByteArray {
        return JSON.toJSONString(value, JSONWriter.Feature.WriteNulls).toByteArray(charset)
    }

    override fun deserialize(bytes: ByteArray?): T? {
        if (bytes == null) return null
        val json = String(bytes, charset)
        return JSON.parseObject(json, clazz)
    }
}
