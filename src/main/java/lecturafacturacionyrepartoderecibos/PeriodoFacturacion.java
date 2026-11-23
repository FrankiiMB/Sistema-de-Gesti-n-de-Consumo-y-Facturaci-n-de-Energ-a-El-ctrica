package lecturafacturacionyrepartoderecibos;

import java.time.LocalDate;

public class PeriodoFacturacion {

	private String idPeriodo;

	private LocalDate fechaInicio;

	private LocalDate fechaFin;

	private int diasPeriodo;

	private EstadoPeriodo estado;

	public void generarRutasYBloques() {

	}

	public boolean esPeriodoValidoAproximado30Dias() {
		return false;
	}

}
