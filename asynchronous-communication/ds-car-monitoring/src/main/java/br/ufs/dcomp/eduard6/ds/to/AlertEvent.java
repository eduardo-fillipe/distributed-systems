package br.ufs.dcomp.eduard6.ds.to;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AlertEvent implements Serializable {
    private LocalDateTime createdAt;
    private long lifeTimeInMinutes;
    private String alert;
    private String originVehicle;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getLifeTimeInMinutes() {
        return lifeTimeInMinutes;
    }

    public void setLifeTimeInMinutes(long lifeTimeInMinutes) {
        this.lifeTimeInMinutes = lifeTimeInMinutes;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getOriginVehicle() {
        return originVehicle;
    }

    public void setOriginVehicle(String originVehicle) {
        this.originVehicle = originVehicle;
    }

    @Override
    public String toString() {
        return "AlertEvent{" +
                "createdAt=" + createdAt +
                ", lifeTimeInMinutes=" + lifeTimeInMinutes +
                ", alert='" + alert + '\'' +
                ", originVehicle='" + originVehicle + '\'' +
                '}';
    }
}
