package aplication;

import database.UsuarioDAO;
import database.UsuarioDAO.UsuarioDTO;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.SQLException;

public class VistaPerfilCliente {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Parent vistaActualizarDatos() {
        UsuarioDTO u = ContextoSistema.getUsuario();
        boolean esJuridica = "JURIDICA".equalsIgnoreCase(u.tipoPersona);

        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Mi Perfil y Datos de Contacto");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        Label lblSub = new Label("Actualice su teléfono, correo electrónico, dirección o contraseña de acceso al sistema.");

        TitledPane panel = new TitledPane();
        panel.setText("Información del Titular y Cuenta");
        panel.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(12);
        grid.setPadding(new Insets(18));
        grid.getColumnConstraints().addAll(new ColumnConstraints(180), new ColumnConstraints(320));

        // Campos de identidad (Solo lectura)
        TextField txtNombres = new TextField(u.nombres != null ? u.nombres : "");
        txtNombres.setEditable(false);

        Label lblApellidos = new Label("Apellidos:");
        TextField txtApellidos = new TextField(u.apellidos != null ? u.apellidos : "");
        txtApellidos.setEditable(false);

        if (esJuridica) {
            lblApellidos.setVisible(false);
            lblApellidos.setManaged(false);
            txtApellidos.setVisible(false);
            txtApellidos.setManaged(false);
        }

        TextField txtDoc = new TextField(u.tipoDocumento + ": " + u.numeroDocumentoIdentidad + " (" + u.tipoPersona + ")");
        txtDoc.setEditable(false);

        String suministroTexto = (u.codigoSuministro != null && !u.codigoSuministro.isBlank())
                ? u.codigoSuministro
                : "Sin suministro asignado aún";
        TextField txtSuministro = new TextField(suministroTexto);
        txtSuministro.setEditable(false);

        // Campos editables
        TextField txtDireccion = new TextField(u.direccion != null ? u.direccion : "");
        TextField txtTelefono = new TextField(u.telefono != null ? u.telefono : "");
        txtTelefono.setPromptText("Ej. 943123456");

        TextField txtCorreo = new TextField(u.correo != null ? u.correo : "");
        txtCorreo.setPromptText("Ej. cliente@correo.com");

        PasswordField txtClave = new PasswordField();
        txtClave.setText(u.clave != null ? u.clave : "");

        Button btnGuardar = new Button("Guardar Cambios");

        grid.add(new Label(esJuridica ? "Razón Social:" : "Nombres:"), 0, 0); grid.add(txtNombres, 1, 0);
        grid.add(lblApellidos, 0, 1);                                       grid.add(txtApellidos, 1, 1);
        grid.add(new Label("Documento / Tipo:"), 0, 2);                     grid.add(txtDoc, 1, 2);
        grid.add(new Label("Código Suministro:"), 0, 3);                    grid.add(txtSuministro, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);                            grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Teléfono:"), 0, 5);                             grid.add(txtTelefono, 1, 5);
        grid.add(new Label("Correo electrónico:"), 0, 6);                   grid.add(txtCorreo, 1, 6);
        grid.add(new Separator(), 0, 7, 2, 1);
        grid.add(new Label("Contraseña:"), 0, 8);                           grid.add(txtClave, 1, 8);
        grid.add(btnGuardar, 1, 9);

        btnGuardar.setOnAction(e -> {
            if (txtClave.getText().isBlank()) {
                mostrarMensaje(Alert.AlertType.WARNING, "Campo obligatorio", "La contraseña no puede quedar vacía.");
                return;
            }
            try {
                boolean ok = usuarioDAO.actualizarDatosCliente(
                        u.idPersona,
                        txtDireccion.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText(),
                        txtClave.getText()
                );
                if (ok) {
                    u.direccion = txtDireccion.getText().trim();
                    u.telefono = txtTelefono.getText().trim();
                    u.correo = txtCorreo.getText().trim();
                    u.clave = txtClave.getText();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Actualización exitosa", "Sus datos fueron actualizados correctamente.");
                }
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                mostrarMensaje(Alert.AlertType.ERROR, "Error de actualización", "Hubo un error de conexión con la base de datos.");
            }
        });

        panel.setContent(grid);
        root.getChildren().addAll(lblTitulo, lblSub, panel);
        return root;
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}