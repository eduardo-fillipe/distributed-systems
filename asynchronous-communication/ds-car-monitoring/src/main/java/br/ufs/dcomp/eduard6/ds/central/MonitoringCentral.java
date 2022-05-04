package br.ufs.dcomp.eduard6.ds.central;

import br.ufs.dcomp.eduard6.ds.central.listener.AlertListener;
import br.ufs.dcomp.eduard6.ds.central.listener.CentralMessageListener;
import br.ufs.dcomp.eduard6.ds.central.listener.ObrigatoryStopListener;
import br.ufs.dcomp.eduard6.ds.central.listener.UpdateCarInfoListener;
import br.ufs.dcomp.eduard6.ds.rabbit.RabbitHelper;
import br.ufs.dcomp.eduard6.ds.to.AlertEvent;
import br.ufs.dcomp.eduard6.ds.to.MessageEvent;
import br.ufs.dcomp.eduard6.ds.to.ObrigatoryStopEvent;
import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;
import br.ufs.dcomp.eduard6.ds.utils.CentralQueueNames;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Classe que representa uma Central de Monitoramento
 */
public class MonitoringCentral implements Closeable {
    private final Map<String, UpdateCarInfoEvent> carInfoEvents; //Simulates a database
    private final Map<String, List<ObrigatoryStopEvent>> obrigatoryStopEvents; //Simulates a database

    public MonitoringCentral(){
        carInfoEvents = new HashMap<>();
        obrigatoryStopEvents = new HashMap<>();
        try {
            createQueues();
            startListeners();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Cria as filas necessárias para o funcionamento da Central
     *
     * @throws IOException Caso ocorra algum erro de comunicação entre o carro e o broker.
     */
    private void createQueues() throws IOException {
        RabbitHelper.getInstance().createQueue(CentralQueueNames.UPDATE_CAR_INFO_QUEUE_NAME);
        RabbitHelper.getInstance().createQueue(CentralQueueNames.OBRIGATORY_STOP_QUEUE_NAME);
        RabbitHelper.getInstance().createQueue(CentralQueueNames.CENTRAL_ALERT_QUEUE_NAME);
        RabbitHelper.getInstance().createQueue(CentralQueueNames.CENTRAL_MESSAGE_QUEUE_NAME);

        RabbitHelper.getInstance().createFunout(CentralQueueNames.ALERT_FUNOUT_NAME);
    }

    /**
     * Inicia os listeners necessários para o funcionamento da Central.
     *
     * @throws IOException Caso ocorra algum erro de comunicação entre o carro e o broker.
     */
    private void startListeners() throws IOException {
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.UPDATE_CAR_INFO_QUEUE_NAME, new UpdateCarInfoListener(this));
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.OBRIGATORY_STOP_QUEUE_NAME, new ObrigatoryStopListener(this));
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.CENTRAL_ALERT_QUEUE_NAME, new AlertListener(this));
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.CENTRAL_MESSAGE_QUEUE_NAME, new CentralMessageListener());

    }

    /**
     * Retorna todos os carros ativos. Um carro ativo é um carro que atualizou sua posição de GPS dentro dos últimos 2 minutos.
     *
     * @return Uma lista de carros ativos no sistema
     */
    public List<String> getActiveCars() {
        return carInfoEvents.keySet()
                .stream()
                .filter(key ->
                    Duration.between(carInfoEvents.get(key).getUpdateDateTime(), LocalDateTime.now())
                            .abs().toMinutes() < 2
                ).collect(Collectors.toList());
    }

    /**
     * Atualiza as informações de GPS de um carro na central.
     *
     * @param id o ID do carro
     * @param event O evento de atualização
     */
    public void updateCarInfo(String id, UpdateCarInfoEvent event) {
        if (carInfoEvents.containsKey(id))
            carInfoEvents.replace(id, event);
        else
            carInfoEvents.put(id, event);
    }

    /**
     * Retorna as informações de GPS de um carro
     * @param id o ID do carro
     * @return o evento que descreve a posição enviada pelo carro.
     */
    public Optional<UpdateCarInfoEvent> getCarInfo(String id) {
        return Optional.ofNullable(this.carInfoEvents.get(id));
    }

    /**
     * Retorna todas as informações de GPS dos carros que estão na Central.
     *
     * @return uma HashTable contendo as informações de todos os carros no sistem, inclusive os inativos.
     */
    public Map<String, UpdateCarInfoEvent> getCarsInfo() {
        return Collections.unmodifiableMap(carInfoEvents);
    }

    /**
     * Adiciona uma notificação parada obrigatória à um veículo
     *
     * @param id o ID do veículo
     * @param evt o evento de parada obrigatória
     */
    public void addObrigatoryStop(String id, ObrigatoryStopEvent evt) {
        if (obrigatoryStopEvents.containsKey(id)) {
            obrigatoryStopEvents.get(id).add(evt);
        } else {
            ArrayList<ObrigatoryStopEvent> arr = new ArrayList<>();
            arr.add(evt);
            obrigatoryStopEvents.put(id, arr);
        }
    }

    /**
     * Retorna todas as paradas obrigatórias de um veículo
     * @param id O id do veículo
     */
    public Optional<List<ObrigatoryStopEvent>> getObrigatoryStops(String id) {
        if (obrigatoryStopEvents.containsKey(id)) {
            return Optional.of(Collections.unmodifiableList(obrigatoryStopEvents.get(id)));
        }
        return Optional.empty();
    }

    /**
     * Alerta todos os veículos com um evento de alerta
     * @param alert O evento de alerta
     */
    public void alertVehicles(AlertEvent alert) {
        try {
            RabbitHelper.getInstance().funoutMessage(CentralQueueNames.ALERT_FUNOUT_NAME, alert);
        } catch (IOException e) {
            System.out.println("NÃO FOI POSSÍVEL TRANSMITIR ALERTA: " + e.getMessage());
        }
    }

    /**
     * Alerta todos os veículos com um texto
     * @param alertText O texto do alerta que será gerado
     */
    public void alertVehicles(String alertText) {
        if (alertText != null && !alertText.isBlank()) {
            try {
                AlertEvent evt = new AlertEvent();
                evt.setOriginVehicle("MONITORING_CENTRAL");
                evt.setAlert(alertText);
                evt.setCreatedAt(LocalDateTime.now());
                evt.setLifeTimeInMinutes(60);
                RabbitHelper.getInstance().funoutMessage(CentralQueueNames.ALERT_FUNOUT_NAME, evt);
            } catch (IOException e) {
                System.out.println("NÃO FOI POSSÍVEL TRANSMITIR ALERTA: " + e.getMessage());
            }
        }
    }

    /**
     * Envia uma mensagem para um veículo
     * @param id O ID do veículo
     * @param message o texto da mensagem que será enviada
     */
    public boolean sendMessage(String id, String message) {
        try {
            RabbitHelper.getInstance().createQueue(CentralQueueNames.PRIVATE_MESSAGE_QUEUE_PREFIX + id);

            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setFrom("CENTRAL");
            messageEvent.setContent(message);
            messageEvent.setTo(id);
            messageEvent.setSendDateTime(LocalDateTime.now());

            RabbitHelper.getInstance().enqueueMessage(CentralQueueNames.PRIVATE_MESSAGE_QUEUE_PREFIX + id, messageEvent);
            return true;
        } catch (IOException e) {
            System.out.println("ERRO ENVIANDO MENSAGEM: " + e.getMessage());
            return false;
        }
    }

    /**
     * Encerra a Central
     */
    @Override
    public void close() throws IOException {
        RabbitHelper.getInstance().close();
    }
}
