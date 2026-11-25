package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public static class RolDTO {
        public int idRol;
        public String categoria;
        public String nombreRol;
        public String prefijo;

        public RolDTO(int idRol, String categoria, String nombreRol, String prefijo) {
            this.idRol = idRol;
            this.categoria = categoria;
            this.nombreRol = nombreRol;
            this.prefijo = prefijo;
        }

        @Override
        public String toString() {
            return categoria + "  ·  " + nombreRol;
        }
    }

    public static class UsuarioDTO {
        public int idPersona;
        public String codigoUsuario;
        public String clave;
        public int idRol;
        public String nombreRol;
        public String categoriaRol;
        public String nombres;
        public String apellidos;
        public String tipoPersona;
        public String tipoDocumento;
        public String numeroDocumentoIdentidad;
        public String codigoSuministro;
        public String condicionCliente;
        public String direccion;
        public String telefono;
        public String correo;

        // Soporte para doble perfil (Trabajador + Cliente)
        public boolean tienePerfilTrabajador;
        public boolean tienePerfilCliente;
        public String rolTrabajador;
        public String categoriaTrabajador;
        public String usuarioTrabajador;

        public String getNombreCompleto() {
            return nombres + (apellidos != null && !apellidos.isBlank() ? " " + apellidos : "");
        }

        public void activarModoCliente() {
            this.nombreRol = "Cliente / Titular del suministro";
            this.categoriaRol = "Cliente";
            this.codigoUsuario = (this.codigoSuministro != null && !this.codigoSuministro.isBlank())
                    ? this.codigoSuministro
                    : this.numeroDocumentoIdentidad;
        }

        public void activarModoTrabajador() {
            this.nombreRol = this.rolTrabajador;
            this.categoriaRol = this.categoriaTrabajador;
            if (this.usuarioTrabajador != null) this.codigoUsuario = this.usuarioTrabajador;
        }
    }

    // 1. Catálogo de roles con sus siglas de 3 letras
    public List<RolDTO> listarRoles() {
        return List.of(
                new RolDTO(1, "Sistema", "Administrador", "ADM"),
                new RolDTO(2, "Cliente", "Cliente / Titular del suministro", "CLI"),
                new RolDTO(3, "Trabajador", "Auxiliar Comercial", "AXC"),
                new RolDTO(4, "Trabajador", "Supervisor de Control Comercial", "SCC"),
                new RolDTO(5, "Trabajador", "Supervisor de Control de Mediciones", "SCM"),
                new RolDTO(6, "Trabajador", "Técnico Supervisor de Mediciones", "TSM"),
                new RolDTO(7, "Contratista", "Contratista de instalación", "CIN"),
                new RolDTO(8, "Trabajador", "Asistente de Facturación", "ASF"),
                new RolDTO(9, "Contratista", "Contratista de Lectura", "CLE"),
                new RolDTO(10, "Trabajador", "Analista de Facturación", "ANF"),
                new RolDTO(11, "Contratista", "Contratista de Reparto", "CRE"),
                new RolDTO(12, "Trabajador", "Supervisor de Cobranzas", "SCO"),
                new RolDTO(13, "Trabajador", "Técnico Supervisor Comercial", "TSC"),
                new RolDTO(14, "Contratista", "Contratista ejecutor de corte y reconexión", "CCR"),
                new RolDTO(15, "Trabajador", "Asistente de Recaudación", "ASR"),
                new RolDTO(16, "Externo", "Entidad Recaudadora / CAR", "CAR")
        );
    }

    // 2. Generar siguiente código correlativo para trabajador (Ej. AXC-001, AXC-002)
    public String generarSiguienteCodigoTrabajador(String prefijo) {
        int maxCorrelativo = 0;
        String sql = "SELECT codigo_trabajador FROM trabajadores WHERE codigo_trabajador LIKE ?";
        try (Connection con = ConexionDB.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, prefijo + "-%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cod = rs.getString(1);
                    int guion = cod.lastIndexOf('-');
                    if (guion != -1 && guion + 1 < cod.length()) {
                        try {
                            int num = Integer.parseInt(cod.substring(guion + 1));
                            if (num > maxCorrelativo) maxCorrelativo = num;
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error generando código de trabajador: " + e.getMessage());
        }
        return String.format("%s-%03d", prefijo, maxCorrelativo + 1);
    }

    // 3. Autenticación:
    //    - Trabajadores: DNI/RUC o Código de Trabajador
    //    - Clientes: DNI/RUC o Código de Suministro
    public UsuarioDTO autenticar(String identificador, String clave) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {

            // A) Buscar en Trabajadores
            String sqlTrab = """
                    SELECT p.id_persona, p.id_rol, r.nombre_rol, r.categoria,
                           p.nombres, p.apellidos, p.tipo_persona, p.tipo_documento,
                           p.numero_documento_identidad, p.direccion, p.telefono, p.correo,
                           t.codigo_trabajador, t.clave AS clave_trab,
                           c.id_persona AS id_cli, c.codigo_suministro, c.tipo_cliente
                    FROM trabajadores t
                    INNER JOIN personas p ON t.id_persona = p.id_persona
                    INNER JOIN roles r ON p.id_rol = r.id_rol
                    LEFT JOIN clientes c ON p.id_persona = c.id_persona
                    WHERE (p.numero_documento_identidad = ? OR t.codigo_trabajador = ?)
                      AND t.clave = ?
                    """;
            try (PreparedStatement ps = con.prepareStatement(sqlTrab)) {
                ps.setString(1, identificador);
                ps.setString(2, identificador);
                ps.setString(3, clave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UsuarioDTO u = mapearBase(rs);
                        u.codigoUsuario = rs.getString("codigo_trabajador");
                        u.clave = rs.getString("clave_trab");
                        u.nombreRol = rs.getString("nombre_rol");
                        u.categoriaRol = rs.getString("categoria");

                        u.tienePerfilTrabajador = true;
                        u.rolTrabajador = u.nombreRol;
                        u.categoriaTrabajador = u.categoriaRol;
                        u.usuarioTrabajador = u.codigoUsuario;

                        if (rs.getObject("id_cli") != null) {
                            u.tienePerfilCliente = true;
                            u.codigoSuministro = rs.getString("codigo_suministro");
                            u.condicionCliente = rs.getString("tipo_cliente");
                        }
                        return u;
                    }
                }
            }

            // B) Buscar en Clientes (solo por DNI/RUC o codigo_suministro)
            String sqlCli = """
                    SELECT p.id_persona, p.id_rol, r.nombre_rol AS rol_persona, r.categoria AS cat_persona,
                           p.nombres, p.apellidos, p.tipo_persona, p.tipo_documento,
                           p.numero_documento_identidad, p.direccion, p.telefono, p.correo,
                           c.codigo_suministro, c.clave AS clave_cli, c.tipo_cliente,
                           t.codigo_trabajador
                    FROM clientes c
                    INNER JOIN personas p ON c.id_persona = p.id_persona
                    INNER JOIN roles r ON p.id_rol = r.id_rol
                    LEFT JOIN trabajadores t ON p.id_persona = t.id_persona
                    WHERE (p.numero_documento_identidad = ? OR c.codigo_suministro = ?)
                      AND c.clave = ?
                    """;
            try (PreparedStatement ps = con.prepareStatement(sqlCli)) {
                ps.setString(1, identificador);
                ps.setString(2, identificador);
                ps.setString(3, clave);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UsuarioDTO u = mapearBase(rs);
                        u.codigoSuministro = rs.getString("codigo_suministro");
                        u.codigoUsuario = (u.codigoSuministro != null && !u.codigoSuministro.isBlank())
                                ? u.codigoSuministro
                                : u.numeroDocumentoIdentidad;
                        u.clave = rs.getString("clave_cli");
                        u.nombreRol = "Cliente / Titular del suministro";
                        u.categoriaRol = "Cliente";
                        u.condicionCliente = rs.getString("tipo_cliente");
                        u.tienePerfilCliente = true;

                        if (rs.getString("codigo_trabajador") != null) {
                            u.tienePerfilTrabajador = true;
                            u.usuarioTrabajador = rs.getString("codigo_trabajador");
                            u.rolTrabajador = rs.getString("rol_persona");
                            u.categoriaTrabajador = rs.getString("cat_persona");
                        }
                        return u;
                    }
                }
            }
        }
        return null;
    }

    // 4A. Registrar cuenta de Cliente (sin campo usuario_cliente)
    public boolean registrarCliente(String clave, String nombres, String apellidos,
                                    String tipoPersona, String tipoDoc, String numDoc,
                                    String condicionCliente, String direccion,
                                    String telefono, String correo) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                Integer idPersonaExistente = buscarIdPersonaPorDocumento(con, numDoc.trim());
                int idPersonaFinal;

                if (idPersonaExistente != null) {
                    if (existeEnClientes(con, idPersonaExistente)) {
                        throw new SQLException("Este documento ya tiene una cuenta de cliente registrada.", "23000");
                    }
                    idPersonaFinal = idPersonaExistente;
                    String sqlUpd = """
                            UPDATE personas
                            SET direccion = COALESCE(?, direccion),
                                telefono = COALESCE(?, telefono),
                                correo = COALESCE(?, correo)
                            WHERE id_persona = ?
                            """;
                    try (PreparedStatement psUpd = con.prepareStatement(sqlUpd)) {
                        psUpd.setString(1, limpiarOpcional(direccion));
                        psUpd.setString(2, limpiarOpcional(telefono));
                        psUpd.setString(3, limpiarOpcional(correo));
                        psUpd.setInt(4, idPersonaFinal);
                        psUpd.executeUpdate();
                    }
                } else {
                    idPersonaFinal = insertarPersona(con, 2, nombres, apellidos, tipoDoc, numDoc, direccion, telefono, tipoPersona, correo);
                }

                String sqlCli = "INSERT INTO clientes (id_persona, clave, tipo_cliente) VALUES (?, ?, ?)";
                try (PreparedStatement psCli = con.prepareStatement(sqlCli)) {
                    psCli.setInt(1, idPersonaFinal);
                    psCli.setString(2, clave);
                    psCli.setString(3, condicionCliente);
                    psCli.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 4B. Registrar Trabajador / Contratista / Administrador
    public boolean registrarTrabajador(String codigoTrabajador, String clave, int idRol, String nombres,
                                       String apellidos, String tipoPersona, String tipoDoc,
                                       String numDoc, String direccion, String telefono, String correo) throws SQLException {
        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try {
                int idPersonaFinal = insertarPersona(con, idRol, nombres, apellidos, tipoDoc, numDoc, direccion, telefono, tipoPersona, correo);
                String nombreCargo = obtenerNombreRol(con, idRol);

                String sqlTrab = """
                        INSERT INTO trabajadores (codigo_trabajador, id_persona, cargo, clave)
                        VALUES (?, ?, ?, ?)
                        """;
                try (PreparedStatement psTrab = con.prepareStatement(sqlTrab)) {
                    psTrab.setString(1, codigoTrabajador.trim());
                    psTrab.setInt(2, idPersonaFinal);
                    psTrab.setString(3, nombreCargo);
                    psTrab.setString(4, clave);
                    psTrab.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    private int insertarPersona(Connection con, int idRol, String nombres, String apellidos,
                                String tipoDoc, String numDoc, String direccion,
                                String telefono, String tipoPersona, String correo) throws SQLException {
        String sqlPersona = """
                INSERT INTO personas (id_rol, nombres, apellidos, tipo_documento,
                                      numero_documento_identidad, direccion, telefono, tipo_persona, correo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement psPer = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
            psPer.setInt(1, idRol);
            psPer.setString(2, nombres.trim());
            psPer.setString(3, limpiarOpcional(apellidos));
            psPer.setString(4, tipoDoc);
            psPer.setString(5, numDoc.trim());
            psPer.setString(6, limpiarOpcional(direccion));
            psPer.setString(7, limpiarOpcional(telefono));
            psPer.setString(8, tipoPersona);
            psPer.setString(9, limpiarOpcional(correo));
            psPer.executeUpdate();

            try (ResultSet keys = psPer.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("No se obtuvo id_persona.");
                return keys.getInt(1);
            }
        }
    }

    // 5. Actualizar datos de contacto y contraseña del Cliente
    public boolean actualizarDatosCliente(int idPersona, String direccion, String telefono,
                                          String correo, String nuevaClave) throws SQLException {
        String sqlPersona = "UPDATE personas SET direccion = ?, telefono = ?, correo = ? WHERE id_persona = ?";
        String sqlCliente = "UPDATE clientes SET clave = ? WHERE id_persona = ?";

        try (Connection con = ConexionDB.getInstancia().getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psPer = con.prepareStatement(sqlPersona);
                 PreparedStatement psCli = con.prepareStatement(sqlCliente)) {

                psPer.setString(1, limpiarOpcional(direccion));
                psPer.setString(2, limpiarOpcional(telefono));
                psPer.setString(3, limpiarOpcional(correo));
                psPer.setInt(4, idPersona);
                psPer.executeUpdate();

                psCli.setString(1, nuevaClave);
                psCli.setInt(2, idPersona);
                psCli.executeUpdate();

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    // 6. Listar todas las cuentas registradas
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_persona, p.id_rol, r.nombre_rol, r.categoria,
                       p.nombres, p.apellidos, p.tipo_persona, p.tipo_documento,
                       p.numero_documento_identidad, p.direccion, p.telefono, p.correo,
                       t.codigo_trabajador AS codigo_usuario,
                       NULL AS codigo_suministro
                FROM trabajadores t
                INNER JOIN personas p ON t.id_persona = p.id_persona
                INNER JOIN roles r ON p.id_rol = r.id_rol
                UNION ALL
                SELECT p.id_persona, 2 AS id_rol, 'Cliente / Titular del suministro' AS nombre_rol, 'Cliente' AS categoria,
                       p.nombres, p.apellidos, p.tipo_persona, p.tipo_documento,
                       p.numero_documento_identidad, p.direccion, p.telefono, p.correo,
                       COALESCE(c.codigo_suministro, p.numero_documento_identidad) AS codigo_usuario,
                       c.codigo_suministro
                FROM clientes c
                INNER JOIN personas p ON c.id_persona = p.id_persona
                ORDER BY id_persona DESC
                """;
        try (Connection con = ConexionDB.getInstancia().getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UsuarioDTO u = mapearBase(rs);
                u.nombreRol = rs.getString("nombre_rol");
                u.categoriaRol = rs.getString("categoria");
                u.codigoUsuario = rs.getString("codigo_usuario");
                u.codigoSuministro = rs.getString("codigo_suministro");
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    private UsuarioDTO mapearBase(ResultSet rs) throws SQLException {
        UsuarioDTO u = new UsuarioDTO();
        u.idPersona = rs.getInt("id_persona");
        u.idRol = rs.getInt("id_rol");
        u.nombres = rs.getString("nombres");
        u.apellidos = rs.getString("apellidos");
        u.tipoPersona = rs.getString("tipo_persona");
        u.tipoDocumento = rs.getString("tipo_documento");
        u.numeroDocumentoIdentidad = rs.getString("numero_documento_identidad");
        u.direccion = rs.getString("direccion");
        u.telefono = rs.getString("telefono");
        u.correo = rs.getString("correo");
        return u;
    }

    private Integer buscarIdPersonaPorDocumento(Connection con, String numDoc) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT id_persona FROM personas WHERE numero_documento_identidad = ?")) {
            ps.setString(1, numDoc);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_persona") : null;
            }
        }
    }

    private boolean existeEnClientes(Connection con, int idPersona) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM clientes WHERE id_persona = ?")) {
            ps.setInt(1, idPersona);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String obtenerNombreRol(Connection con, int idRol) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT nombre_rol FROM roles WHERE id_rol = ?")) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("nombre_rol") : "Trabajador";
            }
        }
    }

    private String limpiarOpcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}