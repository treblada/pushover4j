package net.pushover.client;

import java.util.Set;

/**
 * Pushover.net client interface.
 * 
 * @author <a href="mailto:sean.scanlon@gmail.com">Sean Scanlon</a>
 * 
 * @since Dec 18, 2012
 */
public interface PushoverClient {

    /**
     * Push a message to the service
     * 
     * @param msg The desired message
     * @return a {@link Status}
     * @throws PushoverException based on the results of the APIs
     */
    Status pushMessage(PushoverMessage msg) throws PushoverException;

    /**
     * Retrieve a list of available sounds from the service
     * 
     * @return a set of {@link PushOverSound}
     * @throws PushoverException based on the results of the APIs
     */
    Set<PushOverSound> getSounds() throws PushoverException;
}
