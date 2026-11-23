package lecturafacturacionyrepartoderecibos;


import altanuevosuministro.IValidable;

public class OrdenReparto implements IValidable {

	private String idReparto;

	private String ruta;

	private ResultadoReparto resultado;

	private boolean requiereCargoFirmado;

	private boolean cargoFirmadoRegistrado;

	private boolean direccionVerificada;

	public void registrarEntrega() {

	}

	public void programarRepartoPosterior(String nuevaDireccion) {

	}

	public boolean repartirRecibosYRegistrarResultado(OrdenReparto reparto, boolean requiereCargo) {
		return false;
	}

	public boolean validar() {
		return false;
	}

}
