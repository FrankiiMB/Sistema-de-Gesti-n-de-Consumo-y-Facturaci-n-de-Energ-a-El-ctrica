package aplication;

import database.UsuarioDAO.UsuarioDTO;

public class SesionActual {
    private static UsuarioDTO usuarioLogueado;

    public static UsuarioDTO getUsuario() {
        return usuarioLogueado;
    }

    public static void setUsuario(UsuarioDTO usuario) {
        usuarioLogueado = usuario;
    }
}