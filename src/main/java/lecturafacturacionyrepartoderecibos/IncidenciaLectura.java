package lecturafacturacionyrepartoderecibos;

import java.time.LocalDateTime;
import java.util.UUID;

public class IncidenciaLectura {

	private String idIncidencia;
	private TipoIncidencia tipo;
	private String descripcionJustificacion;
	private LocalDateTime fechaRegistro;

	public IncidenciaLectura() {
		this.idIncidencia = "INC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		this.fechaRegistro = LocalDateTime.now();
	}

	public IncidenciaLectura(TipoIncidencia tipo, String descripcionJustificacion) {
		this();
		registrarExcepcionCampo(tipo, descripcionJustificacion);
	}

	public void registrarExcepcionCampo(TipoIncidencia tipo, String detalle) {
		if (tipo == null) {
			throw new IllegalArgumentException("Debe especificar el tipo de incidencia de lectura.");
		}
		if (detalle == null || detalle.isBlank()) {
			throw new IllegalArgumentException("La incidencia requiere una descripción o justificación documentada.");
		}
		this.tipo = tipo;
		this.descripcionJustificacion = detalle.trim();
		this.fechaRegistro = LocalDateTime.now();
	}

	public String getIdIncidencia() { return idIncidencia; }
	public void setIdIncidencia(String idIncidencia) { this.idIncidencia = idIncidencia; }
	public TipoIncidencia getTipo() { return tipo; }
	public void setTipo(TipoIncidencia tipo) { this.tipo = tipo; }
	public String getDescripcionJustificacion() { return descripcionJustificacion; }
	public void setDescripcionJustificacion(String descripcionJustificacion) { this.descripcionJustificacion = descripcionJustificacion; }
	public LocalDateTime getFechaRegistro() { return fechaRegistro; }
	public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}