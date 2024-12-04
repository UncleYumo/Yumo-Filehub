package cn.uncleyumo.filehub.mainapplication.job

import cn.uncleyumo.filehub.mainapplication.entity.pojo.FileDTO
import cn.uncleyumo.filehub.mainapplication.entity.pojo.UserDTO
import cn.uncleyumo.filehub.mainapplication.utils.FileManipulationUtil
import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

/**
 * @author uncle_yumo
 * @fileName FileCleanJob
 * @createDate 2024/12/4 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Component
class FileCleanJob : Job {

    @Autowired
    private lateinit var userRedisTemplate: RedisTemplate<String, UserDTO>

    @Autowired
    private lateinit var fileRedisTemplate: RedisTemplate<String, FileDTO>

    @Autowired
    private lateinit var manipulationUtil: FileManipulationUtil

    // TODO: 实现文件清理逻辑
    override fun execute(context: JobExecutionContext?) {
        val currentTimeMillis = System.currentTimeMillis()
        LogPrinter.warn("FileCleanJob execute start\t\t>>>>>>>>>>>>>>>>>>>>")

        // 获取所有用户列表
        val redisUserDTOList: List<UserDTO> = userRedisTemplate.keys("access-key:*").mapNotNull {
            userRedisTemplate.opsForValue().get(it)
        }.also {
            LogPrinter.warn("RedisUserDTOList's length is ${it.size}")
        }

        // 获取本地accessKey列表 即本地存在的用户目录
        val localAccessKeyList: List<String> = manipulationUtil.getLocalAccessKeyList()
        LogPrinter.warn("LocalAccessKeyList's length is ${localAccessKeyList.size}")

        // 遍历本地accessKey列表并与redis中用户列表进行比对
        for (localAccessKey in localAccessKeyList) {
            ColorPrinter.printlnRedBlack("Current access key is $localAccessKey")

            if (redisUserDTOList.none { it.accessKey == localAccessKey }) {
                // 本地accessKey不在redis中，则删除该目录及其下所有文件
                manipulationUtil.deleteDirectory(localAccessKey)
                LogPrinter.error("!!!!!!Delete directory $localAccessKey success")

            } else {
                // 该accessKey在redis中，则遍历该目录下所有文件并进行比对
                ColorPrinter.printlnGreenBlack("Access key $localAccessKey is in Redis")

                // 获取本地文件列表
                val localFileUUIDNameList: List<String> = manipulationUtil.getLocalUUIDFileNameList(localAccessKey)
                LogPrinter.warn("LocalFileUUIDNameList's length is ${localFileUUIDNameList.size}")

                // 获取redis文件列表
                val redisFileDTOList: List<FileDTO> = fileRedisTemplate.keys("file:${localAccessKey}:*")
                    .mapNotNull {
                        fileRedisTemplate.opsForValue().get(it)
                    }.also {
                        LogPrinter.warn("RedisFileDTOList's length is ${it.size}")
                    }

                // 遍历本地文件列表并与redis中文件列表进行比对
                for (localFileUUIDName in localFileUUIDNameList) {
                    // 判断该文件是否在redis中 如果在redis中，则判断是否有效 如果有效，则跳过 如果无效，则调用函数删除对应的本地文件
                    val redisFileDTO: FileDTO? = redisFileDTOList.find { it.uuidFileName == localFileUUIDName }
                    if (redisFileDTO == null) {
                        // 无效文件则删除
                        manipulationUtil.deleteFile(localAccessKey, localFileUUIDName)
                        LogPrinter.error("!!!!!!Delete file $localAccessKey:$localFileUUIDName success")
                    }
                    println()
                }
            }
            println()
        }

        val endTimeMillis = System.currentTimeMillis()
        ColorPrinter.printlnGreenBlack("FileCleanJob execute time is ${endTimeMillis - currentTimeMillis} ms")
        ColorPrinter.printlnGreenBlack("Next execute time is ${context?.nextFireTime}")
        LogPrinter.warn("FileCleanJob execute end\t\t>>>>>>>>>>>>>>>>>>>>")

    }

}