package com.wentao.kettle.job;

import com.wentao.kettle.model.JobParam;
import com.wentao.kettle.pool.StandardThreadExecutor;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.cluster.TransSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务提交
 *
 * @author
 * @since 1.0
 */
@Component
public class JobSubmitter {

    public static final String VARIABLE_JOB_ID = "GLOBAL_JOB_ID";

    public static final String VARIABLE_JOB_HISTORY_ID = "GLOBAL_JOB_HISTORY_ID";

    private static final Logger LOG = LoggerFactory.getLogger(JobSubmitter.class);

    @Autowired
    private StandardThreadExecutor kettleJobExecutor;

//    @Autowired
//    private JobEntryListener logJobEntryListener;

//    @Autowired
//    private JobListener logJobListener;

//    @Autowired(required = true)
//    private TransListener transListener;

    @Resource()
    private TransExecutionConfiguration transExecutionConfiguration;

    /**
     * 提交作业
     *
     * @param param JobParam
     */
    public void submitJob(JobParam param) {
        kettleJobExecutor.execute(() -> {
            try {
                // 创建job
                JobMeta meta = new JobMeta(param.getPath(), null);
                Job job = new Job(null, meta);
                // 日志
                job.setLogLevel(LogLevel.ROWLEVEL);
                // 设置变量
                Map<String, String> variables = param.getVariables();
                for (Map.Entry<String, String> variable : variables.entrySet()) {
                    job.setVariable(variable.getKey(), variable.getValue());
                }
                // 全局job变量设置


                // 添加listener
//                job.addJobEntryListener(logJobEntryListener);
//                job.addJobListener(logJobListener);

                // 运行job;
//                Job.sendToSlaveServer(job,);
                // 等待job执行完毕
                job.waitUntilFinished();
                job.getResult();
            } catch (Exception e) {
                LOG.error("job submit unknown error", e);
            }
        });
    }



    /**
     * 集群提交转换
     *
     * @param param JobParam
     */
    public void submitTrans(JobParam param) {
        kettleJobExecutor.execute(() -> {
            try {
                // 创建job
                TransMeta transMeta = new TransMeta(param.getPath());
                Trans trans = new Trans(transMeta);
                // 日志
                trans.setLogLevel(LogLevel.ROWLEVEL);
                // 设置变量
                param.getVariables().forEach(trans::setVariable);
//                trans.addTransListener(transListener);
                // 集群执行
                TransSplitter transSplitter = Trans.executeClustered(transMeta, transExecutionConfiguration);
            } catch (Exception e) {
                LOG.error("job submit unknown error", e);
            }
        });
    }

    /**
     * 单机执行转换
     * @param hasMapParam
     * @param args
     * @param ktrPath
     */
    public  void standAlonerunTransfer(Map<String,String> hasMapParam,String[] args, String ktrPath) {
        try {/*
            List<PluginFolderInterface> pluginFolders = StepPluginType.getInstance().getPluginFolders();
            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\system\\karaf\\system\\pentaho\\pentaho-mongodb-plugin",false,true));
            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\plugins\\kettle-json-plugin",false,true));
            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\system\\karaf\\system\\org\\pentaho\\di\\plugins\\pentaho-streaming-jms-plugin",false,true));
*/
//            KettleEnvironment.init();// 初始化
//            EnvUtil.environmentInit();
            TransMeta transMeta = new TransMeta(ktrPath);
            Trans trans = new Trans(transMeta);
            hasMapParam.forEach(trans::setVariable);
            trans.execute(args);

            trans.waitUntilFinished();

            Result result = trans.getResult();
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否在执行
     *
     * @return 执行返回true
     */
    public boolean isActive() {
        return kettleJobExecutor.getSubmittedTasksCount() > 0;
    }

    public static void main(String[] args) {

        String[] stringArgs = new String[2];
        Map<String,String> hashMapParam= new HashMap<>(4);
//        hashMapParam.put("id", "1");
//        hashMapParam.put("names", "小毛驴");
//        hashMapParam.put("phone", "119");
//        hashMapParam.put("filter", "GXSJ>'2019-05-10 00:00:00'");
        hashMapParam.put("applicationResourceQuery_Queues", "applicationResourceQuery");
       new JobSubmitter().standAlonerunTransfer(hashMapParam,stringArgs, "D:\\test\\test.ktr");
    }
}
