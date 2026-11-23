package altanuevosuministro;

public class OrdenInstalacion {

    private SolicitudSuministro solicitud;
    private ContratistaInstalacion contratista;
    private EstadoInstalacion estadoInstalacion;

    public OrdenInstalacion(SolicitudSuministro solicitud, ContratistaInstalacion contratista) {
        this.solicitud = solicitud;
        this.contratista = contratista;
        this.estadoInstalacion = EstadoInstalacion.PROGRAMADA;
    }

    public void generarOrden() {
        this.estadoInstalacion = EstadoInstalacion.PROGRAMADA;
    }

    public void registrarResultado(boolean conforme) {
        this.estadoInstalacion = conforme ? EstadoInstalacion.CONFORME : EstadoInstalacion.NO_CONFORME;
    }

    public SolicitudSuministro getSolicitud() { return solicitud; }
    public EstadoInstalacion getEstadoInstalacion() { return estadoInstalacion; }
    public void setEstadoInstalacion(EstadoInstalacion estado) { this.estadoInstalacion = estado; }
}