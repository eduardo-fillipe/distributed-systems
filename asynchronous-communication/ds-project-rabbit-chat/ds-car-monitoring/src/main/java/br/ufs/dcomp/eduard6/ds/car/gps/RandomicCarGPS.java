package br.ufs.dcomp.eduard6.ds.car.gps;

import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Mock randômico de GPS que retorna posições e velocidades aleatórias.
 */
public class RandomicCarGPS implements CarGPS {
    private final Random random = new Random();

    public RandomicCarGPS() {
        System.out.println("INICIALIZANDO GPS RANDÔMICO...");
    }

    @Override
    public UpdateCarInfoEvent getCurrentInfo() {
        UpdateCarInfoEvent info = new UpdateCarInfoEvent();
        info.setLatitude(random.nextFloat());
        info.setLongitude(random.nextFloat());
        info.setVelocityInKmH(Math.random() * 300);
        info.setUpdateDateTime(LocalDateTime.now());
        return info;
    }
}
