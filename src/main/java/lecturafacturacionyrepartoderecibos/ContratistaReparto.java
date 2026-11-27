package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.Trabajador;

public class ContratistaReparto extends Trabajador {

    public ContratistaReparto(String codigoTrabajador, String cargo) {
        super(codigoTrabajador, cargo);
    }

    public ContratistaReparto(String codigoTrabajador, String cargo, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, cargo, nombres, apellidos, dni);
    }

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
