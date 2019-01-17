Pushover4j
--------
[![Maven Central](https://img.shields.io/maven-central/v/com.github.ilpersi/pushover-client.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.ilpersi%22%20AND%20a:%22pushover-client%22)

A fork of the [pushover4j](https://github.com/sps/pushover4j/) java client for [pushover](https://pushover.net/) notification service. Example:
```
PushoverClient client = new PushoverRestClient();        

client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
        .setUserId("USER_ID_TOKEN")
        .setMessage("testing!")
        .build());
```
Just that fast. Now the message can be customized with much more control as seen below. We also capture the result from the API call and can reference that for success or error message logging:
```
// push a message with optional fields
//(note we are reusing the client from the first example here)
Status result = client.pushMessage(PushoverMessage.builderWithApiToken("MY_APP_API_TOKEN")
      .setUserId("USER_ID_TOKEN")
      .setMessage("Welcome to pushover4j!")
      .setDevice("<your device name>")
      .setPriority(MessagePriority.HIGH) // LOWEST,QUIET,NORMAL,HIGH,EMERGENCY
      .setTitle("Testing")
      .setUrl("https://github.com/ilpersi/pushover4j")
      .setTitleForURL("pushover4j github repo")
      .setSound("magic")
      .setAttachment(new File("cool_image.png"))
      .build());
System.out.println(String.format("status: %d, request id: %s", result.getStatus(), result.getRequestId()));
```
And you can keep up to date with the latest in available sounds with a quick call as well.
```
// get and print out the list of available sounds:
for (PushOverSound sound : client.getSounds() ) {
    System.out.println(String.format("name: %s, id: %s", sound.getName(), sound.getId()));
}              
```

### Installing 
Installation is best done through the Maven build system. We should keep the maven system up to date with releases but you are free to manually install things. Java 6 or higher required. 
##### For maven
Use my fork of the ` pushover-client ` or add the following to your POM
```
<dependency>
    <groupId>com.github.ilpersi</groupId>
    <artifactId>pushover-client</artifactId>
    <version>1.0.2</version>
</dependency>
```

##### Other build systems
for the non-maven types, here are the required dependencies
* [pushover4j](https://github.com/ilpersi/pushover4j/downloads)
* [gson 2.8.5](https://github.com/google/gson)
* [apache commons httpclient 4.5.6](http://hc.apache.org/downloads.cgi)
 
##### Note: Java 6 users
For java 6 users you will need to enhance your security provider. Pushover uses a Diffie-Hillman 1024 for handshake on its new key which Java 6 does not support natively. If your country allows it you may install the Boucny Castle provider to continue using the service. Otherwise newer versions of Java 7 and Java 8 will work natively.

### Whats new from the standard version?
I've integrated all the fixes from [hhocker fork](https://github.com/hhocker/pushover4j) and added the possibility to send [image attachmets](https://blog.pushover.net/posts/pushing-images-with-pushover-30) together with messages