package cn.uncleyumo.filehub.mainapplication.config

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import com.alibaba.fastjson2.support.spring.data.redis.FastJsonRedisSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * @author uncle_yumo
 * @fileName RedisConfig
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Configuration
class RedisConfig {

    @Bean
    fun userDTORedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, UserDTO> {
        val template = RedisTemplate<String, UserDTO>()
        return template.apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            val fastJsonRedisSerializer = FastJsonRedisSerializer(UserDTO::class.java)
            valueSerializer = fastJsonRedisSerializer
        }
    }

    @Bean
    fun fileDTORedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, FileDTO> {
        val template = RedisTemplate<String, FileDTO>()
        return template.apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            val fastJsonRedisSerializer = FastJsonRedisSerializer(FileDTO::class.java)
            valueSerializer = fastJsonRedisSerializer
        }
    }
}