package com.szewec.execute;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 在此处定义定时任务具体执行逻辑
 *
 * @author QZB
 * @create 2019-09-05 11:00
 */
@Slf4j
@DisallowConcurrentExecution
public class QuartzJobLogic implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //书写你自己的逻辑
        log.info("Job Is Running" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "......");
        log.info("Job Content:" + jobExecutionContext.toString());
    }
}
