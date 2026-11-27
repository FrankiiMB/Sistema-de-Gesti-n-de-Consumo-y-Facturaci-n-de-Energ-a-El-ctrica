package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.IValidable;

public class CalculoConsumo implements CalculableTarifario, IValidable {

	private static final double TARIFA_KWH_BT5B = 0.72;
	private static final double CARGO_FIJO_BASE = 4.50;
	private static final double MANTENIMIENTO_BASE = 4.00;
	private static final double ALUMBRADO_PUBLICO_BASE = 6.80;
	private static final double TASA_IGV = 0.18;
	private static final double LIMITE_CONSUMO_ANOMALO_KWH = 1500.0;
	private static final double LIMITE_FOSE_KWH = 140.0;

	private double lecturaAnterior;
	private double lecturaActual;
	private double factorMedidor = 1.0;
	private double consumoNetoKwh;
	private double cargoFijo = CARGO_FIJO_BASE;
	private double mantenimientoRepocicion = MANTENIMIENTO_BASE;
	private double alumbradoPublico = ALUMBRADO_PUBLICO_BASE;
	private double igv;
	private double descuentoFose;
	private boolean esConsumoAnomalo;
	private boolean cambioMedidorAutorizado;

	public CalculoConsumo() {
	}

	public CalculoConsumo(double lecturaAnterior, double lecturaActual, double factorMedidor) {
		this.lecturaAnterior = lecturaAnterior;
		this.lecturaActual = lecturaActual;
		this.factorMedidor = factorMedidor > 0 ? factorMedidor : 1.0;
	}

	@Override
	public double calcularConsumoNeto(double lecturaAnterior, double lecturaActual, double factor) {
		this.lecturaAnterior = lecturaAnterior;
		this.lecturaActual = lecturaActual;
		this.factorMedidor = factor > 0 ? factor : 1.0;

		if (lecturaActual < lecturaAnterior && !cambioMedidorAutorizado) {
			this.esConsumoAnomalo = true;
			this.consumoNetoKwh = 0.0;
			return 0.0;
		}

		double diferencia = (lecturaActual >= lecturaAnterior)
				? (lecturaActual - lecturaAnterior)
				: lecturaActual; // Vuelta de dial o medidor nuevo en 0

		this.consumoNetoKwh = diferencia * this.factorMedidor;
		this.esConsumoAnomalo = this.consumoNetoKwh > LIMITE_CONSUMO_ANOMALO_KWH;
		return this.consumoNetoKwh;
	}

	@Override
	public double calcularCargosFijosYMantenimiento() {
		if (this.cargoFijo <= 0) {
			this.cargoFijo = CARGO_FIJO_BASE;
		}
		if (this.mantenimientoRepocicion <= 0) {
			this.mantenimientoRepocicion = MANTENIMIENTO_BASE;
		}
		return this.cargoFijo + this.mantenimientoRepocicion;
	}

	@Override
	public double calcularDescuentoFOSE(double consumoKwh, double cargoTotal) {
		if (consumoKwh > 0 && consumoKwh <= 30.0) {
			this.descuentoFose = cargoTotal * 0.50;
		} else if (consumoKwh > 30.0 && consumoKwh <= LIMITE_FOSE_KWH) {
			this.descuentoFose = cargoTotal * 0.25;
		} else {
			this.descuentoFose = 0.0;
		}
		return this.descuentoFose;
	}

	@Override
	public double calcularIGV(double subtotal) {
		this.igv = Math.max(0.0, subtotal) * TASA_IGV;
		return this.igv;
	}

	public double calcularTotalFactura() {
		double consumo = calcularConsumoNeto(this.lecturaAnterior, this.lecturaActual, this.factorMedidor);
		double cargosFijos = calcularCargosFijosYMantenimiento();
		if (this.alumbradoPublico <= 0) {
			this.alumbradoPublico = ALUMBRADO_PUBLICO_BASE;
		}

		double importeEnergia = consumo * TARIFA_KWH_BT5B;
		double descuento = calcularDescuentoFOSE(consumo, importeEnergia);
		double subtotal = (importeEnergia - descuento) + cargosFijos + this.alumbradoPublico;
		double impuestoIgv = calcularIGV(subtotal);

		return Math.round((subtotal + impuestoIgv) * 100.0) / 100.0;
	}

	public boolean verificarConsistenciaMinima() {
            if (factorMedidor <= 0) {
                return false;
            }
            if (lecturaActual < 0 || lecturaAnterior < 0) {
                return false;
            }
            if (lecturaActual < lecturaAnterior && !esCambioMedidor()) {
                return false;
            }
            return !esConsumoAnomalo;
        }

	public boolean esCambioMedidor() {
		return cambioMedidorAutorizado || (lecturaActual < lecturaAnterior && lecturaAnterior > 0 && lecturaActual >= 0);
	}

	@Override
	public boolean validar() {
		return verificarConsistenciaMinima() && calcularCargosFijosYMantenimiento() > 0;
	}

	// Getters y Setters
	public double getLecturaAnterior() { return lecturaAnterior; }
	public void setLecturaAnterior(double lecturaAnterior) { this.lecturaAnterior = lecturaAnterior; }
	public double getLecturaActual() { return lecturaActual; }
	public void setLecturaActual(double lecturaActual) { this.lecturaActual = lecturaActual; }
	public double getFactorMedidor() { return factorMedidor; }
	public void setFactorMedidor(double factorMedidor) { this.factorMedidor = factorMedidor; }
	public double getConsumoNetoKwh() { return consumoNetoKwh; }
	public double getCargoFijo() { return cargoFijo; }
	public void setCargoFijo(double cargoFijo) { this.cargoFijo = cargoFijo; }
	public double getMantenimientoRepocicion() { return mantenimientoRepocicion; }
	public void setMantenimientoRepocicion(double mantenimientoRepocicion) { this.mantenimientoRepocicion = mantenimientoRepocicion; }
	public double getAlumbradoPublico() { return alumbradoPublico; }
	public void setAlumbradoPublico(double alumbradoPublico) { this.alumbradoPublico = alumbradoPublico; }
	public double getIgv() { return igv; }
	public double getDescuentoFose() { return descuentoFose; }
	public boolean isEsConsumoAnomalo() { return esConsumoAnomalo; }
	public void setEsConsumoAnomalo(boolean esConsumoAnomalo) { this.esConsumoAnomalo = esConsumoAnomalo; }
	public boolean isCambioMedidorAutorizado() { return cambioMedidorAutorizado; }
	public void setCambioMedidorAutorizado(boolean cambioMedidorAutorizado) { this.cambioMedidorAutorizado = cambioMedidorAutorizado; }
}