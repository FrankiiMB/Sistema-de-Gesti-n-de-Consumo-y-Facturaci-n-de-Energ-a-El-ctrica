package altanuevosuministro;

import java.util.ArrayList;
import java.util.List;

public class Expediente implements IValidable {

    private String idExpediente;
    private boolean completo;
    private List<Documento> documentos = new ArrayList<>();

    public Expediente(String idExpediente) {
        this.idExpediente = idExpediente;
    }

    public void agregarDocumento(Documento doc) {
        if (doc != null) {
            documentos.add(doc);
        }
    }

    private boolean contieneTipoValido(TipoDocumento tipoBuscado) {
        return documentos.stream()
                .anyMatch(d -> d.getTipo() == tipoBuscado && d.validar());
    }

    // Regla RN-02: Validación según Propietario o Inquilino
    public boolean validar(CondicionCliente condicion) {
        boolean basicos = contieneTipoValido(TipoDocumento.CARTA_SOLICITUD)
                       && contieneTipoValido(TipoDocumento.TITULO_PROPIEDAD)
                       && contieneTipoValido(TipoDocumento.CROQUIS);

        if (condicion == CondicionCliente.INQUILINO) {
            this.completo = basicos
                    && contieneTipoValido(TipoDocumento.CARTA_PODER)
                    && contieneTipoValido(TipoDocumento.CONTRATO_ARRENDAMIENTO);
        } else {
            this.completo = basicos;
        }
        return this.completo;
    }

    @Override
    public boolean validar() {
        return validar(CondicionCliente.PROPIETARIO);
    }

    public String getIdExpediente() { return idExpediente; }
    public boolean isCompleto() { return completo; }
    public List<Documento> getDocumentos() { return documentos; }
}