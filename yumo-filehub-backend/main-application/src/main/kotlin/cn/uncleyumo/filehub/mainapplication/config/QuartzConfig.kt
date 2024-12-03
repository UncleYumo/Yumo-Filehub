package cn.uncleyumo.filehub.mainapplication.config

//import filehub.uncleyumo.cn.mainapplication.job.TestJob
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author uncle_yumo
 * @fileName QuartzConfig
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

//@Configuration  // Declare as a Spring configuration
class QuartzConfig {

//    @Bean
//    fun jobDetail(): JobDetail {
//        return JobBuilder.newJob(TestJob::class.java)
//            .storeDurably()  // 持久化
//            .withIdentity("testJob", "testGroup")  // 定义job的名称和组： 唯一标识
//            .usingJobData("count", 0)  // 共享数据的初始化
//            .build()
//    }

//    @Bean
//    fun trigger(): Trigger {
//        return TriggerBuilder.newTrigger()
//            .forJob(jobDetail())  // 定义trigger关联的job
//            // Cron表达式： 秒 分 时 日 月 周 年
//            .withSchedule(CronScheduleBuilder.cronSchedule("0/4 * * * * ?"))  // 每隔2秒执行一次
////            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 6,12,18,0 * * ?"))
//            .withIdentity("testTrigger", "testGroup")  // 定义trigger的名称和组： 唯一标识
//            .build()
//    }
}