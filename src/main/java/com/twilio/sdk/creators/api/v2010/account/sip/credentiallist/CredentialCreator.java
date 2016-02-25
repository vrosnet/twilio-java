package com.twilio.sdk.creators.api.v2010.account.sip.credentiallist;

import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.creators.Creator;
import com.twilio.sdk.exceptions.ApiConnectionException;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.http.HttpMethod;
import com.twilio.sdk.http.Request;
import com.twilio.sdk.http.Response;
import com.twilio.sdk.resources.RestException;
import com.twilio.sdk.resources.api.v2010.account.sip.credentiallist.Credential;

public class CredentialCreator extends Creator<Credential> {
    private final String accountSid;
    private final String credentialListSid;
    private final String username;
    private final String password;

    /**
     * Construct a new CredentialCreator.
     * 
     * @param accountSid The account_sid
     * @param credentialListSid The credential_list_sid
     * @param username The username
     * @param password The password
     */
    public CredentialCreator(final String accountSid, 
                             final String credentialListSid, 
                             final String username, 
                             final String password) {
        this.accountSid = accountSid;
        this.credentialListSid = credentialListSid;
        this.username = username;
        this.password = password;
    }

    /**
     * Make the request to the Twilio API to perform the create.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return Created Credential
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public Credential execute(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.POST,
            TwilioRestClient.Domains.API,
            "/2010-04-01/Accounts/" + this.accountSid + "/SIP/CredentialLists/" + this.credentialListSid + "/Credentials.json",
            client.getAccountSid()
        );
        
        addPostParams(request);
        Response response = client.request(request);
        
        if (response == null) {
            throw new ApiConnectionException("Credential creation failed: Unable to connect to server");
        } else if (response.getStatusCode() != TwilioRestClient.HTTP_STATUS_CODE_CREATED) {
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
        
        return Credential.fromJson(response.getStream(), client.getObjectMapper());
    }

    /**
     * Add the requested post parameters to the Request.
     * 
     * @param request Request to add post params to
     */
    private void addPostParams(final Request request) {
        if (username != null) {
            request.addPostParam("Username", username);
        }
        
        if (password != null) {
            request.addPostParam("Password", password);
        }
    }
}