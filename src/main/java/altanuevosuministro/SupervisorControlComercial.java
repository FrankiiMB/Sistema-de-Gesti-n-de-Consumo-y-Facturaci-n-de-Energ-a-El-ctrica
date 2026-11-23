package altanuevosuministro;

import morosidadcorteyreconexion.OrdenReconexion;
import morosidadcorteyreconexion.OrdenCorte;

public class SupervisorControlComercial extends Trabajador {

    public SupervisorControlComercial() {
        super("T-0002", "Supervisor de Control Comercial");
    }

    public SupervisorControlComercial(String codigoTrabajador, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, "Supervisor de Control Comercial", nombres, apellidos, dni);
    }

    public boolean evaluarFactibilidadTecnica(Predio predio) {
        return predio != null && predio.isEsFactible();
    }

    public boolean registrarResultadoFactibilidad(SolicitudSuministro solicitud, int esViable) {
        if (solicitud == null) return false;
        if (esViable == 1) {
            solicitud.getPredio().setEsFactible(true);
            solicitud.actualizarEstado(EstadoSolicitud.APROBADA);
            return true;
        } else {
            solicitud.getPredio().setEsFactible(false);
            solicitud.actualizarEstado(EstadoSolicitud.RECHAZADA);
            return false;
        }
    }

    public void coordinarInstalacion(OrdenInstalacion orden) {
        if (orden != null) orden.generarOrden();
    }

    public void coordinarYSupervisarCorte(OrdenCorte orden) { }
    public void coordinarYSupervisarReconexion(OrdenReconexion orden) { }
}