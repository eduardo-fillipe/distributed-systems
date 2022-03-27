package br.ufs.dcomp.eduard6.disciplines.ds.rabbit;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitHelper implements Closeable {
    private static final RabbitHelper INSTANCE = new RabbitHelper();
    private Connection connection;
    private Channel channel;
    private boolean isConnected;
    private Gson gson;

    private RabbitHelper() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            gson = new Gson();
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

    public <T extends Serializable> void enqueueMessage(String queueName, T content) throws IOException {
        if (!isConnected)
            throw new IllegalStateException();

        channel.basicPublish("", queueName,  getMessageProperties(content), gson.toJson(content).getBytes());
    }

    public <T extends Serializable> void funoutMessage(String exchangeName, T content) throws IOException {
        channel.basicPublish(exchangeName, "", getMessageProperties(content),
                gson.toJson(content).getBytes());
    }

    public void listenQueue(String queueName, DeliverCallback callback) throws IOException {
        channel.basicConsume(queueName, true, callback, consumerTag -> { });
    }

    private <T extends Serializable> AMQP.BasicProperties getMessageProperties(T content) {
        return new AMQP.BasicProperties()
                .builder()
                .deliveryMode(2)
                .contentType("application/json")
                .contentEncoding("UTF-8")
                .headers(Map.of("class-name", content.getClass().getName()))
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
