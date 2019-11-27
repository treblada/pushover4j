package net.pushover.client;

import java.io.File;

/**
 * 
 * @author <a href="mailto:sean.scanlon@gmail.com">Sean Scanlon</a>
 * 
 * @since Dec 18, 2012
 */
public class PushoverMessage {

    private String apiToken;        //API key for your application
 
    private String userId;          //user's/group key ideitifing the recepient(s)

    private String message;         //Body of the message to send to the recepients

    private String device;          //target a specific users device 

    private String title;           //Display title (similar to email subject)

    private String url;             //supplemental url to be appended to the message

    private String titleForURL;     //name for the URL rather than display the http(s) link

    private MessagePriority priority = MessagePriority.NORMAL;    //priority of the message range from -2 to 2, see MessagePriority class

    private Long timestamp;         //unix timestamp that will set a time in the message
    
    private String sound;           //sound to play on the users device. Overrides their default sound. List of sounds can be requested via api

    private int retry;              //required for emergency (2) priority messages only, specifies how frequently to retry the message.  No less than 30 seconds
    
    private int expire;             //how long until the pushover system stops trying to send to the user(s). System uses smaller of specified or 86440 seconds (24 hours) 
    
    private String emergencyCallbackUrl; //a publicly accessable webpage on your server to handle the acknoldegements of the emergency priority message.

    private File image; // As of version 3.0 of our iOS, Android, and Desktop apps, Pushover messages can include an image.

    private boolean html = false; // As of version 2.3 of our device clients, messages can be formatted with HTML tags

    private boolean monospace = false; // As of version 3.4, messages can be formatted with a monospace font.

    private PushoverMessage() {
        // use the builder
    }

    @SuppressWarnings("WeakerAccess")
    public static Builder builderWithApiToken(String token) {
        return new Builder().setApiToken(token);
    }

    @SuppressWarnings("WeakerAccess")
    public static class Builder {

        private PushoverMessage msg;

        public Builder() {
            msg = new PushoverMessage();
        }

        public PushoverMessage build() {
            // TODO: validate message!
            return msg;
        }

        /**
         * @param apiToken (required) - your application's API token
         * @return the current Builder instance
         */
        public Builder setApiToken(String apiToken) {
            msg.apiToken = apiToken;
            return this;
        }

        /**
         * @param userId  (required) - the user/group key (not e-mail address) of your user or group.
         * Viewable when logged into the dashboard
         * @return the current Builder instance
         */
        public Builder setUserId(String userId) {
            msg.userId = userId;
            return this;
        }

        /**
         * @param message  (required on push notifications, optional others) - your message
         * @return the current Builder instance
         */
        public Builder setMessage(String message) {
            msg.message = message;
            return this;
        }

        /**
         * @param device  (optional) - your user's device identifier to send the message directly to that device,
         * rather than all of the user's devices
         * @return the current Builder instance
         */
        public Builder setDevice(String device) {
            msg.device = device;
            return this;
        }

        /**
         * @param title  (optional) - your message's title, otherwise uses your app's name
         * @return the current Builder instance
         */
        public Builder setTitle(String title) {
            msg.title = title;
            return this;
        }

        /**
         * @param url  (optional) - a supplementary URL to show with your message
         * @return the current Builder instance
         */
        public Builder setUrl(String url) {
            msg.url = url;
            return this;
        }

        /**
         * @param titleForURL  (optional) - a title for your supplementary URL
         * @return the current Builder instance
         */
        public Builder setTitleForURL(String titleForURL) {
            msg.titleForURL = titleForURL;
            return this;
        }

        /**
         * @param priority  (optional) - set to MessagePriority.HIGH to display as high-priority and bypass quiet
         * hours, or MessagePriority.QUIET to always send as a quiet notification
         * @return the current Builder instance
         */
        public Builder setPriority(MessagePriority priority) {
            msg.priority = priority;
            return this;
        }

        /**
         * @param timestamp  (optional) - set to a Unix timestamp to have your message show with a particular time,
         * rather than now
         * @return the current Builder instance
         */
        public Builder setTimestamp(Long timestamp) {
            msg.timestamp = timestamp;
            return this;
        }

        /**
         * @param sound  (optional) - set to the name of one of the sounds supported by device clients to override
         * the user's default sound choice
         * @return the current Builder instance
         */
        public Builder setSound(String sound) {
            msg.sound = sound;
            return this;
        }
        
        /**
         * @param seconds  (required when priority is emergency) - how often to retry alerting
         * the user until acknowledged. No less than 30 seconds
         * @return the current Builder instance
         */
        public Builder setRetry(int seconds) {
            msg.retry = seconds;
            return this;
        }
        
        /**
         * @param seconds  (required when priority is emergency) - how long to keep retrying the
         * user unless they acknowledge. System limits to 86400 (24 hours)
         * @return the current Builder instance
         */
        public Builder setExpire(int seconds) {
            msg.expire = seconds;
            return this;
        }
        
        /**
         * @param url  (optional) - a publicly accessable URL that the system will post to
         * when the user acknowledges the alert.
         * @return the current Builder instance
         */
        public Builder setCallbackUrl(String url) {
            msg.emergencyCallbackUrl = url;
            return this;
        }

        /**
         * @param image As of version 3.0 of our iOS, Android, and
         * Desktop apps, Pushover messages can include an image.
         * @deprecated use setImage instead
         * @return the current Builder instance
         */
        @Deprecated
        public Builder setAttachment(File image) {
            return setImage(image);
        }

        /**
         * @param image As of version 3.0 of our iOS, Android, and
         *              Desktop apps, Pushover messages can include an image.
         * @return the current Builder instance
         */
        public Builder setImage(File image) {
            msg.image = image;
            return this;
        }

        /**
         * @param isHTML As of version 2.3 of our device clients, messages
         *               can be formatted with HTML tags.
         * @return the current Builder instance
         */
        public Builder setHTML(boolean isHTML) {
            msg.html = isHTML;
            return this;
        }

        /**
         * @param isMonospace As of version 3.4, messages can be formatted
         *                    with a monospace font.
         * @return the current Builder instance
         */
        public Builder setMonospace(boolean isMonospace) {
            msg.monospace = isMonospace;
            return this;
        }
        
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getDevice() {
        return device;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitleForURL() {
        return titleForURL;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public Long getTimestamp() {return timestamp;}

    public String getSound() {
        return sound;
    }
    
    public int getRetry() {
        return retry;
    }
    
    public int getExpire() {
        return expire;
    }
    
    public String getCallbackUrl() {
        return emergencyCallbackUrl;
    }

    /**
     * @deprecated Use getImage()
     */
    @Deprecated
    public File getAttachment() { return getImage();}

    public File getImage() {return image;}

    public boolean getHTML() {return html;}

    public boolean getMonospace() {return monospace;}
}
