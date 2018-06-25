package com.smart.common.utils;

import com.smart.web.base.Loader;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/***
 * 功能实现:
 *
 * Author: Lzy
 * Date: 2018/6/22 15:27
 * Param:
 * Return:
 */
public class OozieUtils {
    //hadoop相关，hdfs-namenode，jobTracker：master:8032
    public static final String nameNode = Loader.pro.get("hadoop.nameNode");
    public static final String jobTracker = Loader.pro.get("hadoop.jobTracker");

    // spark相关   spark://master:7077，jobTracker
    public static final String sparkMaster = Loader.pro.get("spark.sparkMaster");
    public static final String mainClass = "com.sparkProcess.Main";
    //spark任务参数
    public static final String executorMemory = Loader.pro.get("spark.executorMemory");
    public static final String executorCores = Loader.pro.get("spark.executorCores");

    //oozie配置相关：wfPath-oozie的jar包，workflow等文件地址；workflowApp，当前oozie任务的名称；jars：jar包的名称;url：oozie的服务提交地址
    public static final String wfPath = nameNode + Loader.pro.get("oozie.wfPath");
    public static final String workflowApp = Loader.pro.get("oozie.workflowApp");
    public static final String jars = Loader.pro.get("oozie.jars");
    public static final String oozieUrl=Loader.pro.get("oozie.url");


    public static String run(Map<String, String> map) {
        final OozieClient oc = new OozieClient(oozieUrl);

        Properties conf = oc.createConfiguration();

        //提交任务的用户名称
        conf.setProperty("user.name", "username");
        conf.setProperty("nameNode", nameNode);
        conf.setProperty("jobTracker", jobTracker);
        conf.setProperty("queueName", "default");

        conf.setProperty("masterSpark", sparkMaster);
        conf.setProperty("mainClass", mainClass);
        conf.setProperty("executorMemory", executorMemory);
        conf.setProperty("executorCores", executorCores);

        conf.setProperty("oozie.wf.application.path", wfPath);
        conf.setProperty("workflowApp", workflowApp);
        conf.setProperty("jars", jars);
        conf.setProperty("oozie.use.system.libpath", "True");
        conf.setProperty("security_enabled", "False");
        conf.setProperty("dryrun", "False");

        //获取项目参数，这里示例从http中获取runType对应的参数，并将其写入conf中，最终传送到oozie的对应参数执行。
        final String sceneId = map.get("runType");
        conf.setProperty("arg", sceneId);

        //如果有必要，可以从这里讲mysql的信息进行填充。
//        conf.setProperty("mysqlurl", Loader.pro.get("jdbc.url"));
//        conf.setProperty("mysqlname", Loader.pro.get("jdbc.username"));
//        conf.setProperty("mysqlpsw", Loader.pro.get("jdbc.password"));

        //单独启动一条线程来监控任务的运行状态，并将结果写到制定的数据库中
        String jobId = null;
        try {

            jobId = oc.run(conf);
            System.out.println(jobId);
            final String finalJobId = jobId;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getJobStatus(oc, finalJobId);
                }
            }).start();
        } catch (OozieClientException e) {
            e.printStackTrace();
        }
        return jobId;
    }


    /***
     * 功能实现:获取oozie的指定任务的任务状态，RUNNING、Fail，success
     *
     * Author: Lzy
     * Date: 2018/6/22 15:30
     * Param: [oc, jobId]
     * Return: void
     */
    public static void getJobStatus(OozieClient oc, String jobId) {
        String jobStatus;
        try {
            jobStatus = oc.getStatus(jobId);
            while (jobStatus.equals("RUNNING")) {
                jobStatus = oc.getStatus(jobId);
                System.out.println(jobStatus);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            WorkflowJob jobInfo = oc.getJobInfo(jobId);
            Date startTime = jobInfo.getStartTime();
            Date endTime = jobInfo.getEndTime();
            insertJobStatus2Mysql(jobId, jobStatus, startTime, endTime);
        } catch (OozieClientException e) {
            e.printStackTrace();
        }


    }

    /***
     * 功能实现:将相关的oozie任务运行状态结果写入到mysql中
     *
     * Author: Lzy
     * Date: 2018/6/22 15:29
     * Param: [sceneId, jobId, jobStatus, startTime, endTime]
     * Return: java.lang.Boolean
     */
    public static Boolean insertJobStatus2Mysql(String jobId, String jobStatus, Date startTime, Date endTime) {
        Timestamp start = new Timestamp(startTime.getTime());
        Timestamp end = new Timestamp(endTime.getTime());
        Connection conn = JDBCUtils.getConn();
        Statement stmt = null;
        String sql = "insert into x_defined_job_status(JOBID,STATUS,STARTTIME,ENDTIME) values('" + jobId + "','" + jobStatus + "','" + start + "','" + end + "')";
        System.out.println(sql);
        Boolean isSuccess = false;
        try {
            stmt = conn.createStatement();
            isSuccess = !stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSuccess;
    }

    public static void main(String[] args) {

        OozieUtils.run(new HashMap<String, String>());
//        Loader.run();
//        Date start = new Date();
//        Date end = new Date();
//        System.out.println(insertJobStatus2Mysql("testId4", "Success", start, end));
    }
}
