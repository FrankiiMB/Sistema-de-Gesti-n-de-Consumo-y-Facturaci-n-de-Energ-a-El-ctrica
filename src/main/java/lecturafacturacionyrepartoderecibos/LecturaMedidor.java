package lecturafacturacionyrepartoderecibos;


import altanuevosuministro.IValidable;
import java.time.LocalDate;

public class LecturaMedidor implements IValidable {

	private String idLectura;

	private String numeroMedidorLeido;

	private double valorRegistrado;

	private LocalDate fechaToma;

	private boolean esEstimada;

	private boolean esCorregida;

	private String justificacionEstimacion;

	public boolean validarCoincidenciaMedidor() {
		return false;
	}

	public void registrarEstimacion(double valor, String justificacion) {

	}

	public void marcarComoCorregida(double nuevoValor) {

	}

	public boolean validar() {
		return false;
	}

}
