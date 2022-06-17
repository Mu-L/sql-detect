package com.tangym.sql.server.util;

import com.tangym.sql.server.entity.Quartz;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class QuartzUtils {
    /**
     * 创建定时任务 定时任务创建之后默认启动状态
     *
     * @param scheduler 调度器
     * @param quartz    定时任务信息类
     * @throws Exception
     */
    public static void createScheduleJob(Scheduler scheduler, Quartz quartz) throws SchedulerException, ClassNotFoundException {
        //获取到定时任务的执行类  必须是类的绝对路径名称
        //定时任务类需要是job类的具体实现 QuartzJobBean是job的抽象类。
        Class<? extends Job> jobClass = (Class<? extends Job>) Class.forName(quartz.getJobClass());
        // 构建定时任务信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(quartz.getJobName()).build();
        // 设置定时任务执行方式
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
        // 构建触发器trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(quartz.getJobName()).withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 根据任务名称暂停定时任务
     *
     * @param scheduler 调度器
     * @param jobName   定时任务名称
     * @throws SchedulerException
     */
    public static void pauseScheduleJob(Scheduler scheduler, String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName);
        scheduler.pauseJob(jobKey);
    }

    /**
     * 根据任务名称恢复定时任务
     *
     * @param scheduler 调度器
     * @param jobName   定时任务名称
     * @throws SchedulerException
     */
    public static void resumeScheduleJob(Scheduler scheduler, String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName);
        scheduler.resumeJob(jobKey);
    }

    /**
     * 根据任务名称立即运行一次定时任务
     *
     * @param scheduler 调度器
     * @param jobName   定时任务名称
     * @throws SchedulerException
     */
    public static void runOnce(Scheduler scheduler, String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName);
        scheduler.triggerJob(jobKey);
    }

    /**
     * 更新定时任务
     *
     * @param scheduler 调度器
     * @param quartz    定时任务信息类
     * @throws SchedulerException
     */
    public static void updateScheduleJob(Scheduler scheduler, Quartz quartz) throws SchedulerException {
        //获取到对应任务的触发器
        TriggerKey triggerKey = TriggerKey.triggerKey(quartz.getJobName());
        //设置定时任务执行方式
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartz.getCronExpression());
        //重新构建任务的触发器trigger
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        //重置对应的job
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 根据定时任务名称从调度器当中删除定时任务
     *
     * @param scheduler 调度器
     * @param jobName   定时任务名称
     * @throws SchedulerException
     */
    public static void deleteScheduleJob(Scheduler scheduler, String jobName) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName);
        scheduler.deleteJob(jobKey);
    }

    public static Trigger.TriggerState getStatusOfScheduleJob(Scheduler scheduler, Quartz quartz) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(quartz.getJobName());
            return scheduler.getTriggerState(triggerKey);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
