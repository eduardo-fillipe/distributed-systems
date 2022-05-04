package br.ufs.dcomp.eduard6.ds.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Delivery;

import java.io.Serializable;


/**
 * Utils para serialização e desserialização de mensagem adivindas do Broker Rabbit.
 */
public class MessageUtils {
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static String getSenderId(Delivery message) {
        if (message == null) {
            return null;
        }
        return String.valueOf(message.getProperties().getHeaders().get("senderId"));
    }

    public static <T> T getBodyAs(Delivery message, Class<T> type) {
        try {
            return mapper.readValue(new String(message.getBody()), type);
        } catch (Exception e) {
            System.out.printf("ERRO LENDO MENSAGEM: %s COMO %s%n", new String(message.getBody()), type.getName());
            return null;
        }
    }

    public static String toJson(Serializable content) {
        try {
            return mapper.writeValueAsString(content);
        } catch (JsonProcessingException e) {
            System.out.printf("ERRO ESCREVENDO MENSAGEM: %s COMO JSON", content.toString());
            return "";
        }
    }
}
