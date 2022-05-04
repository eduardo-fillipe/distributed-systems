package br.ufs.dcomp.eduard6.ds.to;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ObrigatoryStopEvent implements Serializable {
    private String location;
    private LocalDateTime dateTime;

    private String senderId;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "ObrigatoryStopEvent{" +
                "location='" + location + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
