package lecturafacturacionyrepartoderecibos;

public interface CalculableTarifario {

	public abstract double calcularConsumoNeto(double lecturaAnterior, double lecturaActual, double factor);

	public abstract double calcularCargosFijosYMantenimiento();

	public abstract double calcularDescuentoFOSE(double consumoKwh, double cargoTotal);

	public abstract double calcularIGV(double subtotal);

}
