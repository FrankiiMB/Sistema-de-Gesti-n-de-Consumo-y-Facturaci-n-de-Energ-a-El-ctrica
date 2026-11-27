package morosidadcorteyreconexion;

import altanuevosuministro.Suministro;
import altanuevosuministro.Trabajador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TecnicoSupervisorComercial extends Trabajador {

    private final List<String> actasInspeccion = new ArrayList<>();

    public TecnicoSupervisorComercial(String codigoTrabajador, String cargo) {
        super(codigoTrabajador, cargo);
    }

    public TecnicoSupervisorComercial(String codigoTrabajador, String cargo, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, cargo, nombres, apellidos, dni);
    }

    public boolean inspeccionarCorteEnCampo(OrdenCorte orden) {
        if (orden == null || !orden.validarEjecucion()) {
            return false;
        }
        registrarCorteYAplicarCargo(orden);
        return true;
    }

    public boolean inspeccionarReconexionEnCampo(OrdenReconexion orden) {
        if (orden == null || !orden.validarEjecucion()) {
            return false;
        }
        actasInspeccion.add("Reconexión auditada conforme en fecha " + orden.getFechaEjecucion()
                + " | Cargo agendado: S/ " + orden.getCargoReconexion());
        return true;
    }

    public void registrarCorteYAplicarCargo(OrdenCorte orden) {
        if (orden == null) {
            return;
        }
        // RN-18: Cargo administrativo según modalidad de corte (Tipo I, II o III)
        int nivel = orden.getTipoCorte() != null ? orden.getTipoCorte().ordinal() : 0;
        double cargo = switch (nivel) {
            case 0 ->
                22.50; // Tipo I: Fusible
            case 1 ->
                45.00; // Tipo II: Caja de medición
            default ->
                85.00; // Tipo III: Línea aérea / acometida
        };
        orden.setCargoAdministrativo(cargo);
        actasInspeccion.add("Corte auditado (" + orden.getTipoCorte() + ") | Lectura: "
                + orden.getLecturaEjecucion() + " | Cargo aplicado: S/ " + cargo);
    }

    public void detectarReconexionNoAutorizada(Suministro suministro) {
        if (suministro == null) {
            return;
        }
        // RN-17 / HU-21: Habilita aplicación de corte progresivo más drástico
        OrdenCorte nuevoCorteDrastico = new OrdenCorte(suministro, OrdenCorte.resolverTipoCorte(0));
        nuevoCorteDrastico.escalarSiguienteTipoCorte();
        actasInspeccion.add("ALERTA: Reconexión no autorizada detectada en suministro " + suministro
                + ". Escalamiento habilitado a " + nuevoCorteDrastico.getTipoCorte());
    }

    public List<String> getActasInspeccion() {
        return Collections.unmodifiableList(actasInspeccion);
    }
}
