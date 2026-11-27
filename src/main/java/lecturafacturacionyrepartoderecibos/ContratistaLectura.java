package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.Medidor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContratistaLectura {

	private final List<IncidenciaLectura> incidenciasReportadas = new ArrayList<>();

	public LecturaMedidor tomarLecturaMedidor(OrdenLectura orden, Medidor medidor) {
		if (orden == null || medidor == null) {
			return null;
		}
		String codigoMedidor = medidor.toString();
		LecturaMedidor lectura = new LecturaMedidor(codigoMedidor, 0.0, LocalDate.now());
		lectura.setNumeroMedidorEsperado(codigoMedidor);
		return lectura;
	}

	public void registrarIncidencia(IncidenciaLectura excepcion) {
		if (excepcion != null) {
			incidenciasReportadas.add(excepcion);
		}
	}

	public LecturaMedidor reevaluarVisitaYReenviar(OrdenLectura orden) {
		if (orden == null) {
			return null;
		}
		LecturaMedidor lecturaCorregida = new LecturaMedidor();
		lecturaCorregida.marcarComoCorregida(0.0);
		return lecturaCorregida;
	}

	public List<IncidenciaLectura> getIncidenciasReportadas() {
		return Collections.unmodifiableList(incidenciasReportadas);
	}
}