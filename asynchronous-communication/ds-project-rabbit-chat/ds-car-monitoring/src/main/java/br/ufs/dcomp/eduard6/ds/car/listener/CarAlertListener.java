package br.ufs.dcomp.eduard6.ds.car.listener;

import br.ufs.dcomp.eduard6.ds.car.Car;
import br.ufs.dcomp.eduard6.ds.to.AlertEvent;
import br.ufs.dcomp.eduard6.ds.utils.MessageUtils;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * Listener para tratamento de alertas
 */
public class CarAlertListener implements DeliverCallback {
    private final Car car;

    public CarAlertListener(Car car) {
        this.car = car;
    }

    @Override
    public void handle(String consumerTag, Delivery message) {
        AlertEvent evt = MessageUtils.getBodyAs(message, AlertEvent.class);
        if (evt != null && !evt.getOriginVehicle().equals(this.car.getId()))
            System.out.printf("!!!!! NOVO ALERTA RECEBIDO: %s !!!!!%n", evt);
    }
}
