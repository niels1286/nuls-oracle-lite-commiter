package io.nuls.oracle.commiter.col;

import io.nuls.core.log.Log;
import io.nuls.core.model.StringUtils;
import io.nuls.core.parse.JSONUtils;
import io.nuls.oracle.commiter.model.AssetsSystemTokenInfoVo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AssetsSystemProvider extends BasePriceProvider {

    private String baseUrl;

    @Override
    public BigDecimal queryPrice(String symbol) {
        String response = this.realHttpRequest(baseUrl + "/price/" + symbol);
        if (StringUtils.isBlank(response)) {
            return null;
        }
        return new BigDecimal(response);
    }

    public List<AssetsSystemTokenInfoVo> getAllTokenList() {
        String path = "/asset/list";
        String response = this.realHttpRequest(baseUrl + path);
        if (StringUtils.isBlank(response)) {
            return null;
        }
        List<AssetsSystemTokenInfoVo> list = null;
        try {
            list = JSONUtils.json2list(response, AssetsSystemTokenInfoVo.class);
        } catch (IOException e) {
            Log.error("", e);
        }
        return list;
    }

    public AssetsSystemProvider(String url) {
        baseUrl = url;
    }
}
