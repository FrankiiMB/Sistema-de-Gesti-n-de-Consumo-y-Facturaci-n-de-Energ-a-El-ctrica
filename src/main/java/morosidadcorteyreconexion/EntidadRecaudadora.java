package morosidadcorteyreconexion;

import lecturafacturacionyrepartoderecibos.Recibo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntidadRecaudadora implements Recaudable {

	private String ruc;
	private String nombre;
	private final List<String> operacionesComunicadas = new ArrayList<>();

	public EntidadRecaudadora() {
	}

	public EntidadRecaudadora(String ruc, String nombre) {
		this.ruc = ruc;
		this.nombre = nombre;
	}

	@Override
	public boolean recibirPago(Recibo r) {
		if (r == null) {
			return false;
		}
		double totalExigible = r.getImporteTotal() + r.calcularInteresesYMoras() + r.getRecargoCorte();
		if (totalExigible <= 0) {
			return false;
		}
		// RN-20: El CAR solo acepta el pago completo del recibo exigible
		if (!validarPagoCompleto(totalExigible, r)) {
			return false;
		}
		r.cambiarEstadoPago("PAGADO");
		comunicarOperacion(r);
		return true;
	}

	@Override
	public void comunicarOperacion(Recibo r) {
		if (r != null) {
			String entidad = (nombre != null && !nombre.isBlank()) ? nombre : "CAR";
			operacionesComunicadas.add("Operación comunicada por " + entidad + " para recibo " + r.getNroRecibo());
		}
	}

	public boolean validarPagoCompleto(double montoPagado, Recibo r) {
		if (r == null) {
			return false;
		}
		double deudaTotal = r.getImporteTotal() + r.calcularInteresesYMoras() + r.getRecargoCorte();
		// RN-20: No se admiten abonos parciales sobre recibos vencidos
		return Math.round(montoPagado * 100.0) >= Math.round(deudaTotal * 100.0);
	}

	// Getters y Setters
	public String getRuc() { return ruc; }
	public void setRuc(String ruc) { this.ruc = ruc; }
	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }
	public List<String> getOperacionesComunicadas() { return Collections.unmodifiableList(operacionesComunicadas); }
}