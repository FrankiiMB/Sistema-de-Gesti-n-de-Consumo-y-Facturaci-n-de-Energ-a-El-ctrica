package lecturafacturacionyrepartoderecibos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeriodoFacturacion {

	private String idPeriodo;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private int diasPeriodo;
	private EstadoPeriodo estado;
	private final List<String> rutasGeneradas = new ArrayList<>();

	public PeriodoFacturacion() {
		this.estado = resolverEstado("ABIERTO", "PROGRAMADO", "EN_CURSO");
	}

	public PeriodoFacturacion(String idPeriodo, LocalDate fechaInicio, LocalDate fechaFin) {
		this();
		this.idPeriodo = idPeriodo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		if (fechaInicio != null && fechaFin != null) {
			this.diasPeriodo = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
		}
	}

	public void generarRutasYBloques() {
		rutasGeneradas.clear();
		rutasGeneradas.add("RUTA-01 | BLOQUE-A (Centro Chimbote)");
		rutasGeneradas.add("RUTA-02 | BLOQUE-B (Nuevo Chimbote)");
		rutasGeneradas.add("RUTA-03 | BLOQUE-C (Zona Industrial)");
		if (estado == null) {
			estado = resolverEstado("ABIERTO", "PROGRAMADO");
		}
	}

	public boolean esPeriodoValidoAproximado30Dias() {
		if (fechaInicio != null && fechaFin != null) {
			this.diasPeriodo = (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin);
		}
		return this.diasPeriodo >= 27 && this.diasPeriodo <= 33;
	}

	private EstadoPeriodo resolverEstado(String... nombres) {
		EstadoPeriodo[] valores = EstadoPeriodo.values();
		for (String n : nombres) {
			for (EstadoPeriodo v : valores) {
				if (v.name().equalsIgnoreCase(n)) return v;
			}
		}
		return valores.length > 0 ? valores[0] : null;
	}

	// Getters y Setters
	public String getIdPeriodo() { return idPeriodo; }
	public void setIdPeriodo(String idPeriodo) { this.idPeriodo = idPeriodo; }
	public LocalDate getFechaInicio() { return fechaInicio; }
	public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
	public LocalDate getFechaFin() { return fechaFin; }
	public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
	public int getDiasPeriodo() { return diasPeriodo; }
	public void setDiasPeriodo(int diasPeriodo) { this.diasPeriodo = diasPeriodo; }
	public EstadoPeriodo getEstado() { return estado; }
	public void setEstado(EstadoPeriodo estado) { this.estado = estado; }
	public List<String> getRutasGeneradas() { return Collections.unmodifiableList(rutasGeneradas); }
}