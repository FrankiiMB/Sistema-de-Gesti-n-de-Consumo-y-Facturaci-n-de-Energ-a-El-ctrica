package aplication;

import database.UsuarioDAO.UsuarioDTO;
import java.util.ArrayList;
import java.util.List;

public class ContextoSistema {

    // Usuario real autenticado desde SQL Server
    private static UsuarioDTO usuarioActual;

    // Listas temporales en memoria para los 3 procesos (sin datos ficticios;
    // se reemplazarán por SolicitudDAO, LecturaDAO y MorosidadDAO)
    public static final List<String[]> listaSolicitudes = new ArrayList<>();
    public static final List<String[]> listaLecturasRecibos = new ArrayList<>();
    public static final List<String[]> listaMorosidadCortes = new ArrayList<>();

    public static UsuarioDTO getUsuario() {
        return usuarioActual;
    }

    public static void setUsuario(UsuarioDTO usuario) {
        usuarioActual = usuario;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}