package lecturafacturacionyrepartoderecibos;


import altanuevosuministro.IValidable;

public class CalculoConsumo implements CalculableTarifario, IValidable {

	private double lecturaAnterior;

	private double lecturaActual;

	private double factorMedidor;

	private double consumoNetoKwh;

	private double cargoFijo;

	private double mantenimientoRepocicion;

	private double alumbradoPublico;

	private double igv;

	private double descuentoFose;

	private boolean esConsumoAnomalo;

	public double calcularTotalFactura() {
		return 0;
	}

	public boolean verificarConsistenciaMinima() {
		return false;
	}

	public boolean esCambioMedidor() {
		return false;
	}

	public double calcularConsumoNeto(double lecturaAnterior, double lecturaActual, double factor) {
		return 0;
	}

	public double calcularCargosFijosYMantenimiento() {
		return 0;
	}

	public double calcularDescuentoFOSE(double consumoKwh, double cargoTotal) {
		return 0;
	}

	public double calcularIGV(double subtotal) {
		return 0;
	}

	public boolean validar() {
		return false;
	}

}
