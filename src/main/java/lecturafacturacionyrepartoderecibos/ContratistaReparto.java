package lecturafacturacionyrepartoderecibos;

public class ContratistaReparto {

	public boolean repartirRecibosYRegistrarResultado(OrdenReparto reparto) {
		if (reparto == null) {
			return false;
		}
		if (!reparto.validar()) {
			reparto.programarRepartoPosterior(null);
			return false;
		}
		reparto.registrarEntrega();
		return true;
	}

}