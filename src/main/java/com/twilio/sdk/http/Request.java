package com.twilio.sdk.http;

import com.google.common.base.Joiner;
import com.google.common.collect.Range;
import com.twilio.sdk.clients.TwilioRestClient;
import com.twilio.sdk.exceptions.ApiException;
import com.twilio.sdk.exceptions.InvalidRequestException;
import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Request {

    public static final String QUERY_STRING_DATE_FORMAT = "yyyy-MM-dd";

    private final HttpMethod method;
    private final String uri;
    private final Map<String, List<String>> queryParams;
    private final Map<String, List<String>> postParams;

    private String username;
    private String password;

    public Request(final HttpMethod method, final String uri, final String accountSid) {
        this(method, TwilioRestClient.Domains.API, uri, accountSid);
    }

    public Request(final HttpMethod method, final TwilioRestClient.Domains domain,
                   final String uri, final String accountSid) {
        this.method = method;
        this.uri = "https://" + domain.toString() + ".twilio.com" + uri;
        this.queryParams = new HashMap<>();
        this.postParams = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public void setAuth(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Create auth string from username and password.
     *
     * @return basic authentication string
     */
    public String getAuthString() {
        String credentials = this.username + ":" + this.password;
        try {

            String encoded = new Base64().encodeAsString(credentials.getBytes("ascii"));
            return "Basic " + encoded;

        } catch (final UnsupportedEncodingException e) {

            throw new InvalidRequestException("It must be possible to encode credentials as ascii", credentials, e);

        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean requiresAuthentication() {
        return username != null || password != null;
    }

    /**
     * Build the URL for the request.
     *
     * @return URL for the request
     */
    @SuppressWarnings("checkstyle:abbreviationaswordinname")
    public URL constructURL() {
        String params = encodeQueryParams();
        String stringUri = uri;

        if (params.length() > 0) {
            stringUri += "?" + params;
        }

        try {
            URI uri = new URI(stringUri);
            return uri.toURL();
        } catch (final URISyntaxException e) {
            throw new ApiException("Bad URI: " + stringUri, e);
        } catch (final MalformedURLException e) {
            throw new ApiException("Bad URL: " + stringUri, e);
        }
    }

    /**
     * Add query parameters for date ranges.
     *
     * @param name name of query parameter
     * @param range date range
     */
    public void addQueryDateRange(final String name, final Range<DateTime> range) {
        if (range.hasLowerBound()) {
            String value = range.lowerEndpoint().toString(QUERY_STRING_DATE_FORMAT);
            addQueryParam(name + ">", value);
        }

        if (range.hasUpperBound()) {
            String value = range.upperEndpoint().toString(QUERY_STRING_DATE_FORMAT);
            addQueryParam(name + "<", value);
        }
    }

    /**
     * Add a query parameter.
     *
     * @param name name of parameter
     * @param value value of parameter
     */
    public void addQueryParam(final String name, final String value) {
        addParam(queryParams, name, value);
    }

    /**
     * Add a form parameter.
     *
     * @param name name of parameter
     * @param value value of parameter
     */
    public void addPostParam(final String name, final String value) {
        addParam(postParams, name, value);
    }

    private void addParam(final Map<String, List<String>> params, final String name, final String value) {
        if (!params.containsKey(name)) {
            params.put(name, new ArrayList<String>());
        }

        params.get(name).add(value);
    }

    /**
     * Encode the form body.
     *
     * @return url encoded form body
     */
    public String encodeFormBody() {
        return encodeParameters(postParams);
    }

    /**
     * Encode the query parameters.
     *
     * @return url encoded query parameters
     */
    public String encodeQueryParams() {
        return encodeParameters(queryParams);
    }

    private static String encodeParameters(final Map<String, List<String>> params) {
        List<String> parameters = new ArrayList<>();

        for (final Map.Entry<String, List<String>> entry : params.entrySet()) {
            try {
                String encodedName = URLEncoder.encode(entry.getKey(), "UTF-8");
                for (final String value : entry.getValue()) {
                    String encodedValue = URLEncoder.encode(value, "UTF-8");
                    parameters.add(encodedName + "=" + encodedValue);
                }
            } catch (final UnsupportedEncodingException e) {
                throw new InvalidRequestException("Couldn't encode params", entry.getKey(), e);
            }
        }
        return Joiner.on("&").join(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Request other = (Request) o;
        return Objects.equals(this.method, other.method) &&
               Objects.equals(this.uri, other.uri) &&
               Objects.equals(this.username, other.username) &&
               Objects.equals(this.password, other.password) &&
               Objects.equals(this.queryParams, other.queryParams) &&
               Objects.equals(this.postParams, other.postParams);
    }
}
