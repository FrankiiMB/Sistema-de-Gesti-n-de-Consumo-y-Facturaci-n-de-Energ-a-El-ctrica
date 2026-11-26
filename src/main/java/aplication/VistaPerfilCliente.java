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
    private boolean cambiandoPassword = false;

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

        // Campos editables de contacto
        TextField txtDireccion = new TextField(u.direccion != null ? u.direccion : "");
        TextField txtTelefono = new TextField(u.telefono != null ? u.telefono : "");
        txtTelefono.setPromptText("Ej. 943123456");

        TextField txtCorreo = new TextField(u.correo != null ? u.correo : "");
        txtCorreo.setPromptText("Ej. cliente@correo.com");

        // --- Botón para desplegar cambio de contraseña ---
        Button btnTogglePassword = new Button("Actualizar contraseña");

        // --- Apartado oculto de nueva contraseña ---
        PasswordField txtClaveOculta = new PasswordField();
        txtClaveOculta.setPromptText("Ingrese nueva contraseña");

        TextField txtClaveVisible = new TextField();
        txtClaveVisible.setPromptText("Ingrese nueva contraseña");
        txtClaveVisible.setVisible(false);
        txtClaveVisible.setManaged(false);
        txtClaveVisible.textProperty().bindBidirectional(txtClaveOculta.textProperty());

        ToggleButton btnVer1 = new ToggleButton("👁");
        btnVer1.setOnAction(e -> {
            boolean ver = btnVer1.isSelected();
            txtClaveVisible.setVisible(ver);
            txtClaveVisible.setManaged(ver);
            txtClaveOculta.setVisible(!ver);
            txtClaveOculta.setManaged(!ver);
        });

        StackPane stackPass1 = new StackPane(txtClaveOculta, txtClaveVisible);
        HBox.setHgrow(stackPass1, Priority.ALWAYS);
        HBox boxPass1 = new HBox(6, stackPass1, btnVer1);

        PasswordField txtConfirmOculta = new PasswordField();
        txtConfirmOculta.setPromptText("Confirme nueva contraseña");

        TextField txtConfirmVisible = new TextField();
        txtConfirmVisible.setPromptText("Confirme nueva contraseña");
        txtConfirmVisible.setVisible(false);
        txtConfirmVisible.setManaged(false);
        txtConfirmVisible.textProperty().bindBidirectional(txtConfirmOculta.textProperty());

        ToggleButton btnVer2 = new ToggleButton("👁");
        btnVer2.setOnAction(e -> {
            boolean ver = btnVer2.isSelected();
            txtConfirmVisible.setVisible(ver);
            txtConfirmVisible.setManaged(ver);
            txtConfirmOculta.setVisible(!ver);
            txtConfirmOculta.setManaged(!ver);
        });

        StackPane stackPass2 = new StackPane(txtConfirmOculta, txtConfirmVisible);
        HBox.setHgrow(stackPass2, Priority.ALWAYS);
        HBox boxPass2 = new HBox(6, stackPass2, btnVer2);

        Label lblReglaLong = new Label("• Mínimo 6 caracteres");
        Label lblReglaMayus = new Label("• Al menos una letra mayúscula");
        Label lblReglaMinus = new Label("• Al menos una letra minúscula");
        Label lblReglaNum = new Label("• Al menos un número");
        Label lblReglaCoincide = new Label("• Las contraseñas coinciden");

        VBox boxRequisitos = new VBox(4,
                new Label("Requisitos de contraseña:"),
                lblReglaLong, lblReglaMayus, lblReglaMinus, lblReglaNum, lblReglaCoincide
        );
        boxRequisitos.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10; -fx-background-radius: 6; -fx-border-color: #CBD5E1; -fx-border-radius: 6;");

        VBox seccionPassword = new VBox(8,
                new Label("Nueva contraseña:"), boxPass1,
                new Label("Confirmar nueva contraseña:"), boxPass2,
                boxRequisitos
        );
        seccionPassword.setVisible(false);
        seccionPassword.setManaged(false);

        Button btnGuardar = new Button("Guardar Cambios");

        Runnable validarClaves = () -> {
            if (!cambiandoPassword) {
                btnGuardar.setDisable(false);
                return;
            }
            String c1 = txtClaveOculta.getText();
            String c2 = txtConfirmOculta.getText();

            boolean okLong = c1.length() >= 6;
            boolean okMayus = c1.matches(".*[A-ZÁÉÍÓÚÑ].*");
            boolean okMinus = c1.matches(".*[a-záéíóúñ].*");
            boolean okNum = c1.matches(".*[0-9].*");
            boolean okCoincide = !c1.isEmpty() && c1.equals(c2);

            actualizarRegla(lblReglaLong, okLong, "Mínimo 6 caracteres");
            actualizarRegla(lblReglaMayus, okMayus, "Al menos una letra mayúscula");
            actualizarRegla(lblReglaMinus, okMinus, "Al menos una letra minúscula");
            actualizarRegla(lblReglaNum, okNum, "Al menos un número");
            actualizarRegla(lblReglaCoincide, okCoincide, "Las contraseñas coinciden");

            btnGuardar.setDisable(!(okLong && okMayus && okMinus && okNum && okCoincide));
        };

        txtClaveOculta.textProperty().addListener((obs, o, n) -> validarClaves.run());
        txtConfirmOculta.textProperty().addListener((obs, o, n) -> validarClaves.run());

        btnTogglePassword.setOnAction(e -> {
            cambiandoPassword = !cambiandoPassword;
            seccionPassword.setVisible(cambiandoPassword);
            seccionPassword.setManaged(cambiandoPassword);

            if (cambiandoPassword) {
                btnTogglePassword.setText("Cancelar cambio de contraseña");
                txtClaveOculta.clear();
                txtConfirmOculta.clear();
                validarClaves.run();
            } else {
                btnTogglePassword.setText("Actualizar contraseña");
                txtClaveOculta.clear();
                txtConfirmOculta.clear();
                btnGuardar.setDisable(false);
            }
        });

        grid.add(new Label(esJuridica ? "Razón Social:" : "Nombres:"), 0, 0); grid.add(txtNombres, 1, 0);
        grid.add(lblApellidos, 0, 1);                                       grid.add(txtApellidos, 1, 1);
        grid.add(new Label("Documento / Tipo:"), 0, 2);                     grid.add(txtDoc, 1, 2);
        grid.add(new Label("Código Suministro:"), 0, 3);                    grid.add(txtSuministro, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);                            grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Teléfono:"), 0, 5);                             grid.add(txtTelefono, 1, 5);
        grid.add(new Label("Correo electrónico:"), 0, 6);                   grid.add(txtCorreo, 1, 6);
        grid.add(new Separator(), 0, 7, 2, 1);
        grid.add(new Label("Seguridad:"), 0, 8);                            grid.add(btnTogglePassword, 1, 8);
        grid.add(seccionPassword, 1, 9);
        grid.add(btnGuardar, 1, 10);

        btnGuardar.setOnAction(e -> {
            String claveAEnviar = cambiandoPassword ? txtClaveOculta.getText() : u.clave;

            if (cambiandoPassword && (claveAEnviar == null || claveAEnviar.isBlank() || !claveAEnviar.equals(txtConfirmOculta.getText()))) {
                mostrarMensaje(Alert.AlertType.WARNING, "Contraseña inválida", "Verifique que la nueva contraseña cumpla todos los requisitos y coincida en ambos campos.");
                return;
            }
            try {
                boolean ok = usuarioDAO.actualizarDatosCliente(
                        u.idPersona,
                        txtDireccion.getText(),
                        txtTelefono.getText(),
                        txtCorreo.getText(),
                        claveAEnviar
                );
                if (ok) {
                    u.direccion = txtDireccion.getText().trim();
                    u.telefono = txtTelefono.getText().trim();
                    u.correo = txtCorreo.getText().trim();
                    u.clave = claveAEnviar;

                    if (cambiandoPassword) {
                        cambiandoPassword = false;
                        seccionPassword.setVisible(false);
                        seccionPassword.setManaged(false);
                        btnTogglePassword.setText("Actualizar contraseña");
                        txtClaveOculta.clear();
                        txtConfirmOculta.clear();
                    }

                    mostrarMensaje(Alert.AlertType.INFORMATION, "Actualización exitosa", "Sus datos fueron actualizados correctamente.");
                }
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                mostrarMensaje(Alert.AlertType.ERROR, "Error de actualización", "Hubo un error de conexión con la base de datos.");
            }
        });

        panel.setContent(grid);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);

        root.getChildren().addAll(lblTitulo, lblSub, scroll);
        return root;
    }

    private void actualizarRegla(Label lbl, boolean cumple, String texto) {
        lbl.setText((cumple ? "✔ " : "• ") + texto);
        lbl.setStyle(cumple ? "-fx-text-fill: #16A34A; -fx-font-weight: bold;" : "-fx-text-fill: #64748B;");
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}