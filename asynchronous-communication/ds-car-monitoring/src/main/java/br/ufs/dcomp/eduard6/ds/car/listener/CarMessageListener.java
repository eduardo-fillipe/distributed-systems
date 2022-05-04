package br.ufs.dcomp.eduard6.ds.car.listener;

import br.ufs.dcomp.eduard6.ds.to.MessageEvent;
import br.ufs.dcomp.eduard6.ds.utils.MessageUtils;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import java.io.IOException;

/**
 * Listener para tratamento de mensagens
 */
public class CarMessageListener implements DeliverCallback {

    @Override
    public void handle(String consumerTag, Delivery message) {
        System.out.println("Nova mensagem: " + MessageUtils.getBodyAs(message, MessageEvent.class));
    }
}
