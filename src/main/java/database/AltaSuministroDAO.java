package database;

import altanuevosuministro.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AltaSuministroDAO {

    public static class SolicitudFila {
        public final SolicitudSuministro solicitud;
        public final int idCliente;
        public final String idContrato;
        public final String idOrdenInstalacion;
        public final String estadoInstalacion;
        public final String idActa;
        public final String numeroMedidorActa;

        public SolicitudFila(SolicitudSuministro solicitud, int idCliente,
                             String idContrato, String idOrdenInstalacion,
                             String estadoInstalacion, String idActa, String numeroMedidorActa) {
            this.solicitud = solicitud;
            this.idCliente = idCliente;
            this.idContrato = idContrato;
            this.idOrdenInstalacion = idOrdenInstalacion;
            this.estadoInstalacion = estadoInstalacion;
            this.idActa = idActa;
            this.numeroMedidorActa = numeroMedidorActa;
        }
    }

    // 1. Registrar nueva solicitud consultando estado real del Predio en SQL Server
    public void registrarSolicitud(SolicitudSuministro sol, int idClienteLogueado) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                int idClienteFinal = idClienteLogueado > 0
                        ? idClienteLogueado
                        : obtenerOCrearCliente(con, sol.getCliente());

                String sqlUpdCli = """
                        UPDATE clientes
                        SET tipo_cliente = ?,
                            carta_poder_legalizada = ?,
                            contrato_arrendamiento_legalizado = ?
                        WHERE id_persona = ?
                        """;
                try (PreparedStatement ps = con.prepareStatement(sqlUpdCli)) {
                    ps.setString(1, sol.getCliente().getTipoCliente().name());
                    ps.setBoolean(2, sol.getCliente().isCartaPoderLegalizada());
                    ps.setBoolean(3, sol.getCliente().isContratoArrendamientoLegalizado());
                    ps.setInt(4, idClienteFinal);
                    ps.executeUpdate();
                }

                String codigoPredioFinal = resolverPredioEnBD(con, sol.getPredio());

                String sqlSol = """
                        INSERT INTO solicitudes_suministro (id_solicitud, id_cliente, codigo_predio, estado, fecha_solicitud, detalle_etapa)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = con.prepareStatement(sqlSol)) {
                    ps.setString(1, sol.getIdSolicitud());
                    ps.setInt(2, idClienteFinal);
                    ps.setString(3, codigoPredioFinal);
                    ps.setString(4, sol.getEstado().name());
                    ps.setDate(5, Date.valueOf(sol.getFechaSolicitud()));
                    ps.setString(6, sol.getDetalleEtapa());
                    ps.executeUpdate();
                }

                Expediente exp = sol.getExpediente();
                String sqlExp = "INSERT INTO expedientes (id_expediente, id_solicitud, completo) VALUES (?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlExp)) {
                    ps.setString(1, exp.getIdExpediente());
                    ps.setString(2, sol.getIdSolicitud());
                    ps.setBoolean(3, exp.isCompleto());
                    ps.executeUpdate();
                }

                String sqlDoc = "INSERT INTO documentos (id_expediente, tipo, estado_validacion, ruta_archivo) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlDoc)) {
                    for (Documento d : exp.getDocumentos()) {
                        ps.setString(1, exp.getIdExpediente());
                        ps.setString(2, d.getTipo().name());
                        ps.setString(3, d.getEstadoValidacion());
                        ps.setString(4, d.getRutaArchivo());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 2. Listar todas las solicitudes con sus relaciones desde SQL Server
    public List<SolicitudFila> listarSolicitudes(String filtroDniCliente) {
        List<SolicitudFila> lista = new ArrayList<>();
        String sql = """
                SELECT s.id_solicitud, s.estado, s.fecha_solicitud, s.detalle_etapa,
                       c.id_persona, c.codigo_suministro, c.tipo_cliente, c.estado_servicio,
                       c.carta_poder_legalizada, c.contrato_arrendamiento_legalizado,
                       p.nombres, p.apellidos, p.tipo_documento, p.numero_documento_identidad,
                       p.direccion AS dir_persona, p.telefono, p.tipo_persona, p.correo,
                       pr.codigo_predio, pr.direccion AS dir_predio, pr.tiene_deuda_pendiente,
                       pr.tiene_suministro_activo, pr.es_factible,
                       e.id_expediente,
                       ct.id_contrato,
                       oi.id_orden_instalacion, oi.estado_instalacion,
                       ai.id_acta, ai.numero_medidor_instalado
                FROM solicitudes_suministro s
                INNER JOIN clientes c ON s.id_cliente = c.id_persona
                INNER JOIN personas p ON c.id_persona = p.id_persona
                INNER JOIN predios pr ON s.codigo_predio = pr.codigo_predio
                INNER JOIN expedientes e ON e.id_solicitud = s.id_solicitud
                LEFT JOIN contratos ct ON ct.id_solicitud = s.id_solicitud
                LEFT JOIN ordenes_instalacion oi ON oi.id_solicitud = s.id_solicitud
                LEFT JOIN actas_instalacion ai ON ai.id_orden_instalacion = oi.id_orden_instalacion
                """ + (filtroDniCliente != null ? " WHERE p.numero_documento_identidad = ?" : "") + """
                ORDER BY s.fecha_solicitud DESC, s.id_solicitud DESC
                """;

        try (Connection con = ConexionDB.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (filtroDniCliente != null) {
                ps.setString(1, filtroDniCliente);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Cliente cli = new Cliente(
                            rs.getString("nombres"),
                            rs.getString("apellidos"),
                            TipoDocumento.valueOf(rs.getString("tipo_documento")),
                            rs.getString("numero_documento_identidad"),
                            rs.getString("dir_persona"),
                            rs.getString("telefono"),
                            TipoPersona.valueOf(rs.getString("tipo_persona")),
                            rs.getString("correo"),
                            CondicionCliente.valueOf(rs.getString("tipo_cliente"))
                    );
                    cli.setCodigoSuministro(rs.getString("codigo_suministro"));
                    cli.setEstadoServicio(rs.getString("estado_servicio"));
                    cli.setCartaPoderLegalizada(rs.getBoolean("carta_poder_legalizada"));
                    cli.setContratoArrendamientoLegalizado(rs.getBoolean("contrato_arrendamiento_legalizado"));

                    Predio predio = new Predio(
                            rs.getString("codigo_predio"),
                            rs.getString("dir_predio"),
                            rs.getBoolean("tiene_deuda_pendiente"),
                            rs.getBoolean("tiene_suministro_activo")
                    );
                    predio.setEsFactible(rs.getBoolean("es_factible"));

                    String idExp = rs.getString("id_expediente");
                    Expediente exp = new Expediente(idExp);
                    cargarDocumentosExpediente(con, exp);

                    SolicitudSuministro sol = new SolicitudSuministro(
                            rs.getString("id_solicitud"),
                            cli,
                            predio,
                            EstadoSolicitud.valueOf(rs.getString("estado")),
                            exp,
                            rs.getDate("fecha_solicitud").toLocalDate()
                    );
                    sol.setDetalleEtapa(rs.getString("detalle_etapa"));

                    lista.add(new SolicitudFila(
                            sol,
                            rs.getInt("id_persona"),
                            rs.getString("id_contrato"),
                            rs.getString("id_orden_instalacion"),
                            rs.getString("estado_instalacion"),
                            rs.getString("id_acta"),
                            rs.getString("numero_medidor_instalado")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error listando solicitudes: " + e.getMessage());
        }
        return lista;
    }

    // 3. Subsanar documentos observados (Cliente)
    public void subsanarDocumentos(SolicitudSuministro sol, List<Documento> nuevosDocs) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                String idExp = sol.getExpediente().getIdExpediente();
                try (PreparedStatement psDel = con.prepareStatement("DELETE FROM documentos WHERE id_expediente = ?")) {
                    psDel.setString(1, idExp);
                    psDel.executeUpdate();
                }

                String sqlIns = "INSERT INTO documentos (id_expediente, tipo, estado_validacion, ruta_archivo) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psIns = con.prepareStatement(sqlIns)) {
                    for (Documento d : nuevosDocs) {
                        psIns.setString(1, idExp);
                        psIns.setString(2, d.getTipo().name());
                        psIns.setString(3, d.getEstadoValidacion());
                        psIns.setString(4, d.getRutaArchivo());
                        psIns.addBatch();
                    }
                    psIns.executeBatch();
                }

                actualizarEstadoSolicitud(con, sol.getIdSolicitud(), EstadoSolicitud.PENDIENTE.name(),
                        "1B. Documentos subsanados - Pendiente nueva revisión");
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 4. Actualizar estado y etapa (Revisión Auxiliar / Factibilidad Supervisor)
    public void actualizarEstadoYFactibilidad(String idSolicitud, String codigoPredio,
                                              EstadoSolicitud estado, String detalleEtapa,
                                              Boolean esFactible, boolean expedienteCompleto) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                actualizarEstadoSolicitud(con, idSolicitud, estado.name(), detalleEtapa);

                try (PreparedStatement psExp = con.prepareStatement(
                        "UPDATE expedientes SET completo = ? WHERE id_solicitud = ?")) {
                    psExp.setBoolean(1, expedienteCompleto);
                    psExp.setString(2, idSolicitud);
                    psExp.executeUpdate();
                }

                if (esFactible != null) {
                    try (PreparedStatement psPre = con.prepareStatement(
                            "UPDATE predios SET es_factible = ? WHERE codigo_predio = ?")) {
                        psPre.setBoolean(1, esFactible);
                        psPre.setString(2, codigoPredio);
                        psPre.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 5. Registrar Contrato y actualizar titular si es inquilino (HU-05)
    public void registrarContrato(SolicitudSuministro sol, int idCliente, Contrato contrato,
                                  String nuevoTitularSiInquilino, String detalleEtapa) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                if (nuevoTitularSiInquilino != null && !nuevoTitularSiInquilino.isBlank()) {
                    try (PreparedStatement psPer = con.prepareStatement(
                            "UPDATE personas SET nombres = ?, apellidos = '(Propietario Titular)' WHERE id_persona = ?")) {
                        psPer.setString(1, nuevoTitularSiInquilino.trim());
                        psPer.setInt(2, idCliente);
                        psPer.executeUpdate();
                    }
                }

                String sqlCtr = """
                        INSERT INTO contratos (id_contrato, id_solicitud, fecha_inicio, opcion_tarifaria, potencia_contratada)
                        VALUES (?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement psCtr = con.prepareStatement(sqlCtr)) {
                    psCtr.setString(1, contrato.getIdContrato());
                    psCtr.setString(2, sol.getIdSolicitud());
                    psCtr.setDate(3, Date.valueOf(LocalDate.now()));
                    psCtr.setString(4, contrato.getOpcionTarifaria());
                    psCtr.setDouble(5, contrato.getPotenciaContratada());
                    psCtr.executeUpdate();
                }

                actualizarEstadoSolicitud(con, sol.getIdSolicitud(), sol.getEstado().name(), detalleEtapa);
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 6. Programar Orden de Instalación desde Contratista real en BD (HU-06)
    public void programarOrdenInstalacion(String idSolicitud, String detalleEtapa) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                String codContratistaFinal = obtenerContratistaInstalacionBD(con);

                try (PreparedStatement psDelActa = con.prepareStatement(
                        "DELETE FROM actas_instalacion WHERE id_orden_instalacion IN (SELECT id_orden_instalacion FROM ordenes_instalacion WHERE id_solicitud = ?)")) {
                    psDelActa.setString(1, idSolicitud);
                    psDelActa.executeUpdate();
                }
                try (PreparedStatement psDelOrd = con.prepareStatement(
                        "DELETE FROM ordenes_instalacion WHERE id_solicitud = ?")) {
                    psDelOrd.setString(1, idSolicitud);
                    psDelOrd.executeUpdate();
                }

                String idOrden = "ORD-" + (int) (Math.random() * 90000 + 10000);
                String sqlOrd = """
                        INSERT INTO ordenes_instalacion (id_orden_instalacion, id_solicitud, codigo_contratista, estado_instalacion)
                        VALUES (?, ?, ?, 'PROGRAMADA')
                        """;
                try (PreparedStatement ps = con.prepareStatement(sqlOrd)) {
                    ps.setString(1, idOrden);
                    ps.setString(2, idSolicitud);
                    ps.setString(3, codContratistaFinal);
                    ps.executeUpdate();
                }

                actualizarEstadoSolicitud(con, idSolicitud, EstadoSolicitud.APROBADA.name(), detalleEtapa);
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 7. Ejecutar Instalación y registrar Acta (Contratista)
    public void registrarActaInstalacion(String idSolicitud, String idOrden, ActaInstalacion acta, String detalleEtapa) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement psOrd = con.prepareStatement(
                        "UPDATE ordenes_instalacion SET estado_instalacion = 'EJECUTADA' WHERE id_orden_instalacion = ?")) {
                    psOrd.setString(1, idOrden);
                    psOrd.executeUpdate();
                }

                String sqlActa = """
                        INSERT INTO actas_instalacion (id_acta, id_orden_instalacion, numero_medidor_instalado, fecha_instalacion, es_conforme, observaciones)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """;
                try (PreparedStatement ps = con.prepareStatement(sqlActa)) {
                    ps.setString(1, acta.getIdActa());
                    ps.setString(2, idOrden);
                    ps.setString(3, acta.getNumeroMedidorInstalado());
                    ps.setDate(4, Date.valueOf(LocalDate.now()));
                    ps.setBoolean(5, acta.isEsConforme());
                    ps.setString(6, "Instalación de acometida y medidor ejecutada.");
                    ps.executeUpdate();
                }

                actualizarEstadoSolicitud(con, idSolicitud, EstadoSolicitud.APROBADA.name(), detalleEtapa);
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 8A. Inspección NO CONFORME (RN-06)
    public void registrarInspeccionNoConforme(String idSolicitud, String idOrden, String detalleEtapa) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE ordenes_instalacion SET estado_instalacion = 'NO_CONFORME' WHERE id_orden_instalacion = ?")) {
                    ps.setString(1, idOrden);
                    ps.executeUpdate();
                }
                actualizarEstadoSolicitud(con, idSolicitud, EstadoSolicitud.APROBADA.name(), detalleEtapa);
                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 8B. Inspección CONFORME y Habilitación de Medidor + Suministro + Cliente (RN-07 / HU-07)
    public String habilitarSuministroEnBD(SolicitudSuministro sol, int idCliente, String idOrden,
                                          String numeroMedidor, double lecturaInicial, String detalleEtapa) throws SQLException {
        String numSuministro = "SUM-" + (int) (Math.random() * 900000 + 100000);
        String medidorFinal = (numeroMedidor != null && !numeroMedidor.isBlank())
                ? numeroMedidor.trim()
                : "MED-" + (int) (Math.random() * 900000 + 100000);

        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                // 1. Marcar orden de instalación como CONFORME
                try (PreparedStatement psOrd = con.prepareStatement(
                        "UPDATE ordenes_instalacion SET estado_instalacion = 'CONFORME' WHERE id_orden_instalacion = ?")) {
                    psOrd.setString(1, idOrden);
                    psOrd.executeUpdate();
                }

                // 2. Insertar Medidor si no existe aún
                String sqlMed = """
                        IF NOT EXISTS (SELECT 1 FROM medidores WHERE numero_medidor = ?)
                        INSERT INTO medidores (numero_medidor, marca, lectura_inicial, estado, factor_medidor, lectura_anterior, lectura_actual)
                        VALUES (?, 'ELSTER-A150', ?, 'ACTIVO', 1.00, ?, ?)
                        """;
                try (PreparedStatement psMed = con.prepareStatement(sqlMed)) {
                    psMed.setString(1, medidorFinal);
                    psMed.setString(2, medidorFinal);
                    psMed.setDouble(3, lecturaInicial);
                    psMed.setDouble(4, lecturaInicial);
                    psMed.setDouble(5, lecturaInicial);
                    psMed.executeUpdate();
                }

                // 3. Obtener opción tarifaria del contrato
                String tarifa = "BT5B Residencial";
                try (PreparedStatement psTar = con.prepareStatement(
                        "SELECT opcion_tarifaria FROM contratos WHERE id_solicitud = ?")) {
                    psTar.setString(1, sol.getIdSolicitud());
                    try (ResultSet rs = psTar.executeQuery()) {
                        if (rs.next()) tarifa = rs.getString(1);
                    }
                }

                // 4. Insertar Suministro activo
                String sqlSum = """
                        INSERT INTO suministros (numero_suministro, id_cliente_titular, codigo_predio, numero_medidor, opcion_tarifaria, fecha_inicio_contrato, estado_suministro, acumulado_deuda_vencida)
                        VALUES (?, ?, ?, ?, ?, ?, 'ACTIVO', 0)
                        """;
                try (PreparedStatement psSum = con.prepareStatement(sqlSum)) {
                    psSum.setString(1, numSuministro);
                    psSum.setInt(2, idCliente);
                    psSum.setString(3, sol.getPredio().getCodigoPredio());
                    psSum.setString(4, medidorFinal);
                    psSum.setString(5, tarifa);
                    psSum.setDate(6, Date.valueOf(LocalDate.now()));
                    psSum.executeUpdate();
                }

                // 5. Actualizar Predio -> tiene_suministro_activo = 1
                try (PreparedStatement psPre = con.prepareStatement(
                        "UPDATE predios SET tiene_suministro_activo = 1 WHERE codigo_predio = ?")) {
                    psPre.setString(1, sol.getPredio().getCodigoPredio());
                    psPre.executeUpdate();
                }

                // 6. Actualizar Cliente -> asignar codigo_suministro y estado ACTIVO
                try (PreparedStatement psCli = con.prepareStatement(
                        "UPDATE clientes SET codigo_suministro = ?, estado_servicio = 'ACTIVO' WHERE id_persona = ?")) {
                    psCli.setString(1, numSuministro);
                    psCli.setInt(2, idCliente);
                    psCli.executeUpdate();
                }

                // 7. Finalizar Solicitud
                String etapaFinal = (detalleEtapa != null)
                        ? detalleEtapa
                        : "FINALIZADA - Suministro " + numSuministro + " HABILITADO";
                actualizarEstadoSolicitud(con, sol.getIdSolicitud(), EstadoSolicitud.FINALIZADA.name(), etapaFinal);

                con.commit();
                return numSuministro;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // --- Métodos auxiliares internos ---
    private String resolverPredioEnBD(Connection con, Predio predioNuevo) throws SQLException {
        String sqlBuscar = "SELECT codigo_predio FROM predios WHERE LOWER(LTRIM(RTRIM(direccion))) = LOWER(LTRIM(RTRIM(?)))";
        try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
            ps.setString(1, predioNuevo.getDireccion());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("codigo_predio");
                }
            }
        }

        String sqlPredio = """
                INSERT INTO predios (codigo_predio, direccion, tiene_deuda_pendiente, tiene_suministro_activo, es_factible)
                VALUES (?, ?, 0, 0, 1)
                """;
        try (PreparedStatement ps = con.prepareStatement(sqlPredio)) {
            ps.setString(1, predioNuevo.getCodigoPredio());
            ps.setString(2, predioNuevo.getDireccion());
            ps.executeUpdate();
        }
        return predioNuevo.getCodigoPredio();
    }

    private void actualizarEstadoSolicitud(Connection con, String idSolicitud, String estado, String detalleEtapa) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "UPDATE solicitudes_suministro SET estado = ?, detalle_etapa = ? WHERE id_solicitud = ?")) {
            ps.setString(1, estado);
            ps.setString(2, detalleEtapa);
            ps.setString(3, idSolicitud);
            ps.executeUpdate();
        }
    }

    private void cargarDocumentosExpediente(Connection con, Expediente exp) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT tipo, estado_validacion, ruta_archivo FROM documentos WHERE id_expediente = ?")) {
            ps.setString(1, exp.getIdExpediente());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    exp.agregarDocumento(new Documento(
                            TipoDocumento.valueOf(rs.getString("tipo")),
                            rs.getString("estado_validacion"),
                            rs.getString("ruta_archivo")
                    ));
                }
            }
        }
    }

    private int obtenerOCrearCliente(Connection con, Cliente c) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(
                "SELECT id_persona FROM personas WHERE numero_documento_identidad = ?")) {
            ps.setString(1, c.getNumeroDocumentoIdentidad());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idPer = rs.getInt(1);
                    try (PreparedStatement psCli = con.prepareStatement("SELECT 1 FROM clientes WHERE id_persona = ?")) {
                        psCli.setInt(1, idPer);
                        try (ResultSet rsC = psCli.executeQuery()) {
                            if (rsC.next()) return idPer;
                        }
                    }
                    insertarFilaCliente(con, idPer, c);
                    return idPer;
                }
            }
        }

        String sqlPer = """
                INSERT INTO personas (id_rol, nombres, apellidos, tipo_documento, numero_documento_identidad, direccion, telefono, tipo_persona, correo)
                VALUES (2, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        int nuevoId;
        try (PreparedStatement ps = con.prepareStatement(sqlPer, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombres());
            ps.setString(2, c.getApellidos());
            ps.setString(3, c.getTipoDocumento().name());
            ps.setString(4, c.getNumeroDocumentoIdentidad());
            ps.setString(5, c.getDireccion());
            ps.setString(6, c.getTelefono());
            ps.setString(7, c.getTipoPersona().name());
            ps.setString(8, c.getCorreo());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                nuevoId = keys.getInt(1);
            }
        }
        insertarFilaCliente(con, nuevoId, c);
        return nuevoId;
    }

    private void insertarFilaCliente(Connection con, int idPersona, Cliente c) throws SQLException {
        String sql = """
                INSERT INTO clientes (id_persona, usuario_cliente, clave, tipo_cliente, carta_poder_legalizada, contrato_arrendamiento_legalizado)
                VALUES (?, ?, '123456', ?, ?, ?)
                """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPersona);
            ps.setString(2, c.getNumeroDocumentoIdentidad());
            ps.setString(3, c.getTipoCliente().name());
            ps.setBoolean(4, c.isCartaPoderLegalizada());
            ps.setBoolean(5, c.isContratoArrendamientoLegalizado());
            ps.executeUpdate();
        }
    }

    private String obtenerContratistaInstalacionBD(Connection con) throws SQLException {
        String sql = """
                SELECT TOP 1 t.codigo_trabajador
                FROM trabajadores t
                INNER JOIN personas p ON t.id_persona = p.id_persona
                INNER JOIN roles r ON p.id_rol = r.id_rol
                WHERE r.nombre_rol = 'Contratista de instalación'
                """;
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getString(1);
        }
        throw new SQLException("No existe ningún trabajador con el rol 'Contratista de instalación' registrado en la base de datos.");
    }
}