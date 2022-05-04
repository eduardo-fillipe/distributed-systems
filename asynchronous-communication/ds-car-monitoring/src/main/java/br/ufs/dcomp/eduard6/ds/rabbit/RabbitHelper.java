package br.ufs.dcomp.eduard6.ds.rabbit;

import br.ufs.dcomp.eduard6.ds.utils.MessageUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitHelper implements Closeable {
    private static final RabbitHelper INSTANCE = new RabbitHelper();
    private Connection connection;
    private Channel channel;
    private boolean isConnected;

    private RabbitHelper() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            isConnected = true;
        } catch (IOException | TimeoutException ex) {
            ex.printStackTrace();
        }
    }

    public static RabbitHelper getInstance() {
        return INSTANCE;
    }

    public void createQueue(String queueName) throws IOException {
        if (!isConnected)
            throw new IllegalStateException();
        channel.queueDeclare(queueName, true, false, false, null);
    }

    public void createFunout(String exchangeName) throws IOException {
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.FANOUT, true);
    }

    public void bindQueueToFunout(String queueName, String funoutName) throws IOException {
        channel.queueBind(queueName, funoutName, "");
    }

    public void enqueueMessage(String queueName, Serializable content) throws IOException {
        if (!isConnected)
            throw new IllegalStateException();

        channel.basicPublish("", queueName,  getMessageProperties(content),
                MessageUtils.toJson(content).getBytes(StandardCharsets.UTF_8));
    }

    public void funoutMessage(String exchangeName, Serializable content) throws IOException {
        channel.basicPublish(exchangeName, "", getMessageProperties(content),
                MessageUtils.toJson(content).getBytes(StandardCharsets.UTF_8));
    }

    public void listenQueue(String queueName, DeliverCallback callback) throws IOException {
        channel.basicConsume(queueName, true, callback, consumerTag -> { });
    }

    private <T extends Serializable> AMQP.BasicProperties getMessageProperties(T content) {
        return new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)
                .timestamp(new Date())
                .contentType("application/json")
                .contentEncoding("UTF-8")
                .headers(
                        Map.of("class-name", content.getClass().getName(),
                                "send-timestamp", new Date())
                )
                .build();
    }

    @Override
    public void close() throws IOException {
        try {
            channel.close();
            connection.close();
        } catch (TimeoutException e) {
            throw new IOException(e);
        }
    }
}
