package br.ufs.dcomp.eduard6.ds.car.gps;

import br.ufs.dcomp.eduard6.ds.to.UpdateCarInfoEvent;

/**
 * Interface que representa um GPS.
 */
public interface CarGPS {
    UpdateCarInfoEvent getCurrentInfo();
}
