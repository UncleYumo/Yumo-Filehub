package cn.uncleyumo.filehub.mainapplication.config

import cn.uncleyumo.filehub.mainapplication.job.FileCleanJob
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * @author uncle_yumo
 * @fileName QuartzConfig
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@Configuration  // Declare as a Spring configuration
class QuartzConfig {

    @Value("\${quartz.jobs.file-clean-job.name}")
    private lateinit var jobName: String

    @Value("\${quartz.jobs.file-clean-job.cron}")  // cron: "* 0/1 * * * ?"
    private lateinit var cron: String

    @Bean
    fun jobDetail(): JobDetail {
        return JobBuilder.newJob(FileCleanJob::class.java)
            .withIdentity(jobName)
            .storeDurably()  // 任务持久化
            .build()
    }

    @Bean
    fun trigger(): Trigger {
        return TriggerBuilder.newTrigger()
            .forJob(jobDetail())  // 定义trigger关联的job
//            .startAt(Date(System.currentTimeMillis() + 3000))  // 任务延迟3秒启动
            .withSchedule(CronScheduleBuilder.cronSchedule(cron))
            .build()
    }
}