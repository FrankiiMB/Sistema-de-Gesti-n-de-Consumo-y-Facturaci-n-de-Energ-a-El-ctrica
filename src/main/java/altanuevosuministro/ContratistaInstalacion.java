package altanuevosuministro;

import java.time.LocalDate;

public class ContratistaInstalacion extends Trabajador {

    private String empresaContratista;

    public ContratistaInstalacion(String empresaContratista) {
        super("K-0001", "Contratista de instalación");
        this.empresaContratista = empresaContratista;
    }

    public ContratistaInstalacion(String codigo, String empresaContratista, String ruc) {
        super(codigo, "Contratista de instalación", empresaContratista, "", ruc);
        this.empresaContratista = empresaContratista;
    }

    public ActaInstalacion ejecutarInstalacionAcometidaMedidor(OrdenInstalacion orden) {
        if (orden != null) {
            orden.setEstadoInstalacion(EstadoInstalacion.EJECUTADA);
        }
        String numMedidor = "MED-" + (int)(Math.random() * 90000 + 10000);
        ActaInstalacion acta = new ActaInstalacion("ACT-" + (int)(Math.random() * 900 + 100),
                numMedidor, LocalDate.now(), true, "Instalación de acometida y medidor ejecutada.");
        acta.emitirActa();
        return acta;
    }

    public void enviarActaConformidad(ActaInstalacion acta) {
        if (acta != null) acta.emitirActa();
    }

    public String getEmpresaContratista() { return empresaContratista; }

    @Override
    public String toString() {
        return "Contratista  ·  Contratista de instalación";
    }
}