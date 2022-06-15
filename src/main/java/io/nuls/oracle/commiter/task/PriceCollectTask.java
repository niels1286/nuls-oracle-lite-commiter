package io.nuls.oracle.commiter.task;

import io.nuls.core.log.Log;
import io.nuls.oracle.commiter.col.AssetsSystemProvider;
import io.nuls.oracle.commiter.model.AssetsSystemTokenInfoVo;
import io.nuls.oracle.commiter.utils.cfg.AppConfig;

import java.util.List;

public class PriceCollectTask implements Runnable {

    private AssetsSystemProvider priceProvider;

    public PriceCollectTask(AppConfig appConfig) {
        priceProvider = new AssetsSystemProvider(appConfig.getAssetSystemUrl());
    }


    @Override
    public void run() {
        Log.info("Start collet prices.");
        List<AssetsSystemTokenInfoVo> list = priceProvider.getAllTokenList();
        PriceCommitTask.QUEUE.offer(list);
        Log.info("Offered Submiter.");
    }
}
