package morosidadcorteyreconexion;

import altanuevosuministro.OrdenServicio;
import altanuevosuministro.Suministro;
import java.time.LocalDate;

public class OrdenReconexion extends OrdenServicio {

	private Suministro suministro;
	private LocalDate fechaEjecucion;
	private double lecturaReconexion;
	private boolean ejecutada;
	private boolean autorizada;
	private double cargoReconexion = 25.00;

	public OrdenReconexion() {
	}

	public OrdenReconexion(Suministro suministro, boolean autorizada) {
		this.suministro = suministro;
		this.autorizada = autorizada;
	}

	public boolean validarEjecucion() {
		return autorizada && ejecutada && fechaEjecucion != null && !fechaEjecucion.isAfter(LocalDate.now());
	}

	// Getters y Setters
	public Suministro getSuministro() { return suministro; }
	public void setSuministro(Suministro suministro) { this.suministro = suministro; }
	public LocalDate getFechaEjecucion() { return fechaEjecucion; }
	public void setFechaEjecucion(LocalDate fechaEjecucion) { this.fechaEjecucion = fechaEjecucion; }
	public double getLecturaReconexion() { return lecturaReconexion; }
	public void setLecturaReconexion(double lecturaReconexion) { this.lecturaReconexion = lecturaReconexion; }
	public boolean isEjecutada() { return ejecutada; }
	public void setEjecutada(boolean ejecutada) { this.ejecutada = ejecutada; }
	public boolean isAutorizada() { return autorizada; }
	public void setAutorizada(boolean autorizada) { this.autorizada = autorizada; }
	public double getCargoReconexion() { return cargoReconexion; }
	public void setCargoReconexion(double cargoReconexion) { this.cargoReconexion = cargoReconexion; }
}