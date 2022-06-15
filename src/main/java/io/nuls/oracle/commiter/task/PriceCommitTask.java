package io.nuls.oracle.commiter.task;

import io.nuls.core.log.Log;
import io.nuls.oracle.commiter.model.AssetsSystemTokenInfoVo;
import io.nuls.oracle.commiter.model.CommitParam;
import io.nuls.oracle.commiter.utils.blockchain.BatchSubmitUtil;
import io.nuls.oracle.commiter.utils.cfg.AppConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PriceCommitTask implements Runnable {
    public static final BlockingQueue<List<AssetsSystemTokenInfoVo>> QUEUE = new LinkedBlockingQueue<>();
    private final AppConfig appConfig;

    public PriceCommitTask(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void run() {
        while (true) {
            try {
                List<AssetsSystemTokenInfoVo> list = QUEUE.take();
                List<CommitParam> paramList = new ArrayList<>();
                for (String asset : appConfig.getAssetList()) {
                    for (AssetsSystemTokenInfoVo vo : list) {
                        if (vo.tokenKey().equals(asset) || asset.equals(vo.getContractAddress())) {
                            paramList.add(new CommitParam(asset, vo.getPrice()));
                            break;
                        }
                    }
                }
                String hash = BatchSubmitUtil.SUBMIT(appConfig, paramList);
                if (null == hash) {
                    Log.warn("Submit failed！");
                } else {
                    Log.info("Submit success: {}", hash);
                }
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }
}
