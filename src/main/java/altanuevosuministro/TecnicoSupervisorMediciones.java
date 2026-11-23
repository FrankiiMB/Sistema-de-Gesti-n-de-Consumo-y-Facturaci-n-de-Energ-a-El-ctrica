package altanuevosuministro;

import lecturafacturacionyrepartoderecibos.LecturaMedidor;
import java.time.LocalDate;

public class TecnicoSupervisorMediciones extends Trabajador implements IInspeccionable {

    public TecnicoSupervisorMediciones() {
        super("T-0004", "Técnico Supervisor de Mediciones");
    }

    public TecnicoSupervisorMediciones(String codigoTrabajador, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, "Técnico Supervisor de Mediciones", nombres, apellidos, dni);
    }

    public boolean inspeccionarInstalacionMuestreo(OrdenInstalacion orden) {
        if (orden == null) return false;
        orden.registrarResultado(true);
        return true;
    }

    public Suministro habilitarSuministro(SolicitudSuministro solicitud, Medidor medidor, double lecturaInicial) {
        medidor.registrarLecturaInicial(lecturaInicial);
        String numSum = "SUM-" + (int)(Math.random() * 900000 + 100000);
        Suministro suministro = new Suministro(numSum, solicitud.getCliente(), solicitud.getPredio(),
                medidor, "BT5B", LocalDate.now());
        solicitud.getCliente().setCodigoSuministro(numSum);
        solicitud.getCliente().setEstadoServicio("ACTIVO");
        solicitud.actualizarEstado(EstadoSolicitud.FINALIZADA);
        suministro.activarSuministroEnCronograma();
        return suministro;
    }

    public boolean verificarTomaLecturaMensual(LecturaMedidor lectura, Medidor medidor) { return false; }
    public boolean validarLecturaTransicionYCalculoEspecial(Medidor medidor, LecturaMedidor lectura) { return false; }
    public LecturaMedidor registrarLecturaEstimadaYJustificacion(String motivo, double valor) { return null; }

    @Override
    public boolean inspeccionarPorMuestreo(boolean conforme) {
        return conforme;
    }
}