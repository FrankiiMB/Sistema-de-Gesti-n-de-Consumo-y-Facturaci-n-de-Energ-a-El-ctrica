package morosidadcorteyreconexion;

import java.util.UUID;

public class ConvenioPago {

	private String idConvenio;
	private int cuotasTotales;
	private double montoFinanciado;
	private boolean vigente;
	private int cuotasPagadas;
	private double montoCuota;

	public ConvenioPago() {
		this.idConvenio = "CONV-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
	}

	public ConvenioPago(double montoFinanciado, int cuotasTotales) {
		this();
		if (montoFinanciado <= 0 || cuotasTotales <= 0) {
			throw new IllegalArgumentException("El monto financiado y el número de cuotas deben ser mayores a cero.");
		}
		this.montoFinanciado = montoFinanciado;
		this.cuotasTotales = cuotasTotales;
		this.montoCuota = Math.round((montoFinanciado / cuotasTotales) * 100.0) / 100.0;
		this.cuotasPagadas = 0;
		this.vigente = true;
	}

	public boolean validarVigencia() {
		if (!vigente || cuotasTotales <= 0 || montoFinanciado <= 0) {
			return false;
		}
		return cuotasPagadas <= cuotasTotales;
	}

	public void registrarPagoCuota() {
		if (!vigente) {
			throw new IllegalStateException("El convenio de pago no se encuentra vigente.");
		}
		if (cuotasPagadas < cuotasTotales) {
			cuotasPagadas++;
		}
		if (cuotasPagadas >= cuotasTotales) {
			vigente = false; // Convenio cancelado en su totalidad
		}
	}

	public void marcarIncumplido() {
		this.vigente = false;
	}

	// Getters y Setters
	public String getIdConvenio() { return idConvenio; }
	public void setIdConvenio(String idConvenio) { this.idConvenio = idConvenio; }
	public int getCuotasTotales() { return cuotasTotales; }
	public void setCuotasTotales(int cuotasTotales) { this.cuotasTotales = cuotasTotales; }
	public double getMontoFinanciado() { return montoFinanciado; }
	public void setMontoFinanciado(double montoFinanciado) { this.montoFinanciado = montoFinanciado; }
	public boolean isVigente() { return vigente; }
	public void setVigente(boolean vigente) { this.vigente = vigente; }
	public int getCuotasPagadas() { return cuotasPagadas; }
	public void setCuotasPagadas(int cuotasPagadas) { this.cuotasPagadas = cuotasPagadas; }
	public double getMontoCuota() { return montoCuota; }
	public void setMontoCuota(double montoCuota) { this.montoCuota = montoCuota; }
}