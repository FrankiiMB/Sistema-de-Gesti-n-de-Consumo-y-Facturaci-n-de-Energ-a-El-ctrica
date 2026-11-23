package altanuevosuministro;

import java.time.LocalDate;

public class SolicitudSuministro {

    private String idSolicitud;
    private Cliente cliente;
    private Predio predio;
    private EstadoSolicitud estado;
    private Expediente expediente;
    private LocalDate fechaSolicitud;
    private String detalleEtapa;

    public SolicitudSuministro(String idSolicitud, Cliente cliente, Predio predio,
                               EstadoSolicitud estado, Expediente expediente, LocalDate fechaSolicitud) {
        this.idSolicitud = idSolicitud;
        this.cliente = cliente;
        this.predio = predio;
        this.estado = estado;
        this.expediente = expediente;
        this.fechaSolicitud = fechaSolicitud;
        this.detalleEtapa = "1. Recibida - Pendiente revisión Auxiliar";
    }

    public void actualizarEstado(EstadoSolicitud nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public String getIdSolicitud() { return idSolicitud; }
    public Cliente getCliente() { return cliente; }
    public Predio getPredio() { return predio; }
    public EstadoSolicitud getEstado() { return estado; }
    public Expediente getExpediente() { return expediente; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public String getDetalleEtapa() { return detalleEtapa; }
    public void setDetalleEtapa(String detalleEtapa) { this.detalleEtapa = detalleEtapa; }
}