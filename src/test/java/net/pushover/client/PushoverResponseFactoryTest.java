package net.pushover.client;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PushoverResponseFactoryTest {

    private HttpResponse response;

    @BeforeEach
    public void setUp() {
        response = mock(HttpResponse.class);
    }

    @Test
    public void testNullStausResponse() {
        assertThrows(IOException.class, () -> PushoverResponseFactory.createStatus(null));
    }

    @Test
    public void testNullEntityStatusResponse() {
        assertThrows(IOException.class, () -> PushoverResponseFactory.createStatus(response));
    }

    @Test
    public void testMalformedStatusResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{"));
        assertThrows(IOException.class, () -> PushoverResponseFactory.createStatus(response));
    }
    
    @Test
    public void testEmptyStatus() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{}"));
        Status status = PushoverResponseFactory.createStatus(response);
        assertNotNull(status);
    }

    @Test
    public void testOKStatus() throws IOException {
        final String expectedRequestId = "1234";

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\"}"));

        final Status status = PushoverResponseFactory.createStatus(response);
        
        assertNotNull(status);
        assertEquals(status.getStatus(), 1);
        assertEquals(status.getRequestId(), expectedRequestId);
    }
    
    @Test
    public void testOKResponse() throws IOException {
        final String expectedRequestId = "1234";
        final int expectedRemaining = 4321;

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\"}"));

        when(response.getFirstHeader(PushoverResponseFactory.REQUEST_REMAINING_HEADER)).thenReturn(new BasicHeader(PushoverResponseFactory.REQUEST_REMAINING_HEADER,
                String.valueOf(expectedRemaining)));

        final Response status = PushoverResponseFactory.createResponse(response);
        assertNotNull(status);
        assertEquals(status.getStatus(), 1);
        assertEquals(status.getRequest(), expectedRequestId);
        assertEquals(status.getRemaining(), expectedRemaining);
    }
    
    @Test
    public void testOKEmergencyResponse() throws IOException {
        final String expectedRequestId = "1234";
        final String expectedReceipt = "qwertyuiop";

        when(response.getEntity()).thenReturn(new StringEntity("{\"status\":1, \"request\":\"" + expectedRequestId +"\",\"receipt\":\"" + expectedReceipt +"\"}"));

        final Response resp = PushoverResponseFactory.createResponse(response);
        assertNotNull(resp);
        assertEquals(resp.getStatus(), 1);
        assertEquals(resp.getRequest(), expectedRequestId);
        assertEquals(resp.getReceipt(), expectedReceipt);
    }

    @Test
    public void testOKVerification() throws IOException {
        final String expectedRequestId = "1234";
        final List<String> expectedDevices = Arrays.asList("abc", "efg", "123");

        when(response.getEntity()).thenReturn(new StringEntity(
                "{\"status\":1, \"request\":\"" + expectedRequestId + "\",\"devices\": " + expectedDevices + "}"
        ));

        final Response status = PushoverResponseFactory.createResponse(response);
        assertNotNull(status);
        assertEquals(status.getStatus(), 1);
        assertEquals(status.getRequest(), expectedRequestId);
        assertEquals(status.getDevices(), expectedDevices);
    }
    
    @Test
    public void testFailedVerification() throws IOException {
        final String expectedRequestId = "1234";
        final List<String> expectedDevices = Collections.singletonList("requesteddevice");
        final List<String> reportedErrors = Collections.singletonList("User not found");

        when(response.getEntity()).thenReturn(new StringEntity(
                "{\"status\":0, \"request\":\"" + expectedRequestId +"\",\"devices\": "+
                        expectedDevices + ", \"errors\":[\"User not found\"]}"
        ));

        final Response status = PushoverResponseFactory.createResponse(response);
        assertNotNull(status);
        assertEquals(status.getStatus(), 0);
        assertEquals(status.getRequest(), expectedRequestId);
        assertEquals(status.getDevices(), expectedDevices);
        assertEquals(status.getErrors(), reportedErrors);
    }
    
    @Test
    public void testOkReceipt() throws IOException {
        final long acknowledgedAt = 12345678;
        final String acknowledgedBy = "user1";
        final long lastDeliveredAt = 12345670;
        final int expired = 0;            
        final long expiresAt = 13000000;
        final int calledBack = 0;
        final long calledBackAt = 0;
        final String request = "lkjhpoiumn";

        when(response.getEntity()).thenReturn(new StringEntity(
                  new StringBuilder().append("{\"status\":1, \"acknowledged\":1")
                              .append(",\"acknowledged_at\":").append(acknowledgedAt)
                              .append(",\"acknowledged_by\":").append(acknowledgedBy)
                              .append(",\"last_delivered_at\":").append(lastDeliveredAt)
                              .append(",\"expired\":").append(expired)
                              .append(",\"expires_at\":").append(expiresAt)
                              .append(",\"called_back\":").append(calledBack)
                              .append(",\"called_back_at\":").append(calledBackAt)
                              .append(",\"request\":").append(request)
                              .append("}").toString()
            ));

        final Receipt rcpt = PushoverResponseFactory.createReceipt(response);
        assertNotNull(rcpt);
        assertEquals(rcpt.getStatus(), 1);
        assertEquals(rcpt.getRequest(), request);
        assertEquals(rcpt.getAcknowledged(), 1);
        assertEquals(rcpt.getAcknowledgedAt(), acknowledgedAt);
        assertEquals(rcpt.getAcknowledgedBy(), acknowledgedBy);
        assertEquals(rcpt.getCalledBack(), calledBack);
        assertEquals(rcpt.getCalledBackAt(), calledBackAt);
        assertEquals(rcpt.getExpired(), expired);
        assertEquals(rcpt.getExpiresAt(), expiresAt);
        assertEquals(rcpt.getLastDeliveredAt(), lastDeliveredAt);
    }
    
    @Test
    public void testFailedReceipt() throws IOException {
        final List<String> reportedErrors = Collections.singletonList("User not found");

        when(response.getEntity()).thenReturn(new StringEntity(
                  new StringBuilder().append("{\"status\":0")
                          .append(",\"errors\":[\"User not found\"]}")
                          .toString()
            ));

        final Receipt rcpt = PushoverResponseFactory.createReceipt(response);
        assertNotNull(rcpt);
        assertEquals(rcpt.getStatus(), 0);
        assertEquals(rcpt.getErrors(), reportedErrors);
    }
    
    @Test
    public void testCreateSoundResponse() throws IOException {

        when(response.getEntity()).thenReturn(new StringEntity("{\"sounds\":{\"id\":\"name\"},\"status\":1}"));

        final Set<PushOverSound> sounds = PushoverResponseFactory.createSoundSet(response);
        assertNotNull(sounds);
        assertFalse(sounds.isEmpty());
        PushOverSound sound = sounds.iterator().next(); 
        assertEquals(sound.id(), "id");
        assertEquals(sound.name(), "name");
    }

    @Test
    public void testMalformedCreateSoundResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{"));
        assertThrows(IOException.class, () -> PushoverResponseFactory.createSoundSet(response));
    }

    @Test
    public void testEmptyCreateSoundResponse() throws IOException {
        when(response.getEntity()).thenReturn(new StringEntity("{}"));
        final Set<PushOverSound> sounds = PushoverResponseFactory.createSoundSet(response);
        assertNotNull(sounds);
        assertTrue(sounds.isEmpty());
    }
    
    @Test
    public void testNullCreateSoundResponse() {
        assertThrows(IOException.class, () -> PushoverResponseFactory.createSoundSet(null));
    }

    @Test
    public void testNullEntityCreateSoundResponse() {
        assertThrows(IOException.class, () -> PushoverResponseFactory.createSoundSet(response));
    }
}
