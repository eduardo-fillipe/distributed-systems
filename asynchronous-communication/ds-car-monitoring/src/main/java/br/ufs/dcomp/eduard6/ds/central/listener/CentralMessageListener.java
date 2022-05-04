package br.ufs.dcomp.eduard6.ds.central.listener;

import br.ufs.dcomp.eduard6.ds.to.MessageEvent;
import br.ufs.dcomp.eduard6.ds.utils.MessageUtils;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

/**
 * Listener para recebimento de mensagens de carros
 */
public class CentralMessageListener implements DeliverCallback {

    @Override
    public void handle(String consumerTag, Delivery message) {
        System.out.println("[CENTRAL] Nova mensagem: " + MessageUtils.getBodyAs(message, MessageEvent.class));
    }
}
