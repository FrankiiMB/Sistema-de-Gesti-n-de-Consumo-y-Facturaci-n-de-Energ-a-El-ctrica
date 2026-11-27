package lecturafacturacionyrepartoderecibos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AsistenteFacturacion {

	private final List<OrdenLectura> resultadosOrganizados = new ArrayList<>();

	public List<OrdenLectura> programarCronogramaYOrdenes(PeriodoFacturacion periodo) {
		List<OrdenLectura> ordenes = new ArrayList<>();
		if (periodo == null || !periodo.esPeriodoValidoAproximado30Dias()) {
			return ordenes;
		}
		periodo.generarRutasYBloques();
		for (String rutaBloque : periodo.getRutasGeneradas()) {
			String[] partes = rutaBloque.split("\\|");
			String ruta = partes[0].trim();
			String bloque = partes.length > 1 ? partes[1].trim() : "BLOQUE-GENERAL";
			ordenes.add(new OrdenLectura(ruta, bloque));
		}
		return ordenes;
	}

	public void organizarResultadosLectura(OrdenLectura orden) {
		if (orden != null && !resultadosOrganizados.contains(orden)) {
			resultadosOrganizados.add(orden);
		}
	}

	public List<OrdenReparto> agruparRecibosEnBloquesYRutas(List<Recibo> recibos) {
		List<OrdenReparto> ordenesReparto = new ArrayList<>();
		if (recibos == null || recibos.isEmpty()) {
			return ordenesReparto;
		}
		int indice = 1;
		for (Recibo recibo : recibos) {
			if (recibo != null && recibo.estaAptoParaReparto()) {
				String rutaAsignada = "RUTA-" + String.format("%02d", ((indice - 1) % 3) + 1);
				ordenesReparto.add(new OrdenReparto(rutaAsignada, false));
				indice++;
			}
		}
		return ordenesReparto;
	}

	public void registrarFalloRepartoYCoordinar(OrdenReparto reparto) {
		if (reparto != null) {
			reparto.programarRepartoPosterior("Dirección verificada en sistema comercial");
		}
	}

	public List<OrdenLectura> getResultadosOrganizados() {
		return Collections.unmodifiableList(resultadosOrganizados);
	}
}