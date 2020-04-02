package org.lordsofchaos.network;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Object that contains data in the form of a Serializable object, annotated with the time of creation.
 */
public class TimestampedPacket implements Serializable
{
    private Object data;
    private String time;
    
    public TimestampedPacket(Object data) {
        this.data = data;
        this.time = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now());
    }
    
    public Object getData() {
        return data;
    }
    
    public String getTime() {
        return time;
    }
}
