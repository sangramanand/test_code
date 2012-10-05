package com.generalsentiment.test.quartz;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.*;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronTriggerExample implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        System.out.println("THE APPLICATION STARTED");
        try {
            Logger log = LoggerFactory.getLogger(CronTriggerExample.class);
            String path = ".";

            Properties prop = new Properties();
            prop.setProperty("org.quartz.scheduler.instanceName", "ALARM_SCHEDULER");
            prop.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            prop.setProperty("org.quartz.threadPool.threadCount", "4");
            prop.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");



            prop.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreCMT");
            prop.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
            prop.setProperty("org.quartz.jobStore.dataSource", "tasksDataStore");
            prop.setProperty("org.quartz.jobStore.nonManagedTXDataSource", "tasksDataStore");
            prop.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
            prop.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
            prop.setProperty("org.quartz.jobStore.isClustered", "false");

            prop.setProperty("org.quartz.dataSource.tasksDataStore.driver", "com.mysql.jdbc.Driver");
            prop.setProperty("org.quartz.dataSource.tasksDataStore.URL", "jdbc:mysql://localhost:3306/gsraisin?useUnicode=true&amp;characterEncoding=UTF-8");
            prop.setProperty("org.quartz.dataSource.tasksDataStore.user", "root");
            prop.setProperty("org.quartz.dataSource.tasksDataStore.password", "rootAdmin");
            prop.setProperty("org.quartz.dataSource.tasksDataStore.maxConnections", "20");

            // First we must get a reference to a scheduler

            SchedulerFactory sf = new StdSchedulerFactory(prop);
            Scheduler sched = sf.getScheduler();
//            List<String> triggerGroups = sched.getTriggerGroupNames();
//            for(String current:triggerGroups) {
//                Set<TriggerKey> triggers = sched.getTriggerKeys(GroupMatcher.triggerGroupEquals(current));
//                for(TriggerKey key:triggers){
//                    Trigger quartzTrigger = sched.getTrigger(key);
//                    System.out.println("Trigger Name : " +key.getName());
//                    System.out.println("Trigger Group : " +current);
//                    sched.rescheduleJob(key, quartzTrigger);
//                }
//            }
//            JobDetail jobDetail = sched.getJobDetail();
//            System.out.println(jobDetail.getDescription()+ "&&&"
//                               +jobDetail.isPersistJobDataAfterExecution()+
//                                  "&&"+jobDetail.getKey().getName());
            System.out.println("------- Initialization Complete --------");

            System.out.println("------- Scheduling Jobs ----------------");

            // jobs can be scheduled before sched.start() has been called

            // job 1 will run exactly at 12:55 daily
            JobDetail job1 = newJob(SimpleJob.class).withIdentity("job1", "group1").build();

            CronTrigger trigger1 = newTrigger().withIdentity("trigger1", "group1").withSchedule(cronSchedule("00 55 16 * * ?")).build();

            Date ft1 = sched.scheduleJob(job1, trigger1);
            System.out.println(sched.getSchedulerName());
            System.out.println(job1.getKey() + " has been scheduled to run at: " + ft1
                    + " and repeat based on expression: "
                    + trigger1.getCronExpression());
            
            JobDetail job2 = newJob(SimpleJob.class).withIdentity("job2", "group1").build();

            CronTrigger trigger2 = newTrigger().withIdentity("trigger2", "group1").withSchedule(cronSchedule("00 00 17 * * ?")).build();

            Date ft2 = sched.scheduleJob(job2, trigger2);
            System.out.println(sched.getSchedulerName());
            System.out.println(job2.getKey() + " has been scheduled to run at: " + ft2
                    + " and repeat based on expression: "
                    + trigger2.getCronExpression());
            
            System.out.println("------- Starting Scheduler ----------------");

            /*
             * All of the jobs have been added to the scheduler, but none of the
             * jobs will run until the scheduler has been started. If you have
             * multiple jobs performing multiple tasks, then its recommended to
             * write it in separate classes, like SimpleJob.class writes
             * organization members to file.
             */
            sched.start();

            System.out.println("------- Started Scheduler -----------------");

            System.out.println("------- Waiting five minutes... ------------");
            try {
                // wait five minutes to show jobs
                Thread.sleep(300L * 1000L);
                // executing...
            } catch (Exception e) {
            }

            System.out.println("------- Shutting Down ---------------------");

//            sched.shutdown(true);
//
//            System.out.println("------- Shutdown Complete -----------------");

            SchedulerMetaData metaData = sched.getMetaData();


            System.out.println("Executed " + metaData.getNumberOfJobsExecuted() + " jobs.");
        } catch (SchedulerException se) {
            System.out.println(se);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("THE APPLICATION STOPPED");
    }
}

