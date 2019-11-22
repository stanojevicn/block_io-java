package io.block.responses;

import java.math.BigDecimal;

import io.block.APIResponse;

public class NetworkFeeEstimate extends APIResponse {

    @Override
    public String getMethodName() {
        return "get_network_fee_estimate";
    }

    public BigDecimal getEstimatedNetworkFee() {
        return _getNumber("estimated_network_fee");
    }

}
