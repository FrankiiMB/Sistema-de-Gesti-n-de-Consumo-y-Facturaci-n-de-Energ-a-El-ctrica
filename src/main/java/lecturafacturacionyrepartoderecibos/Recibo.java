package lecturafacturacionyrepartoderecibos;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
	private String motivoBloqueo;

	public Recibo() {
		this.nroRecibo = "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		this.fechaEmision = LocalDate.now();
		this.fechaVencimiento = this.fechaEmision.plusDays(15);
		this.estado = resolverEstado("EMITIDO", "PENDIENTE", "GENERADO");
	}

	public void generarEstructuraRecibo(CalculoConsumo calculo) {
		if (calculo == null) {
			throw new IllegalArgumentException("Se requiere un cálculo de consumo para generar el recibo.");
		}
		this.calculoConsumo = calculo;
		if (!calculo.validar()) {
			bloquearPorObservacion("Inconsistencia detectada en la pre-facturación.");
			return;
		}
		this.fechaEmision = LocalDate.now();
		this.fechaVencimiento = this.fechaEmision.plusDays(15);
		this.consumoKwh = calculo.getConsumoNetoKwh();
		this.esCargoFijo = (this.consumoKwh == 0.0);
		this.importeTotal = calculo.calcularTotalFactura();
		if (this.importesHistoricos2Meses == null || this.importesHistoricos2Meses.isBlank()) {
			this.importesHistoricos2Meses = "Mes -1: S/ 0.00 | Mes -2: S/ 0.00";
		}
		this.estado = resolverEstado("EMITIDO", "PENDIENTE");
	}

	public void bloquearPorObservacion(String motivo) {
		this.motivoBloqueo = (motivo != null && !motivo.isBlank()) ? motivo : "Observación pendiente de revisión";
		this.estado = resolverEstado("OBSERVADO", "BLOQUEADO", "ANULADO");
	}

	public boolean estaAptoParaReparto() {
		boolean estadoBloqueado = estado != null && (
				estado.name().equalsIgnoreCase("OBSERVADO") ||
				estado.name().equalsIgnoreCase("BLOQUEADO") ||
				estado.name().equalsIgnoreCase("ANULADO")
		);
		return !estadoBloqueado && importeTotal > 0 && calculoConsumo != null && calculoConsumo.validar();
	}

	public String obtenerDetalleFactura() {
		double fose = calculoConsumo != null ? calculoConsumo.getDescuentoFose() : 0.0;
		double igv = calculoConsumo != null ? calculoConsumo.getIgv() : 0.0;
		return String.format(
				"Recibo N° %s | Emisión: %s | Vencimiento: %s | Consumo: %.2f kWh | Prom. 6M: %.2f kWh | Hist. 2M: [%s] | FOSE: -S/ %.2f | IGV (18%%): S/ %.2f | Total: S/ %.2f | Estado: %s",
				nroRecibo, fechaEmision, fechaVencimiento, consumoKwh, promedioConsumo6Meses,
				importesHistoricos2Meses, fose, igv, importeTotal, estado
		);
	}

	public double calcularInteresesYMoras() {
		if (fechaVencimiento == null || !LocalDate.now().isAfter(fechaVencimiento)) {
			this.interesMoratorio = 0.0;
			return 0.0;
		}
		long diasVencidos = ChronoUnit.DAYS.between(fechaVencimiento, LocalDate.now());
		double interesCompensatorio = importeTotal * 0.0005 * diasVencidos;
		// RN-18: Recargo por mora del 15% a partir del 10° día de vencimiento
		double mora = diasVencidos >= 10 ? (importeTotal * 0.15) : 0.0;
		this.interesMoratorio = Math.round((interesCompensatorio + mora) * 100.0) / 100.0;
		return this.interesMoratorio;
	}

	public void cambiarEstadoPago(String nuevoEstado) {
		if (nuevoEstado == null || nuevoEstado.isBlank()) return;
		this.estado = resolverEstado(nuevoEstado.trim());
	}

	private EstadoRecibo resolverEstado(String... nombres) {
		EstadoRecibo[] valores = EstadoRecibo.values();
		for (String n : nombres) {
			for (EstadoRecibo v : valores) {
				if (v.name().equalsIgnoreCase(n)) return v;
			}
		}
		return valores.length > 0 ? valores[0] : null;
	}

	// Getters y Setters
	public String getNroRecibo() { return nroRecibo; }
	public void setNroRecibo(String nroRecibo) { this.nroRecibo = nroRecibo; }
	public LocalDate getFechaEmision() { return fechaEmision; }
	public LocalDate getFechaVencimiento() { return fechaVencimiento; }
	public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
	public double getConsumoKwh() { return consumoKwh; }
	public double getPromedioConsumo6Meses() { return promedioConsumo6Meses; }
	public void setPromedioConsumo6Meses(double promedioConsumo6Meses) { this.promedioConsumo6Meses = promedioConsumo6Meses; }
	public String getImportesHistoricos2Meses() { return importesHistoricos2Meses; }
	public void setImportesHistoricos2Meses(String importesHistoricos2Meses) { this.importesHistoricos2Meses = importesHistoricos2Meses; }
	public double getImporteTotal() { return importeTotal; }
	public EstadoRecibo getEstado() { return estado; }
	public double getInteresMoratorio() { return interesMoratorio; }
	public double getRecargoCorte() { return recargoCorte; }
	public void setRecargoCorte(double recargoCorte) { this.recargoCorte = recargoCorte; }
	public CalculoConsumo getCalculoConsumo() { return calculoConsumo; }
	public String getMotivoBloqueo() { return motivoBloqueo; }
}