package altanuevosuministro;

import java.time.LocalDate;
import morosidadcorteyreconexion.EjecutarOrden;
import morosidadcorteyreconexion.EstadoOrden;

public abstract class OrdenServicio implements EjecutarOrden {

    private String idOrden;
    private LocalDate fechaProgramada;
    private EstadoOrden estado;

    public void programar(LocalDate f) { this.fechaProgramada = f; }
    public void cambiarEstado(String e) { }

    @Override
    public boolean ejecutar(OrdenServicio o) { return true; }

    @Override
    public void comunicarResultado(OrdenServicio o) { }
}