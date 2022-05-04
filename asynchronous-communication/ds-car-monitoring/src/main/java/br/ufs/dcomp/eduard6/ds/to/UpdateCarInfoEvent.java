package br.ufs.dcomp.eduard6.ds.to;

import java.io.Serializable;
import java.time.LocalDateTime;

public class UpdateCarInfoEvent implements Serializable {
    private double velocityInKmH;
    private float latitude;
    private float longitude;
    private LocalDateTime updateDateTime;
    private String senderId;

    public double getVelocityInKmH() {
        return velocityInKmH;
    }

    public void setVelocityInKmH(double velocityInKmH) {
        this.velocityInKmH = velocityInKmH;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public String toString() {
        return "UpdateCarInfoEvent{" +
                "velocityInKmH=" + velocityInKmH +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", updateDateTime=" + updateDateTime +
                ", senderId='" + senderId + '\'' +
                '}';
    }
}
