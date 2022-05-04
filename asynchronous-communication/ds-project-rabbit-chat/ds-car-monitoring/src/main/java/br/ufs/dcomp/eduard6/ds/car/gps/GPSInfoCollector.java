package br.ufs.dcomp.eduard6.ds.car.gps;

import br.ufs.dcomp.eduard6.ds.car.Car;
import br.ufs.dcomp.eduard6.ds.utils.CentralQueueNames;
import br.ufs.dcomp.eduard6.ds.rabbit.RabbitHelper;
import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;

import java.io.IOException;

/**
 * Coleta as informações de GPS e envia para a Central.
 */
public class GPSInfoCollector implements Runnable {
    private final Car car;

    public GPSInfoCollector(Car car) {
        this.car = car;
    }

    @Override
    public void run() {
        try {
            UpdateCarInfoEvent evt = car.getGps().getCurrentInfo();
            evt.setSenderId(this.car.getId());
            RabbitHelper.getInstance().enqueueMessage(CentralQueueNames.UPDATE_CAR_INFO_QUEUE_NAME, evt);
        } catch (IOException e) {
            System.out.println("ERRO ENVIANDO INFORMAÇÕES DE GPS");
        }
    }
}
