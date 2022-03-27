package br.ufs.dcomp.eduard6.disciplines.ds.direct;

import br.ufs.dcomp.eduard6.disciplines.ds.rabbit.RabbitHelper;
import com.google.gson.Gson;

import java.io.IOException;

public class Subscriber {
    public static final String QUEUE_NAME = "direct-queue";

    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        RabbitHelper.getInstance().createQueue(QUEUE_NAME);
        RabbitHelper.getInstance().listenQueue(QUEUE_NAME, (consumerTag, message) -> {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(String.valueOf(message.getProperties().getHeaders().get("class-name")));
            } catch (ClassNotFoundException ex) {
                System.out.println("class not found: "+ ex.getMessage());
            }

            if (clazz != null)
                System.out.println(gson.fromJson(new String(message.getBody()), clazz));
            else
                System.out.println(new String(message.getBody()));
        });
    }
}
