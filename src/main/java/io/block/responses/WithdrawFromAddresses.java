package io.block.responses;

import io.block.APIResponse;

public class WithdrawFromAddresses extends APIResponse {

    @Override
    public String getMethodName() {
        return "withdraw_from_addresses";
    }

    public String getReferenceId() {
        return _getString("reference_id");
    }

    public boolean getMoreSignaturesNeeded() {
        return _getBoolean("more_signatures_needed");
    }

}
