package altanuevosuministro;

import lecturafacturacionyrepartoderecibos.IncidenciaLectura;
import lecturafacturacionyrepartoderecibos.LecturaMedidor;

public class SupervisorControlMediciones extends Trabajador {

    public SupervisorControlMediciones() {
        super("T-0003", "Supervisor de Control de Mediciones");
    }

    public SupervisorControlMediciones(String codigoTrabajador, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, "Supervisor de Control de Mediciones", nombres, apellidos, dni);
    }

    public OrdenInstalacion programarInstalacion(SolicitudSuministro solicitud, ContratistaInstalacion contratista) {
        OrdenInstalacion orden = new OrdenInstalacion(solicitud, contratista);
        orden.generarOrden();
        return orden;
    }

    public void programarInstalacion(Contrato contrato, ContratistaInstalacion contratista) { }
    public void observarLecturaYSolicitarCorreccion(LecturaMedidor lectura) { }
    public void gestionarIncidenciaLectura(IncidenciaLectura incidencia) { }
    public boolean autorizarCalculoEspecial(Medidor medidor) { return false; }
}