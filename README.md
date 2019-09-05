# springboot-quartz-demo
SpringBoot框架整合Quartz完成定时任务调度

1. demo概述
   + 该demo基于SpringBoot2.1.6和Quartz2.2.3框架，提供了相关管理定时任务的接口；
   + 该demo集成了Swagger框架，项目启动成功后访问<http://localhost:8080/swagger-ui.html#/>即可查看项目对应的Swagger文档；
   + 该demo支持MySQL和postgreSQL两种数据库，相关数据库初始化脚本可见resources目录，切换数据库时需同步的修改application.yml配置（datasource和driverDelegateClass）

2. Quartz的3个基本要素
+ Scheduler：调度器，所有的调度都是由它控制。
+ Trigger：触发器，决定什么时候来执行任务。
+ JobDetail & Job：JobDetail定义的是任务数据，而真正的执行逻辑是在Job中。使用JobDetail + Job而不是Job，这是因为任务是有可能并发执行，如果Scheduler直接使用Job，就会存在对同一个Job实例并发访问的问题。而JobDetail & Job 方式，sheduler每次执行，都会根据JobDetail创建一个新的Job实例，这样就可以规避并发访问的问题。
3. 具体API操作详见demo。

