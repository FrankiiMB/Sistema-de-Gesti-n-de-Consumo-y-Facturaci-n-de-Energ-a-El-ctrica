package altanuevosuministro;

import morosidadcorteyreconexion.ConvenioPago;
import lecturafacturacionyrepartoderecibos.Recibo;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona {

    private String codigoSuministro;
    private CondicionCliente tipoCliente;
    private String estadoServicio;
    private boolean cartaPoderLegalizada;
    private boolean contratoArrendamientoLegalizado;
    private List<Recibo> recibosAsociados = new ArrayList<>();

    public Cliente() {
        super();
    }

    public Cliente(String nombres, String apellidos, TipoDocumento tipoDocumento,
                   String numeroDocumentoIdentidad, String direccion, String telefono,
                   TipoPersona tipoPersona, String correo, CondicionCliente tipoCliente) {
        super(nombres, apellidos, tipoDocumento, numeroDocumentoIdentidad, direccion, telefono, tipoPersona, correo);
        this.tipoCliente = tipoCliente;
        this.estadoServicio = "SIN_SUMINISTRO";
    }

    @Override
    public String mostrarDatos() {
        return super.mostrarDatos() + " - Condición: " + tipoCliente;
    }

    @Override
    public boolean validarDocumento() {
        return super.validarDocumento();
    }

    public SolicitudSuministro solicitarSuministro(Predio predio, List<Documento> documentos) {
        Expediente exp = new Expediente("EXP-" + System.currentTimeMillis() % 10000);
        for (Documento doc : documentos) {
            exp.agregarDocumento(doc);
        }
        return new SolicitudSuministro("SOL-" + System.currentTimeMillis() % 10000,
                this, predio, EstadoSolicitud.PENDIENTE, exp, LocalDate.now());
    }

    public void presentarDocumentosCorregidos(Expediente expediente, List<Documento> documentos) {
        if (expediente != null && documentos != null) {
            for (Documento doc : documentos) {
                expediente.agregarDocumento(doc);
            }
        }
    }

    public void recibirRecibo(Recibo recibo) {
        if (recibo != null) recibosAsociados.add(recibo);
    }

    public void solicitarNuevaLectura() { }
    public double consultarDeuda(Suministro s) { return 0; }
    public boolean pagarRecibo(Recibo r) { return false; }
    public ConvenioPago solicitarConvenioPago() { return null; }
    public boolean solicitarReconexion(Suministro s) { return false; }

    // Getters y Setters
    public String getCodigoSuministro() { return codigoSuministro; }
    public void setCodigoSuministro(String codigoSuministro) { this.codigoSuministro = codigoSuministro; }
    public CondicionCliente getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(CondicionCliente tipoCliente) { this.tipoCliente = tipoCliente; }
    public String getEstadoServicio() { return estadoServicio; }
    public void setEstadoServicio(String estadoServicio) { this.estadoServicio = estadoServicio; }
    public boolean isCartaPoderLegalizada() { return cartaPoderLegalizada; }
    public void setCartaPoderLegalizada(boolean cartaPoderLegalizada) { this.cartaPoderLegalizada = cartaPoderLegalizada; }
    public boolean isContratoArrendamientoLegalizado() { return contratoArrendamientoLegalizado; }
    public void setContratoArrendamientoLegalizado(boolean contratoArrendamientoLegalizado) { this.contratoArrendamientoLegalizado = contratoArrendamientoLegalizado; }

    @Override
    public String toString() {
        return "Cliente  ·  Cliente / Titular del suministro";
    }
}