package br.ufs.dcomp.eduard6.ds.car;

import br.ufs.dcomp.eduard6.ds.car.gps.CarGPS;
import br.ufs.dcomp.eduard6.ds.car.gps.GPSInfoCollector;
import br.ufs.dcomp.eduard6.ds.car.listener.CarAlertListener;
import br.ufs.dcomp.eduard6.ds.car.listener.CarMessageListener;
import br.ufs.dcomp.eduard6.ds.utils.CentralQueueNames;
import br.ufs.dcomp.eduard6.ds.rabbit.RabbitHelper;
import br.ufs.dcomp.eduard6.ds.to.AlertEvent;
import br.ufs.dcomp.eduard6.ds.to.MessageEvent;
import br.ufs.dcomp.eduard6.ds.to.ObrigatoryStopEvent;
import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Car {
    private final String id;
    private final CarGPS gps;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Car(String id, CarGPS gps) {
        this.id = id;
        this.gps = gps;
        try {
            createQueues();
            createListeners();
            startInfoCollector();
        } catch (IOException e) {
            this.shutdown();
            System.exit(-1);
        }
    }

    /**
     * Inicia e agenda a coleta automática de informações de GPS.
     */
    private void startInfoCollector() {
        scheduler.scheduleAtFixedRate(new GPSInfoCollector(this), 0, 1, TimeUnit.MINUTES);
    }

    /**
     * "Desliga" o carro.
     */
    public void shutdown() {
        try {
            scheduler.shutdownNow();
            RabbitHelper.getInstance().close();
        } catch (IOException e) {
            System.out.println("Erro desligando carro: " + e.getMessage());
        }
    }

    /**
     * Cria as filas necessárias para o funcionamento do carro.
     *
     * @throws IOException Caso ocorra algum erro de comunicação entre o carro e o broker.
     */
    private void createQueues() throws IOException {
        RabbitHelper.getInstance().createQueue(CentralQueueNames.PRIVATE_MESSAGE_QUEUE_PREFIX + this.id);
        RabbitHelper.getInstance().createQueue(CentralQueueNames.PRIVATE_ALERT_QUEUE_PREFIX + this.id);
    }

    /**
     * Configura listeners em filas para tratamento de mensagens e alertas.
     *
     * @throws IOException Caso ocorra algum erro de comunicação entre o carro e o broker.
     */
    private void createListeners() throws IOException {
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.PRIVATE_MESSAGE_QUEUE_PREFIX + this.id, new CarMessageListener());
        RabbitHelper.getInstance().listenQueue(CentralQueueNames.PRIVATE_ALERT_QUEUE_PREFIX + this.id, new CarAlertListener(this));
        RabbitHelper.getInstance().bindQueueToFunout(CentralQueueNames.PRIVATE_ALERT_QUEUE_PREFIX + this.id, CentralQueueNames.ALERT_FUNOUT_NAME);
    }

    public CarGPS getGps() {
        return gps;
    }

    public String getId() {
        return id;
    }

    /**
     * Envia uma mensagem para um carro ou central
     * @param id o id do carro
     * @param message a mensagem a ser enviada
     * @return True se a mensagem foi enviada
     */
    public boolean sendMessage(String id, String message) {
        try {
            RabbitHelper.getInstance().createQueue(CentralQueueNames.PRIVATE_MESSAGE_QUEUE_PREFIX + id);

            MessageEvent messageEvent = new MessageEvent();
            messageEvent.setFrom(this.id);
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
     * Envia um alerta para a central, que é retransmitido para todos os carros.
     * @param text Texto à ser enviado no alerta
     * @return True se o alerta foi enviado
     */
    public boolean sendAlert(String text) {
        AlertEvent evt = new AlertEvent();
        evt.setAlert(text);
        evt.setLifeTimeInMinutes(60);
        evt.setOriginVehicle(this.id);
        evt.setCreatedAt(LocalDateTime.now());
        try {
            RabbitHelper.getInstance().enqueueMessage(CentralQueueNames.CENTRAL_ALERT_QUEUE_NAME, evt);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Notifica uma parada obrigatória. Os dados da parada são extraídos do GPS.
     *
     * @return True se a parada foi notificada com sucesso.
     */
    public boolean notifyStop() {
        ObrigatoryStopEvent evt = new ObrigatoryStopEvent();
        evt.setSenderId(this.getId());
        evt.setDateTime(LocalDateTime.now());
        UpdateCarInfoEvent info = gps.getCurrentInfo();
        evt.setLocation(info.getLatitude() + ";" + info.getLongitude());

        try {
            RabbitHelper.getInstance().enqueueMessage(CentralQueueNames.OBRIGATORY_STOP_QUEUE_NAME, evt);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                '}';
    }
}
