package io.nuls.oracle.commiter.utils.blockchain;

import io.nuls.core.basic.Result;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.log.Log;
import io.nuls.oracle.commiter.utils.common.NulsJsonRpcHttpClient;
import io.nuls.v2.NulsSDKBootStrap;
import io.nuls.v2.util.NulsSDKTool;

import java.util.Map;

/**
 * @author Niels
 */
public class NulsClient {
    private static NulsJsonRpcHttpClient nulsApiClient;
    private static int chainId;
    private static ECKey ecKey = new ECKey();

    public static void init(int chainId, String nulsApiUrl) {
        NulsClient.chainId = chainId;
        nulsApiClient = NulsJsonRpcHttpClient.getInstance(nulsApiUrl + "/jsonrpc");
        String prefix = "NULS";
        if (chainId == 2) {
            prefix = "tNULS";
        }
        NulsSDKBootStrap.init(chainId, prefix, nulsApiUrl);
    }

    public static boolean broadcast(String txHex) {
        Result result = NulsSDKTool.broadcast(txHex);
        if (!result.isSuccess()) {
            Log.error(result.toString());
        }
        return result.isSuccess();
    }

    public static byte[] getNonce(String address) {
        return getNonce(address, chainId, 1);
    }

    public static byte[] getNonce(String address, int chainId, int assetId) {
        Result result = NulsSDKTool.getAccountBalance(address, chainId, assetId);
        if (!result.isSuccess()) {
            throw new RuntimeException("获取nonce失败");
        }
        Map<String, Object> map = (Map<String, Object>) result.getData();
        String nonce = (String) map.get("nonce");
        return HexUtil.decode(nonce);
    }

}
