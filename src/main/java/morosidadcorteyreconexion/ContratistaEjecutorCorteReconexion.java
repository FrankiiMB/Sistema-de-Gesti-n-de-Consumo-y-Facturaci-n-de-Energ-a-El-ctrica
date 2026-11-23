package morosidadcorteyreconexion;


import altanuevosuministro.OrdenServicio;

public class ContratistaEjecutorCorteReconexion implements EjecutarOrden {

	public boolean realizarCorte(OrdenCorte o) {
		return false;
	}

	public boolean realizarReconexion(OrdenReconexion o) {
		return false;
	}


	/**
	 * @see EjecutarOrden#ejecutar(OrdenServicio)
	 */
	public boolean ejecutar(OrdenServicio o) {
		return false;
	}


	/**
	 * @see EjecutarOrden#comunicarResultado(OrdenServicio)
	 */
	public void comunicarResultado(OrdenServicio o) {

	}

}
