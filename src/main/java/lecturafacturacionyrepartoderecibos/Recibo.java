package lecturafacturacionyrepartoderecibos;

import java.time.LocalDate;

public class Recibo {

	private String nroRecibo;

	private LocalDate fechaEmision;

	private LocalDate fechaVencimiento;

	private double consumoKwh;

	private double promedioConsumo6Meses;

	private String importesHistoricos2Meses;

	private double importeTotal;

	private EstadoRecibo estado;

	private double interesMoratorio;

	private double recargoCorte;

	public boolean esCargoFijo;

	private CalculoConsumo calculoConsumo;

	public void generarEstructuraRecibo(CalculoConsumo calculo) {

	}

	public void bloquearPorObservacion(String motivo) {

	}

	public boolean estaAptoParaReparto() {
		return false;
	}

	public String obtenerDetalleFactura() {
		return null;
	}

	public double calcularInteresesYMoras() {
		return 0;
	}

	public void cambiarEstadoPago(String estado) {

	}

}
