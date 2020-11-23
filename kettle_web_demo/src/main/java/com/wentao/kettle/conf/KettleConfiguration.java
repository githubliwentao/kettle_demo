package com.wentao.kettle.conf;


import com.wentao.kettle.pool.DefaultThreadFactory;
import com.wentao.kettle.pool.StandardThreadExecutor;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.PluginFolderInterface;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * kettle配置
 *
 * @author
 * @since 1.0
 */
@Configuration
@EnableConfigurationProperties(KettleJobPoolProperties.class)
public class KettleConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(KettleConfiguration.class);

    /**
     * 初始化kettle运行环境
     */
    @PostConstruct
    public void setup() {
        try {
            List<PluginFolderInterface> pluginFolders = StepPluginType.getInstance().getPluginFolders();
//            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\system\\karaf\\system\\pentaho\\pentaho-mongodb-plugin",false,true));
            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\plugins\\kettle-json-plugin",false,true));
            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\system\\karaf\\system\\org\\pentaho\\di\\plugins\\pentaho-streaming-jms-plugin",false,true));
//            pluginFolders.add(new PluginFolder("E:\\install\\pdi-ce-9.1.0.0-324\\data-integration\\system\\karaf\\system\\pentaho\\pentaho-dataservice-client",false,true));

            KettleEnvironment.init();
        } catch (KettleException e) {
            LOG.error("init kettle env error", e);
            System.exit(-1);
        }
    }

    /**
     * 销毁kettle运行环境
     */
    @PreDestroy
    public void cleanup() {
        KettleEnvironment.shutdown();
    }

    /**
     * 配置kettle任务运行线程池
     *
     * @param properties KettleJobPoolProperties
     * @return StandardThreadExecutor
     */
    @Bean
    public StandardThreadExecutor kettleJobExecutor(KettleJobPoolProperties properties) {
        return new StandardThreadExecutor(
                properties.getCoreThreads(), properties.getMaxThreads(),
                properties.getKeepAliveTimeMin(), TimeUnit.MINUTES,
                properties.getQueueCapacity(), new DefaultThreadFactory(properties.getNamePrefix()),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
    @Bean
    public TransExecutionConfiguration transExecutionConfiguration(KettleJobPoolProperties properties) {
        // 注册服务
        TransExecutionConfiguration config = new TransExecutionConfiguration();
        //设置集群为true
        config.setExecutingClustered(true);
        //设置local为false
        config.setExecutingLocally(false);
        config.setExecutingRemotely(false);
        config.setClusterPosting(true);
        //设置准备执行为true
        config.setClusterPreparing(true);
        //设置开始执行为true，否则需要到carte的监控页面上点击开始执行
        config.setClusterStarting(true);
        return config;
    }

}
