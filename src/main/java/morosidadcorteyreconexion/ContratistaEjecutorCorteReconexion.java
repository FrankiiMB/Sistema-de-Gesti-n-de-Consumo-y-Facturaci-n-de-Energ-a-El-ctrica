package morosidadcorteyreconexion;

import altanuevosuministro.OrdenServicio;
import altanuevosuministro.Trabajador;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContratistaEjecutorCorteReconexion extends Trabajador implements EjecutarOrden {
    
    private final List<String> comunicacionesCampo = new ArrayList<>();

    public ContratistaEjecutorCorteReconexion(String codigoTrabajador, String cargo) {
        super(codigoTrabajador, cargo);
    }

    public ContratistaEjecutorCorteReconexion(String codigoTrabajador, String cargo, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, cargo, nombres, apellidos, dni);
    }

    public boolean realizarCorte(OrdenCorte o) {
        if (o == null || o.isAnulada()) {
            return false;
        }
        o.setFechaEjecucion(LocalDate.now());
        o.setEjecutada(true);
        o.setEtiquetaDejada(true); // HU-17: Dejar etiqueta de identificación en el predio
        comunicarResultado(o);
        return o.validarEjecucion();
    }

    public boolean realizarReconexion(OrdenReconexion o) {
        if (o == null || !o.isAutorizada()) {
            return false;
        }
        o.setFechaEjecucion(LocalDate.now());
        o.setEjecutada(true);
        comunicarResultado(o);
        return o.validarEjecucion();
    }

    @Override
    public boolean ejecutar(OrdenServicio o) {
        if (o instanceof OrdenCorte corte) {
            return realizarCorte(corte);
        }
        if (o instanceof OrdenReconexion reconexion) {
            return realizarReconexion(reconexion);
        }
        return false;
    }

    @Override
    public void comunicarResultado(OrdenServicio o) {
        if (o instanceof OrdenCorte corte) {
            comunicacionesCampo.add("Corte ejecutado (" + corte.getTipoCorte() + ") en fecha " + corte.getFechaEjecucion());
        } else if (o instanceof OrdenReconexion reconexion) {
            comunicacionesCampo.add("Reconexión ejecutada en fecha " + reconexion.getFechaEjecucion());
        }
    }

    public List<String> getComunicacionesCampo() {
        return Collections.unmodifiableList(comunicacionesCampo);
    }
}
