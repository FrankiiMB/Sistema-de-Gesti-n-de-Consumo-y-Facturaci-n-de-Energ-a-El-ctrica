package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.IValidable;
import java.util.UUID;

public class OrdenReparto implements IValidable {

	private String idReparto;
	private String ruta;
	private ResultadoReparto resultado;
	private boolean requiereCargoFirmado;
	private boolean cargoFirmadoRegistrado;
	private boolean direccionVerificada;

	public OrdenReparto() {
		this.idReparto = "REP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		this.resultado = resolverResultado("PENDIENTE", "PROGRAMADO");
	}

	public OrdenReparto(String ruta, boolean requiereCargoFirmado) {
		this();
		this.ruta = ruta;
		this.requiereCargoFirmado = requiereCargoFirmado;
	}

	public void registrarEntrega() {
		if (requiereCargoFirmado && !cargoFirmadoRegistrado) {
			throw new IllegalStateException("Este suministro (entidad pública/bancaria) exige cargo firmado para registrar entrega.");
		}
		this.resultado = resolverResultado("ENTREGADO", "EXITOSO", "CONFORME");
	}

	public void programarRepartoPosterior(String nuevaDireccion) {
		if (nuevaDireccion != null && !nuevaDireccion.isBlank()) {
			this.direccionVerificada = true;
		}
		this.resultado = resolverResultado("REPROGRAMADO", "FALLIDO", "PENDIENTE");
	}

	public boolean repartirRecibosYRegistrarResultado(OrdenReparto reparto, boolean requiereCargo) {
		if (reparto == null) return false;
		reparto.setRequiereCargoFirmado(requiereCargo);
		if (requiereCargo && !reparto.isCargoFirmadoRegistrado()) {
			reparto.programarRepartoPosterior(null);
			return false;
		}
		reparto.registrarEntrega();
		return true;
	}

	@Override
	public boolean validar() {
		boolean rutaValida = ruta != null && !ruta.isBlank();
		boolean cargoOk = !requiereCargoFirmado || cargoFirmadoRegistrado;
		return rutaValida && cargoOk;
	}

	private ResultadoReparto resolverResultado(String... nombres) {
		ResultadoReparto[] valores = ResultadoReparto.values();
		for (String n : nombres) {
			for (ResultadoReparto v : valores) {
				if (v.name().equalsIgnoreCase(n)) return v;
			}
		}
		return valores.length > 0 ? valores[0] : null;
	}

	// Getters y Setters
	public String getIdReparto() { return idReparto; }
	public void setIdReparto(String idReparto) { this.idReparto = idReparto; }
	public String getRuta() { return ruta; }
	public void setRuta(String ruta) { this.ruta = ruta; }
	public ResultadoReparto getResultado() { return resultado; }
	public boolean isRequiereCargoFirmado() { return requiereCargoFirmado; }
	public void setRequiereCargoFirmado(boolean requiereCargoFirmado) { this.requiereCargoFirmado = requiereCargoFirmado; }
	public boolean isCargoFirmadoRegistrado() { return cargoFirmadoRegistrado; }
	public void setCargoFirmadoRegistrado(boolean cargoFirmadoRegistrado) { this.cargoFirmadoRegistrado = cargoFirmadoRegistrado; }
	public boolean isDireccionVerificada() { return direccionVerificada; }
	public void setDireccionVerificada(boolean direccionVerificada) { this.direccionVerificada = direccionVerificada; }
}