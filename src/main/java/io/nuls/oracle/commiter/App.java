package io.nuls.oracle.commiter;

import io.nuls.core.log.Log;
import io.nuls.oracle.commiter.task.PriceCollectTask;
import io.nuls.oracle.commiter.task.PriceCommitTask;
import io.nuls.oracle.commiter.utils.blockchain.NulsClient;
import io.nuls.oracle.commiter.utils.cfg.AppConfig;
import io.nuls.oracle.commiter.utils.cfg.ConfigLoader;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class App {
    public static AppConfig appConfig;

    /**
     * 系统启动
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Map<String, Object> jsonMap = ConfigLoader.load("config.json");
        appConfig = new AppConfig(jsonMap);
        System.out.println();
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
        pool.scheduleWithFixedDelay(new PriceCollectTask(appConfig), 0, appConfig.getIntervalMinite(), TimeUnit.MINUTES);
        NulsClient.init(appConfig.getChainId(),appConfig.getNulsApiUrl());
        PriceCommitTask task = new PriceCommitTask(appConfig);
        new Thread(task).start();
        Log.info("System start ....");
    }
}
