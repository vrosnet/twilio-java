package com.twilio.sdk.fetchers.trunking.v1.trunk;

import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.exceptions.ApiConnectionException;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.fetchers.Fetcher;
import com.twilio.sdk.http.HttpMethod;
import com.twilio.sdk.http.Request;
import com.twilio.sdk.http.Response;
import com.twilio.sdk.resources.RestException;
import com.twilio.sdk.resources.trunking.v1.trunk.OriginationUrl;

public class OriginationUrlFetcher extends Fetcher<OriginationUrl> {
    private final String trunkSid;
    private final String sid;

    /**
     * Construct a new OriginationUrlFetcher.
     * 
     * @param trunkSid The trunk_sid
     * @param sid The sid
     */
    public OriginationUrlFetcher(final String trunkSid, 
                                 final String sid) {
        this.trunkSid = trunkSid;
        this.sid = sid;
    }

    /**
     * Make the request to the Twilio API to perform the fetch.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return Fetched OriginationUrl
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public OriginationUrl execute(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            TwilioRestClient.Domains.TRUNKING,
            "/v1/Trunks/" + this.trunkSid + "/OriginationUrls/" + this.sid + "",
            client.getAccountSid()
        );
        
        Response response = client.request(request);
        
        if (response == null) {
            throw new ApiConnectionException("OriginationUrl fetch failed: Unable to connect to server");
        } else if (response.getStatusCode() != TwilioRestClient.HTTP_STATUS_CODE_OK) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
        
            throw new ApiException(
                restException.getMessage(),
                restException.getCode(),
                restException.getMoreInfo(),
                restException.getStatus(),
                null
            );
        }
        
        return OriginationUrl.fromJson(response.getStream(), client.getObjectMapper());
    }
}