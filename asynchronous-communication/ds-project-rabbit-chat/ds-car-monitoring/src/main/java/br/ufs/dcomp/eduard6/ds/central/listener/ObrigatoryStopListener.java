package br.ufs.dcomp.eduard6.ds.central.listener;

import br.ufs.dcomp.eduard6.ds.central.MonitoringCentral;
import br.ufs.dcomp.eduard6.ds.to.ObrigatoryStopEvent;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import static br.ufs.dcomp.eduard6.ds.utils.MessageUtils.*;

/**
 * Listener que trata o recebimento de eventos de paradas obrigatórias
 */
public class ObrigatoryStopListener implements DeliverCallback {
    private final MonitoringCentral monitoringCentral;

    public ObrigatoryStopListener(MonitoringCentral monitoringCentral) {
        this.monitoringCentral = monitoringCentral;
    }
    @Override
    public void handle(String consumerTag, Delivery message) {
        ObrigatoryStopEvent evt = getBodyAs(message, ObrigatoryStopEvent.class);
        if (evt != null)
            monitoringCentral.addObrigatoryStop(evt.getSenderId(), evt);
    }
}
