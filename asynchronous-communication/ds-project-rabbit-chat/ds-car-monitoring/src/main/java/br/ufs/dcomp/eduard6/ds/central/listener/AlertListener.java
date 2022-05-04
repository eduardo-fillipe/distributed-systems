package br.ufs.dcomp.eduard6.ds.central.listener;

import br.ufs.dcomp.eduard6.ds.central.MonitoringCentral;
import br.ufs.dcomp.eduard6.ds.to.AlertEvent;
import br.ufs.dcomp.eduard6.ds.utils.MessageUtils;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Listener que recebe mensanges de alerta e reencaminha para todos os carros.
 */
public class AlertListener implements DeliverCallback {

    private final MonitoringCentral monitoringCentral;

    public AlertListener(MonitoringCentral monitoringCentral) {
        this.monitoringCentral = monitoringCentral;
    }

    @Override
    public void handle(String consumerTag, Delivery message) {
        AlertEvent alertEvent = MessageUtils.getBodyAs(message, AlertEvent.class);
        if (alertEvent != null) {
            if (Duration.between(alertEvent.getCreatedAt(), LocalDateTime.now())
                    .abs().toMinutes() < alertEvent.getLifeTimeInMinutes()) {
                monitoringCentral.alertVehicles(alertEvent);
            }
        }
    }
}
