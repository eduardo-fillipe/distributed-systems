package br.ufs.dcomp.eduard6.ds.utils;

/**
 * Nome das filas do sistema. Cada fila trata de um assunto específico
 */
public class CentralQueueNames {

    /**
     * Fila de atualização de eventos de GPS
     */
    public final static String UPDATE_CAR_INFO_QUEUE_NAME = "UPDATE_CAR_INFO_QUEUE";

    /**
     * Fila de notificação de eventos de parada obrigatória
     */
    public final static String OBRIGATORY_STOP_QUEUE_NAME = "OBRIGATORY_STOP_QUEUE";

    /**
     * Funout de mensagens de alerta
     */
    public final static String ALERT_FUNOUT_NAME = "ALERT_FUNOUT";

    /**
     * Fila de alertas que são encaminhados para a central para que sejam então distribuídos para todos os carros.
     */
    public final static String CENTRAL_ALERT_QUEUE_NAME = "CENTRAL_ALERT_QUEUE";

    /**
     * Prefixo usado para a criação de filas de mensagens privadas
     */
    public final static String PRIVATE_MESSAGE_QUEUE_PREFIX = "PRIVATE_MESSAGE_QUEUE_";

    /**
     * Nome da fila de mensagens privadas para a Central
     */
    public final static String CENTRAL_MESSAGE_QUEUE_NAME = PRIVATE_MESSAGE_QUEUE_PREFIX + "CENTRAL";

    /**
     * Prefixo para filas de alertas enviados para carros. Cada carro possui uma fila privada.
     */
    public final static String PRIVATE_ALERT_QUEUE_PREFIX = "PRIVATE_ALERT_QUEUE_";

}
