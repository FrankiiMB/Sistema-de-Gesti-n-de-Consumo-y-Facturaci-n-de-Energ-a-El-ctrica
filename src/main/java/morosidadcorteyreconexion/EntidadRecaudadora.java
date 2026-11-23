package morosidadcorteyreconexion;


import lecturafacturacionyrepartoderecibos.Recibo;

public class EntidadRecaudadora implements Recaudable {

	private String ruc;

	private String nombre;

	public boolean recibirPago(Recibo r) {
		return false;
	}

	public void comunicarOperacion(Recibo r) {

	}

	public boolean validarPagoCompleto(double montoPagado, Recibo r) {
		return false;
	}

}
