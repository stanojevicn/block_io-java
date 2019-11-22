package io.block;

import io.block.responses.BalanceInfo;
import io.block.responses.MyAddressesInfo;
import io.block.responses.NewAddressInfo;
import io.block.responses.TransactionsInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.block.responses.AddressBalanceInfo;
import io.block.responses.NetworkFeeEstimate;
import io.block.responses.WithdrawFromAddresses;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockIO {
    private static final Logger LOG = LoggerFactory.getLogger(BlockIO.class);
    protected String apiKey;

    public BlockIO(String apiKey) {
        this.apiKey = apiKey;
    }

    protected < T extends APIResponse> T apiCall(T apiResponse, Map< String, String> parameters) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://block.io/api/v2/" + apiResponse.getMethodName() + "/");

        if (parameters == null) {
            parameters = new HashMap< String, String>();
        }

        parameters.put("api_key", this.apiKey);

        URIBuilder uri = new URIBuilder(get.getURI());

        for (Entry< String, String> entry : parameters.entrySet()) {
            uri.addParameter(entry.getKey(), entry.getValue());
        }

        get.setURI(uri.build());

        LOG.trace("Executing request: {}", get.getURI());
        CloseableHttpResponse response = client.execute(get);
        int httpStatus = response.getStatusLine().getStatusCode();
        String responseText = EntityUtils.toString(response.getEntity());
        LOG.trace("Received response with status: {} and body: {}", httpStatus, responseText);

        if (httpStatus == 500) {
            throw new Exception("[API ERROR] HTTP Error " + httpStatus + " Message:" + responseText);
        }
        if (httpStatus == 404) {
            throw new Exception("[API ERROR] API Error " + httpStatus + " Message:" + responseText);
        }

        Gson gson = new Gson();
        try {
            apiResponse = (T) gson.fromJson(responseText, apiResponse.getClass());
        } catch (JsonSyntaxException e) {
            throw e;
        }

        response.close();

        return (T) apiResponse;
    }
    
    public AddressBalanceInfo getAddressBalance(List<String> addresses) throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("addresses", String.join(",", addresses));
        
        return apiCall(new AddressBalanceInfo(), parameters);
    }

    public BalanceInfo getBalance() throws Exception {
        return apiCall(new BalanceInfo(), null);
    }

    public MyAddressesInfo getMyAddresses() throws Exception {
        return apiCall(new MyAddressesInfo(), null);
    }

    public NewAddressInfo getNewAddress() throws Exception {
        return getNewAddress(null);
    }

    public NewAddressInfo getNewAddress(Map< String, String> parameters) throws Exception {
        return apiCall(new NewAddressInfo(), parameters);
    }

    public TransactionsInfo getTransactions() throws Exception {
        return getTransactions(null);
    }

    public TransactionsInfo getTransactions(Map< String, String> parameters) throws Exception {
        return apiCall(new TransactionsInfo(), parameters);
    }

    public NetworkFeeEstimate getNetworkFeeEstimate(Map<String, String> parameters) throws Exception {
        return apiCall(new NetworkFeeEstimate(), parameters);
    }
    
    public WithdrawFromAddresses withdrawFromAddress(Map<String, String> parameters) throws Exception {
        return apiCall(new WithdrawFromAddresses(), parameters);
    }
}
