package br.ufs.dcomp.eduard6.ds.central.listener;

import static br.ufs.dcomp.eduard6.ds.utils.MessageUtils.*;

import br.ufs.dcomp.eduard6.ds.central.MonitoringCentral;
import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * Listener que trata o recebimento de atualização de posição e velocidade de um carro.
 */
public class UpdateCarInfoListener implements DeliverCallback {
    private final MonitoringCentral monitoringCentral;

    public UpdateCarInfoListener(MonitoringCentral monitoringCentral) {
        this.monitoringCentral = monitoringCentral;
    }

    @Override
    public void handle(String consumerTag, Delivery message) {
        try {
            UpdateCarInfoEvent evt = getBodyAs(message, UpdateCarInfoEvent.class);
            if (evt != null)
                monitoringCentral.updateCarInfo(evt.getSenderId(), evt);
        } catch (Exception e) {
            System.out.println("ERRO AO ATUALIZAR INFORMAÇÕES DO CARRO: " + e.getMessage());
        }
    }
}
