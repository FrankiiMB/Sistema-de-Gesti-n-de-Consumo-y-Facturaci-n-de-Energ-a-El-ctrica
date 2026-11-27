package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.IValidable;
import java.time.LocalDate;
import java.util.UUID;

public class LecturaMedidor implements IValidable {

	private String idLectura;
	private String numeroMedidorLeido;
	private String numeroMedidorEsperado;
	private double valorRegistrado;
	private LocalDate fechaToma;
	private boolean esEstimada;
	private boolean esCorregida;
	private String justificacionEstimacion;

	public LecturaMedidor() {
		this.idLectura = "LEC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		this.fechaToma = LocalDate.now();
	}

	public LecturaMedidor(String numeroMedidorLeido, double valorRegistrado, LocalDate fechaToma) {
		this();
		this.numeroMedidorLeido = numeroMedidorLeido;
		this.valorRegistrado = valorRegistrado;
		this.fechaToma = fechaToma != null ? fechaToma : LocalDate.now();
	}

	public boolean validarCoincidenciaMedidor() {
		if (numeroMedidorLeido == null || numeroMedidorLeido.isBlank()) {
			return false;
		}
		if (numeroMedidorEsperado == null || numeroMedidorEsperado.isBlank()) {
			return true;
		}
		return numeroMedidorLeido.trim().equalsIgnoreCase(numeroMedidorEsperado.trim());
	}

	public void registrarEstimacion(double valor, String justificacion) {
		if (valor < 0) {
			throw new IllegalArgumentException("El valor estimado no puede ser negativo.");
		}
		if (justificacion == null || justificacion.isBlank()) {
			throw new IllegalArgumentException("Toda lectura estimada exige justificación documentada.");
		}
		this.valorRegistrado = valor;
		this.esEstimada = true;
		this.justificacionEstimacion = justificacion.trim();
		this.fechaToma = LocalDate.now();
	}

	public void marcarComoCorregida(double nuevoValor) {
		if (nuevoValor < 0) {
			throw new IllegalArgumentException("La lectura corregida no puede ser negativa.");
		}
		this.valorRegistrado = nuevoValor;
		this.esCorregida = true;
		this.fechaToma = LocalDate.now();
	}

	@Override
	public boolean validar() {
		boolean fechaValida = fechaToma != null && !fechaToma.isAfter(LocalDate.now());
		boolean valorValido = valorRegistrado >= 0;
		boolean estimacionValida = !esEstimada || (justificacionEstimacion != null && !justificacionEstimacion.isBlank());
		return validarCoincidenciaMedidor() && fechaValida && valorValido && estimacionValida;
	}

	// Getters y Setters
	public String getIdLectura() { return idLectura; }
	public void setIdLectura(String idLectura) { this.idLectura = idLectura; }
	public String getNumeroMedidorLeido() { return numeroMedidorLeido; }
	public void setNumeroMedidorLeido(String numeroMedidorLeido) { this.numeroMedidorLeido = numeroMedidorLeido; }
	public String getNumeroMedidorEsperado() { return numeroMedidorEsperado; }
	public void setNumeroMedidorEsperado(String numeroMedidorEsperado) { this.numeroMedidorEsperado = numeroMedidorEsperado; }
	public double getValorRegistrado() { return valorRegistrado; }
	public void setValorRegistrado(double valorRegistrado) { this.valorRegistrado = valorRegistrado; }
	public LocalDate getFechaToma() { return fechaToma; }
	public void setFechaToma(LocalDate fechaToma) { this.fechaToma = fechaToma; }
	public boolean isEsEstimada() { return esEstimada; }
	public boolean isEsCorregida() { return esCorregida; }
	public String getJustificacionEstimacion() { return justificacionEstimacion; }
}