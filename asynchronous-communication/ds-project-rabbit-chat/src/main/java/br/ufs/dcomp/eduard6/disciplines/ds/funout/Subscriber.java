package br.ufs.dcomp.eduard6.disciplines.ds.funout;

import br.ufs.dcomp.eduard6.disciplines.ds.rabbit.RabbitHelper;
import com.rabbitmq.client.Delivery;

import java.io.IOException;

public class Subscriber {
    public static final String QUEUE_PREFIX = "simple-funout-queue-";

    public static void main(String[] args) throws IOException {
        RabbitHelper.getInstance().createQueue(QUEUE_PREFIX + 1);
        RabbitHelper.getInstance().createQueue(QUEUE_PREFIX + 2);

        RabbitHelper.getInstance().listenQueue(QUEUE_PREFIX + 1, Subscriber::handle);
        RabbitHelper.getInstance().listenQueue(QUEUE_PREFIX + 2, Subscriber::handle);

    }

    private static void handle(String consumerTag, Delivery message) {
        System.out.println(message.getEnvelope().getExchange() + ": " + new String(message.getBody()));
    }
}
