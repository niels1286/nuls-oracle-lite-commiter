package io.nuls.oracle.commiter.utils.cfg;

import java.util.List;
import java.util.Map;

public class AppConfig {

    private int chainId;
    private int intervalMinite;
    private String oracleContract;
    private String nulsApiUrl;
    private String commiterAccoutPrikey;
    private String assetSystemUrl;
    /**
     * 进行喂价的资产id或者NULS合约地址
     */
    private List<String> assetList;

    public AppConfig(Map<String, Object> jsonMap) {
        this.chainId = Integer.parseInt(""+jsonMap.get("chainId"));
        this.intervalMinite = Integer.parseInt("" + jsonMap.get("intervalMinite"));
        this.oracleContract = (String) jsonMap.get("oracleContract");
        this.nulsApiUrl = (String) jsonMap.get("nulsApiUrl");
        this.commiterAccoutPrikey = (String) jsonMap.get("commiterAccoutPrikey");
        this.assetSystemUrl = (String) jsonMap.get("assetSystemUrl");
        this.assetList = (List<String>) jsonMap.get("assets");
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getIntervalMinite() {
        return intervalMinite;
    }

    public void setIntervalMinite(int intervalMinite) {
        this.intervalMinite = intervalMinite;
    }

    public String getOracleContract() {
        return oracleContract;
    }

    public void setOracleContract(String oracleContract) {
        this.oracleContract = oracleContract;
    }

    public String getNulsApiUrl() {
        return nulsApiUrl;
    }

    public void setNulsApiUrl(String nulsApiUrl) {
        this.nulsApiUrl = nulsApiUrl;
    }

    public String getCommiterAccoutPrikey() {
        return commiterAccoutPrikey;
    }

    public void setCommiterAccoutPrikey(String commiterAccoutPrikey) {
        this.commiterAccoutPrikey = commiterAccoutPrikey;
    }

    public String getAssetSystemUrl() {
        return assetSystemUrl;
    }

    public void setAssetSystemUrl(String assetSystemUrl) {
        this.assetSystemUrl = assetSystemUrl;
    }

    public List<String> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<String> assetList) {
        this.assetList = assetList;
    }
}
