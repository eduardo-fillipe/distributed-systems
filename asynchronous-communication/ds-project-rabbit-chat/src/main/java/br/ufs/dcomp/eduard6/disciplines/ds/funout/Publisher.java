package br.ufs.dcomp.eduard6.disciplines.ds.funout;

import br.ufs.dcomp.eduard6.disciplines.ds.chat.MessagePackage;
import br.ufs.dcomp.eduard6.disciplines.ds.rabbit.RabbitHelper;

import java.io.IOException;
import java.time.LocalDateTime;

public class Publisher {
    public static final String EXCHANGE_NAME = "simple-funout";
    public static final String QUEUE_PREFIX = "simple-funout-queue-";

    public static void main(String[] args) throws IOException {
        RabbitHelper.getInstance().createFunout(EXCHANGE_NAME);
        RabbitHelper.getInstance().createQueue(QUEUE_PREFIX + 1);
        RabbitHelper.getInstance().createQueue(QUEUE_PREFIX + 2);
        RabbitHelper.getInstance().bindQueueToFunout(QUEUE_PREFIX+1, EXCHANGE_NAME);
        RabbitHelper.getInstance().bindQueueToFunout(QUEUE_PREFIX+2, EXCHANGE_NAME);

        RabbitHelper.getInstance().funoutMessage(EXCHANGE_NAME,
                new MessagePackage("FUNOUT", "EVERYBODY", "The time is: " + LocalDateTime.now()));

        RabbitHelper.getInstance().close();
    }
}
