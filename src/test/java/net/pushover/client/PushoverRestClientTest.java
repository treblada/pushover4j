package net.pushover.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * 
 * @since Dec 19, 2012
 */
public class PushoverRestClientTest {

    private HttpClient httpClient;
    private PushoverRestClient client;
    private HttpResponse mockHttpResponse;

    @BeforeEach
    public void setUp() {
        httpClient = mock(HttpClient.class);
        mockHttpResponse = mock(HttpResponse.class);

        client = new PushoverRestClient();
        client.setHttpClient(httpClient);
    }

    @Test
    public void testPushMessageWithPostFailure() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException("nope!"));
        assertThrows(
                PushoverException.class,
                () -> client.pushMessage(PushoverMessage.builderWithApiToken("").build())
        );
    }

    @Test
    public void testPushMessageWithNonDefaultPriority() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.HIGH;
        String expectedMessage = "UTF-8 MSG (ue=ü, oe=ö, ae=ä)";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("TOKEN_CONTENT")
                .setUserId("USER_ID")
                .setMessage(expectedMessage)
                .setPriority(expectedPriority)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();
        assertTrue(
                postBody.contains("Content-Disposition: form-data; name=\"priority\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedPriority.getPriority()),
                postBody
        );

        assertTrue(
                postBody.contains("Content-Disposition: form-data; name=\"message\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedMessage),
                postBody
        );
    }
    
     @Test
    public void testPushMessageWithEmergencyPriority() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.EMERGENCY;
        final int requestedRetry = 120;
        final int requestedExpire = 7200;
        final String expectedReceipt = "asdfghjkl";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"receipt\":\""+expectedReceipt+"\"}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setPriority(expectedPriority)
                .setRetry(requestedRetry)
                .setExpire(requestedExpire)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(
                postBody.contains("Content-Disposition: form-data; name=\"priority\"\r\n" +
                 "Content-Type: text/plain; charset=UTF-8\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedPriority.getPriority()),
                postBody
        );
        assertTrue(
                postBody.contains("Content-Disposition: form-data; name=\"retry\"\r\n" +
                 "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + requestedRetry),
                postBody
        );
        assertTrue(
                postBody.contains("Content-Disposition: form-data; name=\"expire\"\r\n" +
                 "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                 "Content-Transfer-Encoding: 8bit\r\n\r\n" + requestedExpire),
                postBody
        );

    }

    @Test
    public void testPushMessageWithImage() throws Exception {

        final MessagePriority expectedPriority = MessagePriority.EMERGENCY;
        final String expectedReceipt = "asdfghjkl";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"receipt\":\""+expectedReceipt+"\"}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setPriority(expectedPriority)
                .setImage(new File("image/test_image.jpg"))
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(postBody.contains(
                "Content-Disposition: form-data; name=\"attachment\"; filename=\"test_image.jpg\"\r\n" +
                        "Content-Type: image/jpeg\r\n" +
                        "Content-Transfer-Encoding: binary")
        );
    }
    
    @Test
    public void testRequestVerification() throws Exception {

       final String expectedUser = "bnmUaSdfqwER";
        final String expectedDevice = "testPad";

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setMessage("")
                .setUserId(expectedUser)
                .setDevice(expectedDevice)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();

        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"user\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedUser));
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"device\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n" + expectedDevice));

    }
    
    @Test
    public void testRequestEmergencyReceipt() throws Exception {

       final String expectedToken = "qwerasdfzxcv";
       final String receipt = "atestdevice";
        
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.requestEmergencyReceipt(expectedToken, receipt);

        ArgumentCaptor<HttpGet> captor = ArgumentCaptor.forClass(HttpGet.class);

        verify(httpClient).execute(captor.capture());

        final String url = captor.getValue().getURI().toASCIIString();
        final String expectedUrl = PushoverRestClient.RECEIPT_CHECK_URL_FRAGMENT + receipt + ".json?token=" + expectedToken;
        assertEquals(expectedUrl, url);
        
    }

    @Test
    public void testCancelEmergencyMessage() throws Exception {

       final String expectedToken = "qwerasdfzxcv";
       final String receipt = "atestdevice";
        
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.cancelEmergencyMessage(expectedToken, receipt);

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final String url = captor.getValue().getURI().toASCIIString();
        final String expectedUrl = PushoverRestClient.RECEIPT_CHECK_URL_FRAGMENT + receipt + "/cancel.json";
        final HttpPost post = captor.getValue();
        final String postBody = EntityUtils.toString(post.getEntity());
        assertEquals(expectedUrl, url);
        assertTrue(postBody.contains("token=" + expectedToken));

    }
    
    @Test
    public void testGetSoundsWithFailure() throws Exception {
        when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(new IOException("nope!"));
        assertThrows(PushoverException.class, () -> client.getSounds());
    }

    @Test
    public void testGetSounds() throws Exception {

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        Set<PushOverSound> sounds = client.getSounds();
        assertNotNull(sounds);
        verify(httpClient).execute(any(HttpUriRequest.class));

        sounds = client.getSounds();
        assertNotNull(sounds);
        verifyNoMoreInteractions(httpClient);
    }

    @Test
    public void testPushMessageWithHTML() throws Exception {

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setHTML(true)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"html\"\r\n" +
                "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n1"));

    }

    @Test
    public void testPushMessageWithMonospace() throws Exception {

        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(new StringEntity("{\"status\":1}", "UTF-8"));

        client.pushMessage(PushoverMessage.builderWithApiToken("")
                .setUserId("")
                .setMessage("")
                .setMonospace(true)
                .build());

        ArgumentCaptor<HttpPost> captor = ArgumentCaptor.forClass(HttpPost.class);

        verify(httpClient).execute(captor.capture());

        final HttpPost post = captor.getValue();
        final HttpEntity entity = post.getEntity();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        entity.writeTo(bytes);

        final String postBody = bytes.toString();
        assertTrue(postBody.contains("Content-Disposition: form-data; name=\"monospace\"\r\n" +
                "Content-Type: text/plain; charset=ISO-8859-1\r\n" +
                "Content-Transfer-Encoding: 8bit\r\n\r\n1"));

    }
}
