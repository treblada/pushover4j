package net.pushover.client;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class PushoverMessageTest {

    @Test
    public void test() {
        final PushoverMessage message = PushoverMessage.builderWithApiToken(null)
                .setDevice(null)
                .setMessage(null)
                .setPriority(null)
                .setSound(null)
                .setTimestamp(null)
                .setTitle(null)
                .setTitleForURL(null)
                .setUrl(null)
                .setUserId(null)
                .build();
        assertNull(message.getApiToken());
        assertNull(message.getDevice());
        assertNull(message.getMessage());
        assertNull(message.getSound());
        assertNull(message.getPriority());
        assertNull(message.getTimestamp());
        assertNull(message.getTitle());
        assertNull(message.getTitleForURL());
        assertNull(message.getUrl());
        assertNull(message.getUserId());
    }

}
