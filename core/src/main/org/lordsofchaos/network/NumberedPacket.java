package org.lordsofchaos.network;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NumberedPacket implements Serializable
{
    private Object data;
    private String time;
    
    public NumberedPacket(Object data) {
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
