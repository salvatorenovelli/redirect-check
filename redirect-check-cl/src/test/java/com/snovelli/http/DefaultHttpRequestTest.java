package com.snovelli.http;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DefaultHttpRequestTest {

    @Test
    public void testExecute() throws Exception {

        //TODO: check on localhost
        DefaultHttpRequest sut = new DefaultHttpRequest(new URI("https://www.google.it"), HttpMethod.GET);
        HttpResponse response = sut.execute();
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}