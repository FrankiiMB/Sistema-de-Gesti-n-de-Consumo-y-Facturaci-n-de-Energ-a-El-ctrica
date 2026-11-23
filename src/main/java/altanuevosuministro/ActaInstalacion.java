package altanuevosuministro;

import java.time.LocalDate;

public class ActaInstalacion {

    private String idActa;
    private String numeroMedidorInstalado;
    private LocalDate fechaInstalacion;
    private boolean esConforme;
    private String observaciones;

    public ActaInstalacion(String idActa, String numeroMedidorInstalado, LocalDate fechaInstalacion,
                           boolean esConforme, String observaciones) {
        this.idActa = idActa;
        this.numeroMedidorInstalado = numeroMedidorInstalado;
        this.fechaInstalacion = fechaInstalacion;
        this.esConforme = esConforme;
        this.observaciones = observaciones;
    }

    public void emitirActa() {
        System.out.println("Acta emitida: " + idActa + " | Medidor: " + numeroMedidorInstalado);
    }

    public String getIdActa() { return idActa; }
    public String getNumeroMedidorInstalado() { return numeroMedidorInstalado; }
    public boolean isEsConforme() { return esConforme; }
}