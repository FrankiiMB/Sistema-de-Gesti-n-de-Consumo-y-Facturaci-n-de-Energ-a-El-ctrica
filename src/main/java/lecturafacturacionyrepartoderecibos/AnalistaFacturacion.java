package lecturafacturacionyrepartoderecibos;

import java.util.ArrayList;
import java.util.List;

public class AnalistaFacturacion {

	private CalculoConsumo calculoConsumo;
	private final List<String> observaciones = new ArrayList<>();

	public AnalistaFacturacion() {
		this.calculoConsumo = new CalculoConsumo();
	}

	public AnalistaFacturacion(CalculoConsumo calculoConsumo) {
		this.calculoConsumo = calculoConsumo;
	}

	public boolean controlarPreFacturacion() {
		if (calculoConsumo == null) {
			observaciones.add("No existe cálculo de consumo cargado para pre-facturación.");
			return false;
		}
		if (!calculoConsumo.validar()) {
			derivarObservacionASupervisor(calculoConsumo);
			return false;
		}
		return true;
	}

	public boolean verificarConsistenciaConsumo(LecturaMedidor lectura, CalculoConsumo calculo) {
		if (lectura == null || calculo == null) {
			observaciones.add("Lectura o cálculo nulo durante verificación de consistencia.");
			return false;
		}
		this.calculoConsumo = calculo;
		if (!lectura.validar()) {
			observaciones.add("Lectura inválida o medidor no coincidente: " + lectura.getIdLectura());
			return false;
		}
		if (!calculo.verificarConsistenciaMinima()) {
			derivarObservacionASupervisor(calculo);
			return false;
		}
		return true;
	}

	public void emitirRecibosPeriodo() {
		if (!controlarPreFacturacion()) {
			return;
		}
		Recibo recibo = new Recibo();
		recibo.generarEstructuraRecibo(this.calculoConsumo);
	}

	public void derivarObservacionASupervisor(CalculoConsumo calculo) {
		String detalle = (calculo != null)
				? String.format("Observación derivada a Supervisor de Mediciones (Lect. Ant: %.2f, Lect. Act: %.2f)",
						calculo.getLecturaAnterior(), calculo.getLecturaActual())
				: "Observación derivada sin detalle de cálculo.";
		observaciones.add(detalle);
	}

	public String generarReporteErrores() {
		if (observaciones.isEmpty()) {
			return "Pre-facturación conforme: sin observaciones pendientes.";
		}
		return String.join("\n", observaciones);
	}

	public List<Recibo> emitirRecibosPeriodo(PeriodoFacturacion periodo) {
		List<Recibo> recibosEmitidos = new ArrayList<>();
		if (periodo == null || !periodo.esPeriodoValidoAproximado30Dias()) {
			observaciones.add("Periodo de facturación inválido (debe ser de aproximadamente 30 días).");
			return recibosEmitidos;
		}
		if (controlarPreFacturacion()) {
			Recibo recibo = new Recibo();
			recibo.generarEstructuraRecibo(this.calculoConsumo);
			if (recibo.estaAptoParaReparto()) {
				recibosEmitidos.add(recibo);
			}
		}
		return recibosEmitidos;
	}

	public CalculoConsumo getCalculoConsumo() { return calculoConsumo; }
	public void setCalculoConsumo(CalculoConsumo calculoConsumo) { this.calculoConsumo = calculoConsumo; }
}