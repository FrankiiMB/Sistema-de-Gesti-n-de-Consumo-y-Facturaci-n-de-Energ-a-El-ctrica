package altanuevosuministro;

import java.time.LocalDate;
import java.util.List;

public class AuxiliarComercial extends Trabajador {

    public AuxiliarComercial(String codigoTrabajador, String cargo) {
        super(codigoTrabajador, cargo);
    }

    public AuxiliarComercial(String codigoTrabajador, String nombres, String apellidos, String dni) {
        super(codigoTrabajador, "Auxiliar Comercial", nombres, apellidos, dni);
    }

    public SolicitudSuministro recibirSolicitud(Cliente cliente, Predio predio, List<Documento> documentos) {
        Expediente exp = new Expediente("EXP-" + (int)(Math.random() * 900 + 100));
        if (documentos != null) {
            for (Documento doc : documentos) {
                exp.agregarDocumento(doc);
            }
        }
        return new SolicitudSuministro("SOL-" + (int)(Math.random() * 900 + 100),
                cliente, predio, EstadoSolicitud.PENDIENTE, exp, LocalDate.now());
    }

    public boolean verificarDocumentacionRequisitos(Expediente expediente, CondicionCliente condicion) {
        return expediente != null && expediente.validar(condicion);
    }

    public boolean verificarDocumentacionRequisitos(Expediente expediente) {
        return expediente != null && expediente.validar();
    }

    public boolean verificarPredio(Predio predio) {
        return predio != null && predio.validar();
    }

    public void firmarContrato(Contrato contrato) {
        if (contrato != null && contrato.validarPotenciaTarifa()) {
            System.out.println("Contrato firmado: " + contrato.getIdContrato());
        }
    }

    public boolean cobrarPresupuesto(double presupuesto) {
        return presupuesto > 0;
    }
}