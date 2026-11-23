package morosidadcorteyreconexion;


import lecturafacturacionyrepartoderecibos.Recibo;

public interface Recaudable {

	public abstract boolean recibirPago(Recibo r);

	public abstract void comunicarOperacion(Recibo r);

}
