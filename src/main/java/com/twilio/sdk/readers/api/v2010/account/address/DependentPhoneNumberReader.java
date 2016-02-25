package com.twilio.sdk.readers.api.v2010.account.address;

import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.exceptions.ApiConnectionException;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.http.HttpMethod;
import com.twilio.sdk.http.Request;
import com.twilio.sdk.http.Response;
import com.twilio.sdk.readers.Reader;
import com.twilio.sdk.resources.Page;
import com.twilio.sdk.resources.ResourceSet;
import com.twilio.sdk.resources.RestException;
import com.twilio.sdk.resources.api.v2010.account.address.DependentPhoneNumber;

public class DependentPhoneNumberReader extends Reader<DependentPhoneNumber> {
    private final String accountSid;
    private final String addressSid;

    /**
     * Construct a new DependentPhoneNumberReader.
     * 
     * @param accountSid The account_sid
     * @param addressSid The address_sid
     */
    public DependentPhoneNumberReader(final String accountSid, 
                                      final String addressSid) {
        this.accountSid = accountSid;
        this.addressSid = addressSid;
    }

    /**
     * Make the request to the Twilio API to perform the read.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return DependentPhoneNumber ResourceSet
     */
    @Override
    public ResourceSet<DependentPhoneNumber> execute(final TwilioRestClient client) {
        return new ResourceSet<>(this, client, firstPage());
    }

    /**
     * Make the request to the Twilio API to perform the read.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return DependentPhoneNumber ResourceSet
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public Page<DependentPhoneNumber> firstPage(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            TwilioRestClient.Domains.API,
            "/2010-04-01/Accounts/" + this.accountSid + "/Addresses/" + this.addressSid + "/DependentPhoneNumbers.json",
            client.getAccountSid()
        );
        
        addQueryParams(request);
        return pageForRequest(client, request);
    }

    /**
     * Retrieve the next page from the Twilio API.
     * 
     * @param page current page
     * @param client TwilioRestClient with which to make the request
     * @return Next Page
     */
    @Override
    public Page<DependentPhoneNumber> nextPage(final Page<DependentPhoneNumber> page, 
                                               final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.GET,
            page.getNextPageUri(),
            client.getAccountSid()
        );
        return pageForRequest(client, request);
    }

    /**
     * Generate a Page of DependentPhoneNumber Resources for a given request.
     * 
     * @param client TwilioRestClient with which to make the request
     * @param request Request to generate a page for
     * @return Page for the Request
     */
    private Page<DependentPhoneNumber> pageForRequest(final TwilioRestClient client, final Request request) {
        Response response = client.request(request);
        
        if (response == null) {
            throw new ApiConnectionException("DependentPhoneNumber read failed: Unable to connect to server");
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
        
        return Page.fromJson(
            "dependent_phone_numbers",
            response.getContent(),
            DependentPhoneNumber.class,
            client.getObjectMapper()
        );
    }

    /**
     * Add the requested query string arguments to the Request.
     * 
     * @param request Request to add query string arguments to
     */
    private void addQueryParams(final Request request) {
        request.addQueryParam("PageSize", Integer.toString(getPageSize()));
    }
}