package morosidadcorteyreconexion;


import altanuevosuministro.OrdenServicio;

public interface EjecutarOrden {

	public abstract boolean ejecutar(OrdenServicio o);

	public abstract void comunicarResultado(OrdenServicio o);

}
