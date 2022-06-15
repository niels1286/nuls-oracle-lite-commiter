package io.nuls.oracle.commiter.model;

public class AssetsSystemTokenInfoVo {

    /**
     * 名称
     */
    private String name;

    /**
     * 资产简称
     */
    private String symbol;

    /**
     * 资产小数位数
     */
    private Long decimals;

    /**
     * 资产链ID
     */
    private Long assetChainId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 合约地址
     */
    private String contractAddress;

    private String imageUrl;

    private String crossChainIds;

    private String crossInfo;

    private String price;
    private Long evmChainId;
    private Long sourceChainId;
    private boolean nulsCross;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getDecimals() {
        return decimals;
    }

    public void setDecimals(Long decimals) {
        this.decimals = decimals;
    }

    public Long getAssetChainId() {
        return assetChainId;
    }

    public void setAssetChainId(Long assetChainId) {
        this.assetChainId = assetChainId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public void setAssetId(Long assetId) {
        this.assetId = assetId;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCrossChainIds() {
        return crossChainIds;
    }

    public void setCrossChainIds(String crossChainIds) {
        this.crossChainIds = crossChainIds;
    }

    public String getCrossInfo() {
        return crossInfo;
    }

    public void setCrossInfo(String crossInfo) {
        this.crossInfo = crossInfo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Long getEvmChainId() {
        return evmChainId;
    }

    public void setEvmChainId(Long evmChainId) {
        this.evmChainId = evmChainId;
    }

    public Long getSourceChainId() {
        return sourceChainId;
    }

    public void setSourceChainId(Long sourceChainId) {
        this.sourceChainId = sourceChainId;
    }

    public boolean isNulsCross() {
        return nulsCross;
    }

    public void setNulsCross(boolean nulsCross) {
        this.nulsCross = nulsCross;
    }

    public String tokenKey() {
        return assetChainId + "-" + assetId;
    }
}
