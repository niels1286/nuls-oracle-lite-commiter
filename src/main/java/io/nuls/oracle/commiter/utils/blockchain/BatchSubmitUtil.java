package io.nuls.oracle.commiter.utils.blockchain;

import io.nuls.base.basic.AddressTool;
import io.nuls.base.data.*;
import io.nuls.base.signture.P2PHKSignature;
import io.nuls.base.signture.TransactionSignature;
import io.nuls.core.constant.TxType;
import io.nuls.core.crypto.ECKey;
import io.nuls.core.crypto.HexUtil;
import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import io.nuls.oracle.commiter.model.CommitParam;
import io.nuls.oracle.commiter.utils.cfg.AppConfig;
import io.nuls.v2.txdata.CallContractData;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BatchSubmitUtil {

    private static final String SUBMIT(AppConfig appConfig, String[] keys, String[] values) throws IOException {

        if (StringUtils.isBlank(appConfig.getCommiterAccoutPrikey())) {
            return null;
        }
        ECKey ecKey = ECKey.fromPrivate(HexUtil.decode(appConfig.getCommiterAccoutPrikey()));
        if (null == ecKey) {
            return null;
        }
        String prefix = "NULS";
        if (appConfig.getChainId() == 2) {
            prefix = "tNULS";
        }
        byte[] fromAddress = AddressTool.getAddress(ecKey.getPubKey(), appConfig.getChainId());
        String address = AddressTool.getStringAddressByBytes(fromAddress, prefix);
        Transaction tx = new Transaction();
        tx.setType(TxType.CALL_CONTRACT);
        tx.setTime(System.currentTimeMillis() / 1000);
        tx.setRemark("NULS - Oracle Lite v0.1".getBytes(StandardCharsets.UTF_8));

        CallContractData txData = new CallContractData();
        txData.setSender(fromAddress);
        txData.setContractAddress(AddressTool.getAddress(appConfig.getOracleContract()));
        txData.setValue(BigInteger.ZERO);
        txData.setGasLimit(20000);
        txData.setPrice(25);
        txData.setMethodName("batchSubmit");
        txData.setMethodDesc("");
        txData.setArgsCount((short) 2);
        txData.setArgs(new String[][]{keys, values});
        tx.setTxData(txData.serialize());

        CoinData data = new CoinData();
        byte[] nulsNonce = NulsClient.getNonce(address, appConfig.getChainId(), 1);

        data.addFrom(new CoinFrom(fromAddress, appConfig.getChainId(), 1, BigInteger.valueOf(600000), nulsNonce, (byte) 0));
        tx.setCoinData(data.serialize());

        NulsHash hash = tx.getHash();
        TransactionSignature sig = new TransactionSignature();
        List<P2PHKSignature> plist = new ArrayList<>();
        P2PHKSignature sign = new P2PHKSignature();
        sign.setPublicKey(ecKey.getPubKey());
        byte[] signValue = ecKey.sign(hash.getBytes());
        NulsSignData signData = new NulsSignData();
        signData.setSignBytes(signValue);
        sign.setSignData(signData);
        plist.add(sign);
        sig.setP2PHKSignatures(plist);
        tx.setTransactionSignature(sig.serialize());

        NulsClient.broadcast(HexUtil.encode(tx.serialize()));

        return tx.getHash().toHex();
    }

    public static String SUBMIT(AppConfig appConfig, List<CommitParam> paramList) throws IOException {
        String[] keys = new String[paramList.size()];
        String[] values = new String[paramList.size()];
        for (int i = 0; i < paramList.size(); i++) {
            CommitParam param = paramList.get(i);
            keys[i] = param.getKey();
            values[i] = param.getValue();
            Log.info("{} : {}",param.getKey(),param.getValue());
        }
        return SUBMIT(appConfig, keys, values);
    }
}
