package com.szewec.controller;

import com.szewec.entity.TaskDefine;
import com.szewec.execute.QuartzJobLogic;
import com.szewec.service.QuartzJobService;
import com.szewec.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 定时任务管理控制器
 *
 * @author QZB
 * @create 2019-09-05 10:52
 */
@RestController
@RequestMapping("/api/")
@Api(value = "定时任务管理")
@Slf4j
public class QuartzJobController {
    @Resource
    private QuartzJobService quartzJobService;

    //假设这个定时任务的名字叫做HelloWorld, 组名GroupOne
    private final JobKey jobKey = JobKey.jobKey("HelloWorld", "GroupOne");

    @GetMapping("/startJob")
    @ApiOperation(value = "启动定时任务", notes = "启动定时任务")
    public Object startJob() {
        try {
            //创建一个定时任务
            TaskDefine task = TaskDefine.builder()
                    .jobKey(jobKey)
                    .cronExpression("0/10 * * * * ? ")   //定时任务的cron表达式（每10秒执行一次）
                    .jobClass(QuartzJobLogic.class)     //定时任务的具体执行逻辑
                    .description("这是一个测试的任务")     //定时任务的描述
                    .build();

            quartzJobService.scheduleJob(task);

            return ResponseUtil.successResponse("定时任务启动成功！");
        } catch (Exception e) {
            log.error("定时任务启动异常！", e);
            return ResponseUtil.failedResponse("定时任务启动异常！", e.getMessage());
        }
    }

    @GetMapping("/pauseJob")
    @ApiOperation(value = "暂停定时任务", notes = "暂停定时任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "job名称", paramType = "query", dataType = "string", required = true),
            @ApiImplicitParam(name = "jobGroup", value = "job组名", paramType = "query", dataType = "string", required = true)
    })
    public Object pauseJob(String jobName, String jobGroup) {
        try {
            return ResponseUtil.successObjectResponse(quartzJobService.pauseJob(jobName, jobGroup));
        } catch (Exception e) {
            log.error("暂停定时任务异常！", e);
            return ResponseUtil.failedResponse("暂停定时任务异常！", e.getMessage());
        }
    }

    @GetMapping("/pauseAllJob")
    @ApiOperation(value = "暂停所有定时任务", notes = "暂停所有定时任务")
    public Object pauseAllJob() {
        try {
            quartzJobService.pauseAllJob();
            return ResponseUtil.successResponse("暂停所有定时任务成功！");
        } catch (Exception e) {
            log.error("暂停所有定时任务异常！", e);
            return ResponseUtil.failedResponse("暂停所有定时任务异常！", e.getMessage());
        }
    }

    @GetMapping("/resumeJob")
    @ApiOperation(value = "恢复定时任务执行", notes = "恢复定时任务执行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "job名称", paramType = "query", dataType = "string", required = true),
            @ApiImplicitParam(name = "jobGroup", value = "job组名", paramType = "query", dataType = "string", required = true)
    })
    public Object resumeJob(String jobName, String jobGroup) {
        try {
            return ResponseUtil.successResponse(quartzJobService.resumeJob(jobName, jobGroup));
        } catch (Exception e) {
            log.error("恢复定时任务执行异常！", e);
            return ResponseUtil.failedResponse("恢复定时任务执行异常！", e.getMessage());
        }
    }

    @GetMapping("/resumeAllJob")
    @ApiOperation(value = "恢复所有定时任务执行", notes = "恢复所有定时任务执行")
    public Object resumeAllJob() {
        try {
            quartzJobService.resumeAllJob();
            return ResponseUtil.successResponse("恢复定时任务执行成功！");
        } catch (Exception e) {
            log.error("恢复所有定时任务执行异常！", e);
            return ResponseUtil.failedResponse("恢复所有定时任务执行异常！", e.getMessage());
        }
    }

    @GetMapping("/deleteJob")
    @ApiOperation(value = "删除定时任务", notes = "删除定时任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobName", value = "job名称", paramType = "query", dataType = "string", required = true),
            @ApiImplicitParam(name = "jobGroup", value = "job组名", paramType = "query", dataType = "string", required = true)
    })
    public Object deleteJob(String jobName, String jobGroup) {
        try {
            return ResponseUtil.successObjectResponse(quartzJobService.deleteJob(jobName, jobGroup));
        } catch (Exception e) {
            log.error("删除定时任务异常！", e);
            return ResponseUtil.failedResponse("删除定时任务异常！", e.getMessage());
        }
    }

    @GetMapping("/modifyJobCron")
    @ApiOperation(value = "修改定时任务cron表达式", notes = "修改定时任务cron表达式")
    public Object modifyJobCron() {
        try {
            //这是即将要修改cron的定时任务
            TaskDefine modifyCronTask = TaskDefine.builder()
                    .jobKey(jobKey)
                    .cronExpression("0/5 * * * * ? ")   //定时任务的cron表达式（每5秒执行一次）
                    .jobClass(QuartzJobLogic.class)     //定时任务的具体执行逻辑
                    .description("这是一个测试的任务")     //定时任务的描述
                    .build();
            if (quartzJobService.modifyJobCron(modifyCronTask)) {
                return ResponseUtil.successResponse("修改定时任务cron表达式成功！");
            } else {
                return ResponseUtil.failedResponse("修改定时任务cron表达式失败！", "");
            }
        } catch (Exception e) {
            log.error("修改定时任务cron表达式异常！", e);
            return ResponseUtil.failedResponse("修改定时任务cron表达式异常！", e.getMessage());
        }
    }
}
