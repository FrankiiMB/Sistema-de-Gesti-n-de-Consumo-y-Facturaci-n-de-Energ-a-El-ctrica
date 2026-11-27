package morosidadcorteyreconexion;

import altanuevosuministro.OrdenServicio;
import altanuevosuministro.Suministro;
import java.time.LocalDate;

public class OrdenCorte extends OrdenServicio {

	private TipoCorte tipoCorte;
	private double lecturaEjecucion;
	private LocalDate fechaEjecucion;
	private Suministro suministro;
	private boolean ejecutada;
	private boolean anulada;
	private boolean etiquetaDejada;
	private double cargoAdministrativo;

	public OrdenCorte() {
		this.tipoCorte = resolverTipoCorte(0); // Por defecto Tipo I (Fusible)
	}

	public OrdenCorte(Suministro suministro, TipoCorte tipoCorte) {
		this.suministro = suministro;
		this.tipoCorte = tipoCorte != null ? tipoCorte : resolverTipoCorte(0);
	}

	public boolean validarEjecucion() {
		if (anulada || tipoCorte == null) {
			return false;
		}
		return ejecutada && fechaEjecucion != null && !fechaEjecucion.isAfter(LocalDate.now()) && lecturaEjecucion >= 0;
	}

	public void anularPorPagoPrevio() {
		if (!ejecutada) {
			this.anulada = true;
		}
	}

	public void escalarSiguienteTipoCorte() {
		TipoCorte[] niveles = TipoCorte.values();
		if (niveles.length == 0) return;
		if (this.tipoCorte == null) {
			this.tipoCorte = niveles[0];
			return;
		}
		int siguienteIndice = Math.min(this.tipoCorte.ordinal() + 1, niveles.length - 1);
		this.tipoCorte = niveles[siguienteIndice];
	}

	public static TipoCorte resolverTipoCorte(int indice) {
		TipoCorte[] niveles = TipoCorte.values();
		if (niveles.length == 0) return null;
		int idx = Math.max(0, Math.min(indice, niveles.length - 1));
		return niveles[idx];
	}

	// Getters y Setters
	public TipoCorte getTipoCorte() { return tipoCorte; }
	public void setTipoCorte(TipoCorte tipoCorte) { this.tipoCorte = tipoCorte; }
	public double getLecturaEjecucion() { return lecturaEjecucion; }
	public void setLecturaEjecucion(double lecturaEjecucion) { this.lecturaEjecucion = lecturaEjecucion; }
	public LocalDate getFechaEjecucion() { return fechaEjecucion; }
	public void setFechaEjecucion(LocalDate fechaEjecucion) { this.fechaEjecucion = fechaEjecucion; }
	public Suministro getSuministro() { return suministro; }
	public void setSuministro(Suministro suministro) { this.suministro = suministro; }
	public boolean isEjecutada() { return ejecutada; }
	public void setEjecutada(boolean ejecutada) { this.ejecutada = ejecutada; }
	public boolean isAnulada() { return anulada; }
	public boolean isEtiquetaDejada() { return etiquetaDejada; }
	public void setEtiquetaDejada(boolean etiquetaDejada) { this.etiquetaDejada = etiquetaDejada; }
	public double getCargoAdministrativo() { return cargoAdministrativo; }
	public void setCargoAdministrativo(double cargoAdministrativo) { this.cargoAdministrativo = cargoAdministrativo; }
}