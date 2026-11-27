package morosidadcorteyreconexion;

import altanuevosuministro.Suministro;
import lecturafacturacionyrepartoderecibos.Recibo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsistenteRecaudacion {

	private final List<String> historialConciliacion = new ArrayList<>();

	public void registrarPago(Recibo r) {
		if (r == null) {
			throw new IllegalArgumentException("El recibo no puede ser nulo.");
		}
		r.cambiarEstadoPago("PAGADO");
		historialConciliacion.add("Pago conciliado para recibo: " + r.getNroRecibo());
	}

	public void actualizarEstadoCuentaYSaldo(Suministro s) {
		if (s == null) {
			return;
		}
		historialConciliacion.add("Estado de cuenta y saldo actualizados en tiempo real para suministro: " + s);
	}

	public void reversarPago(Recibo r, String motivo) {
		if (r == null) {
			return;
		}
		String detalleMotivo = (motivo != null && !motivo.isBlank()) ? motivo.trim() : "Cheque rebotado / operación rechazada";
		// HU-19: Si el banco informa cheque sin fondos, el pago se reversa y vuelve a estado vencido/pendiente
		r.cambiarEstadoPago("VENCIDO");
		r.calcularInteresesYMoras();
		historialConciliacion.add("Pago reversado en recibo " + r.getNroRecibo() + " - Motivo: " + detalleMotivo);
	}

	public List<String> getHistorialConciliacion() {
		return Collections.unmodifiableList(historialConciliacion);
	}
}