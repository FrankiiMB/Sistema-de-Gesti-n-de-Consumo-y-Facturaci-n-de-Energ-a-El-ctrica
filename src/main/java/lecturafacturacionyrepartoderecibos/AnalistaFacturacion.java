package lecturafacturacionyrepartoderecibos;

import java.util.List;

public class AnalistaFacturacion {

	private CalculoConsumo calculoConsumo;

	public boolean controlarPreFacturacion() {
		return false;
	}

	public boolean verificarConsistenciaConsumo(LecturaMedidor lectura, CalculoConsumo calculo) {
		return false;
	}

	public void emitirRecibosPeriodo() {

	}

	public void derivarObservacionASupervisor(CalculoConsumo calculo) {

	}

	public String generarReporteErrores() {
		return null;
	}

	public List<Recibo> emitirRecibosPeriodo(PeriodoFacturacion periodo) {
		return null;
	}

}
