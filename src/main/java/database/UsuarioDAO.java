package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public static class RolDTO {
        public int idRol;
        public String categoria;
        public String nombreRol;

        public RolDTO(int idRol, String categoria, String nombreRol) {
            this.idRol = idRol;
            this.categoria = categoria;
            this.nombreRol = nombreRol;
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
        public String tipoDocumento;
        public String numeroDocumentoIdentidad;
        public String condicionCliente;

        public String getNombreCompleto() {
            return nombres + (apellidos != null && !apellidos.isBlank() ? " " + apellidos : "");
        }
    }

    public List<RolDTO> listarRoles() {
        return List.of(
                new RolDTO(1, "Sistema", "Administrador"),
                new RolDTO(2, "Cliente", "Cliente / Titular del suministro"),
                new RolDTO(3, "Trabajador", "Auxiliar Comercial"),
                new RolDTO(4, "Trabajador", "Supervisor de Control Comercial"),
                new RolDTO(5, "Trabajador", "Supervisor de Control de Mediciones"),
                new RolDTO(6, "Trabajador", "Técnico Supervisor de Mediciones"),
                new RolDTO(7, "Contratista", "Contratista de instalación"),
                new RolDTO(8, "Trabajador", "Asistente de Facturación"),
                new RolDTO(9, "Contratista", "Contratista de Lectura"),
                new RolDTO(10, "Trabajador", "Analista de Facturación"),
                new RolDTO(11, "Contratista", "Contratista de Reparto"),
                new RolDTO(12, "Trabajador", "Supervisor de Cobranzas"),
                new RolDTO(13, "Trabajador", "Técnico Supervisor Comercial"),
                new RolDTO(14, "Contratista", "Contratista ejecutor de corte y reconexión"),
                new RolDTO(15, "Trabajador", "Asistente de Recaudación"),
                new RolDTO(16, "Externo", "Entidad Recaudadora / CAR")
        );
    }

    // 2. Autenticación obligatoria (busca en trabajadores o clientes según el rol)
    public UsuarioDTO autenticar(String usuarioIngresado, String clave, int idRol) throws SQLException {
        String sql = """
                SELECT p.id_persona, p.id_rol, r.nombre_rol, r.categoria,
                       p.nombres, p.apellidos, p.tipo_documento, p.numero_documento_identidad,
                       t.codigo_trabajador, c.usuario_cliente, c.tipo_cliente
                FROM personas p
                INNER JOIN roles r ON p.id_rol = r.id_rol
                LEFT JOIN trabajadores t ON p.id_persona = t.id_persona
                LEFT JOIN clientes c ON p.id_persona = c.id_persona
                WHERE p.id_rol = ?
                  AND ((t.codigo_trabajador = ? AND t.clave = ?)
                    OR (c.usuario_cliente = ? AND c.clave = ?))
                """;
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ps.setString(2, usuarioIngresado);
            ps.setString(3, clave);
            ps.setString(4, usuarioIngresado);
            ps.setString(5, clave);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UsuarioDTO u = new UsuarioDTO();
                    u.idPersona = rs.getInt("id_persona");
                    u.idRol = rs.getInt("id_rol");
                    u.nombreRol = rs.getString("nombre_rol");
                    u.categoriaRol = rs.getString("categoria");
                    u.nombres = rs.getString("nombres");
                    u.apellidos = rs.getString("apellidos");
                    u.tipoDocumento = rs.getString("tipo_documento");
                    u.numeroDocumentoIdentidad = rs.getString("numero_documento_identidad");
                    u.condicionCliente = rs.getString("tipo_cliente");
                    u.codigoUsuario = rs.getString("codigo_trabajador") != null
                            ? rs.getString("codigo_trabajador")
                            : rs.getString("usuario_cliente");
                    return u;
                }
            }
        }
        return null;
    }

    // 3. Registrar cuenta (inserta en personas y luego en clientes o trabajadores)
    public boolean registrarUsuario(String codigoUsuario, String clave, int idRol, String nombres,
                                    String apellidos, String tipoPersona, String tipoDoc,
                                    String numDoc, String condicionCliente, String direccion) throws SQLException {
        String sqlPersona = """
                INSERT INTO personas (id_rol, nombres, apellidos, tipo_documento,
                                      numero_documento_identidad, direccion, tipo_persona)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = ConexionDB.getConexion()) {
            con.setAutoCommit(false);
            try (PreparedStatement psPer = con.prepareStatement(sqlPersona, Statement.RETURN_GENERATED_KEYS)) {
                psPer.setInt(1, idRol);
                psPer.setString(2, nombres);
                psPer.setString(3, apellidos);
                psPer.setString(4, tipoDoc);
                psPer.setString(5, numDoc);
                psPer.setString(6, direccion);
                psPer.setString(7, tipoPersona);
                psPer.executeUpdate();

                int idPersonaGenerado;
                try (ResultSet keys = psPer.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No se obtuvo id_persona.");
                    idPersonaGenerado = keys.getInt(1);
                }

                if (condicionCliente != null) {
                    // Es Cliente -> insertar en tabla clientes
                    String sqlCli = """
                            INSERT INTO clientes (id_persona, usuario_cliente, clave, tipo_cliente)
                            VALUES (?, ?, ?, ?)
                            """;
                    try (PreparedStatement psCli = con.prepareStatement(sqlCli)) {
                        psCli.setInt(1, idPersonaGenerado);
                        psCli.setString(2, codigoUsuario);
                        psCli.setString(3, clave);
                        psCli.setString(4, condicionCliente);
                        psCli.executeUpdate();
                    }
                } else {
                    // Es Trabajador / Contratista -> obtener nombre del rol e insertar en trabajadores
                    String nombreCargo = obtenerNombreRol(con, idRol);
                    String sqlTrab = """
                            INSERT INTO trabajadores (codigo_trabajador, id_persona, cargo, clave)
                            VALUES (?, ?, ?, ?)
                            """;
                    try (PreparedStatement psTrab = con.prepareStatement(sqlTrab)) {
                        psTrab.setString(1, codigoUsuario);
                        psTrab.setInt(2, idPersonaGenerado);
                        psTrab.setString(3, nombreCargo);
                        psTrab.setString(4, clave);
                        psTrab.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
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

    // 4. Listar todas las cuentas registradas para la tabla del Administrador
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> lista = new ArrayList<>();
        String sql = """
                SELECT p.id_persona, p.id_rol, r.nombre_rol, r.categoria,
                       p.nombres, p.apellidos, p.tipo_documento, p.numero_documento_identidad,
                       COALESCE(t.codigo_trabajador, c.usuario_cliente) AS codigo_usuario
                FROM personas p
                INNER JOIN roles r ON p.id_rol = r.id_rol
                LEFT JOIN trabajadores t ON p.id_persona = t.id_persona
                LEFT JOIN clientes c ON p.id_persona = c.id_persona
                ORDER BY p.id_persona DESC
                """;
        try (Connection con = ConexionDB.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UsuarioDTO u = new UsuarioDTO();
                u.idPersona = rs.getInt("id_persona");
                u.idRol = rs.getInt("id_rol");
                u.nombreRol = rs.getString("nombre_rol");
                u.categoriaRol = rs.getString("categoria");
                u.nombres = rs.getString("nombres");
                u.apellidos = rs.getString("apellidos");
                u.tipoDocumento = rs.getString("tipo_documento");
                u.numeroDocumentoIdentidad = rs.getString("numero_documento_identidad");
                u.codigoUsuario = rs.getString("codigo_usuario");
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}