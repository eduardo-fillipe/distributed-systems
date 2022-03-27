package br.ufs.dcomp.eduard6.disciplines.ds.direct;

import br.ufs.dcomp.eduard6.disciplines.ds.chat.MessagePackage;
import br.ufs.dcomp.eduard6.disciplines.ds.rabbit.RabbitHelper;

import java.io.IOException;

public class Publisher {
    public static final String QUEUE_NAME = "direct-queue";
    public static void main(String[] args) throws IOException {
        RabbitHelper.getInstance().createQueue(QUEUE_NAME);
        RabbitHelper.getInstance().enqueueMessage(QUEUE_NAME, new MessagePackage("Publisher", "Subscriber", "Olá da fila direta!"));
        RabbitHelper.getInstance().close();
    }
}
