package cn.uncleyumo.filehub.mainapplication.job

import cn.uncleyumo.utils.ColorPrinter
import cn.uncleyumo.utils.LogPrinter
import cn.uncleyumo.utils.enum.QuickTemplateEnum
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.PersistJobDataAfterExecution

/**
 * @author uncle_yumo
 * @fileName TestJob
 * @createDate 2024/12/3 December
 * @school 无锡学院
 * @studentID 22344131
 * @description
 */

@PersistJobDataAfterExecution  // 持久化JobDataMap
@DisallowConcurrentExecution  // 不允许并发执行: 防止并发地访问同一个JobDetail实例
class TestJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val jobDetail: JobDetail? = context?.jobDetail

        QuickTemplateEnum.PROGRAM_START.println()

        // 输出任务的Name
        ColorPrinter.printlnCyanRed("Job Name: ${jobDetail?.key?.name ?: "name is null"}")
        // 输出任务的Group
        ColorPrinter.printlnCyanBlack("Job Group: ${jobDetail?.key?.group ?: "group is null"}")
        // 输出任务的CLass
        ColorPrinter.printlnCyanBlack("Job Class: ${jobDetail?.jobClass?.name ?: "class is null"}")
        // 输出执行的时间
        ColorPrinter.printlnCyanBlack("Current Execution Time: ${context?.fireTime ?: "time is null"}")
        // 输出下次执行的时间
        ColorPrinter.printlnCyanBlack("Next Execution Time: ${context?.nextFireTime ?: "time is null"}")

        // 记录任务执行次数
        val jboDataMap: JobDataMap? = jobDetail?.jobDataMap
        val count: Int = jboDataMap?.getInt("count") ?: -1
        LogPrinter.error("This is the $count time execute this job.")
        jboDataMap?.put("count", count + 1)

        QuickTemplateEnum.PROGRAM_END.println()
    }
}