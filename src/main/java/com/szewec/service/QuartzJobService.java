package com.szewec.service;

import com.szewec.entity.TaskDefine;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * 核心其实就是Scheduler的功能, 这里只是非常简单的示例说明其功能。
 * 如需根据自身业务进行扩展请参考 {@link org.quartz.Scheduler}
 *
 * @author QZB
 * @create 2019-09-05 11:30
 */
@Slf4j
@Service
public class QuartzJobService {
    //Quartz定时任务核心的功能实现类
    @Autowired
    private Scheduler scheduler;

    /**
     * 创建和启动定时任务
     * {@link org.quartz.Scheduler#scheduleJob(JobDetail, Trigger)}
     *
     * @param define 定时任务
     */
    public void scheduleJob(TaskDefine define) throws SchedulerException {
        //1.定时任务的名字和组名
        JobKey jobKey = define.getJobKey();
        //2.定时任务的元数据
        JobDataMap jobDataMap = getJobDataMap(define.getJobDataMap());
        //3.定时任务的描述
        String description = define.getDescription();
        //4.定时任务的逻辑实现类
        Class<? extends Job> jobClass = define.getJobClass();
        //5.定时任务的cron表达式
        String cron = define.getCronExpression();

        JobDetail jobDetail = getJobDetail(jobKey, description, jobDataMap, jobClass);
        Trigger trigger = getTrigger(jobKey, description, jobDataMap, cron);

        scheduler.scheduleJob(jobDetail, trigger);
    }


    /**
     * 根据Job名称和组名暂停Job
     *
     * @param jobName  Job名称
     * @param jobGroup Job组名
     * @return
     * @throws SchedulerException
     */
    public String pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        if (jobDetail == null) {
            return "jobDetail is null!";
        } else if (!scheduler.checkExists(jobKey)) {
            return "jobKey is not exists!";
        } else {
            scheduler.pauseJob(jobKey);
            return "success!";
        }
    }

    /**
     * 暂停所有的Job
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 恢复所有Job执行
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 根据Job名称和Job组名恢复Job执行
     *
     * @param jobName  Job名称
     * @param jobGroup Job组名
     * @return
     * @throws SchedulerException
     */
    public String resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        if (jobDetail == null) {
            return "jobDetail is null!";
        } else if (!scheduler.checkExists(jobKey)) {
            return "jobKey is not exists!";
        } else {
            scheduler.resumeJob(jobKey);
            return "success!";
        }
    }

    /**
     * 根据Job名称和Job组名删除Job
     *
     * @param jobName
     * @param jobGroup
     * @throws SchedulerException
     */
    public String deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        if (jobDetail == null) {
            return "jobDetail is null!";
        } else if (!scheduler.checkExists(jobKey)) {
            return "jobKey is not exists!";
        } else {
            scheduler.deleteJob(jobKey);
            return "success!";
        }
    }


    /**
     * 修改Job的cron表达式
     *
     * @param define
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJobCron(TaskDefine define) throws SchedulerException {
        String cronExpression = define.getCronExpression();

        //1.如果cron表达式的格式不正确,则返回修改失败
        if (!CronExpression.isValidExpression(cronExpression)) return false;

        JobKey jobKey = define.getJobKey();
        TriggerKey triggerKey = new TriggerKey(jobKey.getName(), jobKey.getGroup());

        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        JobDataMap jobDataMap = getJobDataMap(define.getJobDataMap());
        //2.如果cron发生变化了,则按新cron触发 进行重新启动定时任务
        if (!cronTrigger.getCronExpression().equalsIgnoreCase(cronExpression)) {
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .usingJobData(jobDataMap)
                    .build();

            scheduler.rescheduleJob(triggerKey, trigger);
        }

        return true;
    }


    /**
     * 获取定时任务的定义
     * JobDetail是任务的定义,Job是任务的执行逻辑
     *
     * @param jobKey      定时任务的名称组名
     * @param description 定时任务的描述
     * @param jobDataMap  定时任务的元数据
     * @param jobClass    定时任务的真正执行逻辑定义类
     * @return
     */
    public JobDetail getJobDetail(JobKey jobKey, String description, JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(description)
                .setJobData(jobDataMap)
                .usingJobData(jobDataMap)
                .requestRecovery()
                .storeDurably()
                .build();
    }


    /**
     * 获取Trigger (Job的触发器,执行规则)
     *
     * @param jobKey         定时任务的名称组名
     * @param description    定时任务的描述
     * @param jobDataMap     定时任务的元数据
     * @param cronExpression 定时任务的执行cron表达式
     */
    public Trigger getTrigger(JobKey jobKey, String description, JobDataMap jobDataMap, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(description)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(jobDataMap)
                .build();
    }


    /**
     * 获取定时任务的元数据
     *
     * @param map
     * @return
     */
    public JobDataMap getJobDataMap(Map<?, ?> map) {
        return map == null ? new JobDataMap() : new JobDataMap(map);
    }

}
