package aplication;

import database.UsuarioDAO;
import database.UsuarioDAO.UsuarioDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.SQLException;

public class LoginView {

    private final MainApp app;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();

        // Panel izquierdo
        VBox panelIzquierdo = new VBox(12);
        panelIzquierdo.setPrefWidth(420);
        panelIzquierdo.setAlignment(Pos.CENTER);
        panelIzquierdo.setPadding(new Insets(30));

        ImageView imgLogo = new ImageView();
        var stream = getClass().getResourceAsStream("/logo.png");
        if (stream != null) {
            imgLogo.setImage(new Image(stream));
            imgLogo.setFitWidth(140);
            imgLogo.setPreserveRatio(true);
        }

        Label lblInicio = new Label("Hidrandina S.A.");
        lblInicio.setFont(Font.font("System", FontWeight.BOLD, 28));

        Label lblSubEmpresa = new Label("Sistema de Gestión de Consumo y Facturación Eléctrica");
        lblSubEmpresa.setWrapText(true);
        lblSubEmpresa.setAlignment(Pos.CENTER);

        panelIzquierdo.getChildren().addAll(imgLogo, lblInicio, lblSubEmpresa);
        root.setLeft(new BorderPane(panelIzquierdo, null, new Separator(javafx.geometry.Orientation.VERTICAL), null, null));

        // Formulario derecho
        VBox form = new VBox(10);
        form.setMaxWidth(420);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(40));

        Label lblTitulo = new Label("Ingreso al sistema");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label lblSub = new Label("Ingrese con su DNI/RUC, código de suministro o código de trabajador.");
        lblSub.setWrapText(true);
        VBox.setMargin(lblSub, new Insets(0, 0, 12, 0));

        Label lblUser = new Label("DNI / RUC / Código:");
        TextField txtUser = new TextField();
        txtUser.setPromptText("Ej. DNI, RUC, Suministro o AXC-001");

        Label lblPass = new Label("Contraseña:");
        VBox.setMargin(lblPass, new Insets(6, 0, 0, 0));
        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Ingrese su contraseña");

        Label lblError = new Label();
        lblError.setWrapText(true);

        Button btnIngresar = new Button("Iniciar Sesión");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setDefaultButton(true);
        VBox.setMargin(btnIngresar, new Insets(10, 0, 4, 0));

        Hyperlink linkCrearCuenta = new Hyperlink("¿Eres cliente nuevo? Crear cuenta");
        linkCrearCuenta.setMaxWidth(Double.MAX_VALUE);
        linkCrearCuenta.setAlignment(Pos.CENTER);
        linkCrearCuenta.setOnAction(e -> abrirDialogoRegistroCliente(txtUser, txtPass));

        Runnable accionLogin = () -> {
            String user = txtUser.getText().trim();
            String pass = txtPass.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Debe ingresar obligatoriamente su identificador y contraseña.");
                return;
            }

            try {
                UsuarioDTO autenticado = usuarioDAO.autenticar(user, pass);
                if (autenticado == null) {
                    lblError.setText("Credenciales incorrectas.");
                    return;
                }
                ContextoSistema.setUsuario(autenticado);
                app.mostrarDashboard();
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                lblError.setText("Hubo un error de conexión con la base de datos.");
            }
        };

        btnIngresar.setOnAction(e -> accionLogin.run());
        txtUser.setOnAction(e -> accionLogin.run());
        txtPass.setOnAction(e -> accionLogin.run());

        form.getChildren().addAll(
                lblTitulo, lblSub,
                lblUser, txtUser, lblPass, txtPass,
                lblError, btnIngresar, linkCrearCuenta
        );

        root.setCenter(new StackPane(form));
        return root;
    }

    private void abrirDialogoRegistroCliente(TextField txtUserLogin, PasswordField txtPassLogin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registro de Cliente - Hidrandina S.A.");
        dialog.setHeaderText("Crear cuenta de Cliente / Titular del suministro");

        ButtonType btnGuardar = new ButtonType("Registrarme", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.getColumnConstraints().addAll(new ColumnConstraints(150), new ColumnConstraints(240));

        ComboBox<String> cmbTipoPersona = new ComboBox<>(FXCollections.observableArrayList("NATURAL", "JURIDICA"));
        cmbTipoPersona.setValue("NATURAL");
        cmbTipoPersona.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue("DNI");
        cmbTipoDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumDoc = new TextField();
        txtNumDoc.setPromptText("8 dígitos (DNI)");
        configurarFiltroDocumento(txtNumDoc, cmbTipoDoc);

        Label lblNombres = new Label("Nombres:");
        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Ingrese sus nombres");

        Label lblApellidos = new Label("Apellidos:");
        TextField txtApellidos = new TextField();
        txtApellidos.setPromptText("Ingrese sus apellidos");

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección domiciliaria o fiscal");

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Opcional (Ej. 943123456)");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Opcional (Ej. correo@dominio.com)");

        ComboBox<String> cmbCondicion = new ComboBox<>(FXCollections.observableArrayList("PROPIETARIO", "INQUILINO"));
        cmbCondicion.setValue("PROPIETARIO");
        cmbCondicion.setMaxWidth(Double.MAX_VALUE);

        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Cree una contraseña");

        cmbTipoDoc.valueProperty().addListener((obs, ant, nuevoDoc) -> {
            boolean esRuc = "RUC".equals(nuevoDoc);
            txtNumDoc.setPromptText(esRuc ? "11 dígitos (RUC)" : "8 dígitos (DNI)");
            if (!esRuc && txtNumDoc.getText().length() > 8) {
                txtNumDoc.setText(txtNumDoc.getText().substring(0, 8));
            }
        });

        cmbTipoPersona.valueProperty().addListener((obs, ant, nuevo) -> {
            boolean esJuridica = "JURIDICA".equals(nuevo);

            lblNombres.setText(esJuridica ? "Razón Social:" : "Nombres:");
            txtNombres.setPromptText(esJuridica ? "Ej. Inversiones Norte S.A.C." : "Ingrese sus nombres");

            lblApellidos.setVisible(!esJuridica);
            lblApellidos.setManaged(!esJuridica);
            txtApellidos.setVisible(!esJuridica);
            txtApellidos.setManaged(!esJuridica);

            if (esJuridica) {
                txtApellidos.clear();
                cmbTipoDoc.setValue("RUC");
            } else {
                cmbTipoDoc.setValue("DNI");
            }

            if (dialog.getDialogPane().getScene() != null && dialog.getDialogPane().getScene().getWindow() != null) {
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });

        grid.add(new Label("Tipo de persona:"), 0, 0);     grid.add(cmbTipoPersona, 1, 0);
        grid.add(new Label("Tipo de documento:"), 0, 1);   grid.add(cmbTipoDoc, 1, 1);
        grid.add(new Label("N° de documento:"), 0, 2);     grid.add(txtNumDoc, 1, 2);
        grid.add(lblNombres, 0, 3);                        grid.add(txtNombres, 1, 3);
        grid.add(lblApellidos, 0, 4);                      grid.add(txtApellidos, 1, 4);
        grid.add(new Label("Dirección:"), 0, 5);           grid.add(txtDireccion, 1, 5);
        grid.add(new Label("Teléfono (opcional):"), 0, 6); grid.add(txtTelefono, 1, 6);
        grid.add(new Label("Correo (opcional):"), 0, 7);   grid.add(txtCorreo, 1, 7);
        grid.add(new Label("Condición:"), 0, 8);           grid.add(cmbCondicion, 1, 8);
        grid.add(new Label("Contraseña:"), 0, 9);          grid.add(txtClave, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
                boolean esJuridica = "JURIDICA".equals(cmbTipoPersona.getValue());
                String tipoDoc = cmbTipoDoc.getValue();
                String docIngresado = txtNumDoc.getText().trim();

                if (txtNombres.getText().isBlank() || (!esJuridica && txtApellidos.getText().isBlank())
                        || docIngresado.isBlank() || txtClave.getText().isBlank()) {
                    mostrarMensaje(Alert.AlertType.WARNING, "Campos incompletos", "Complete todos los campos obligatorios.");
                    return;
                }

                if ("DNI".equals(tipoDoc) && !docIngresado.matches("\\d{8}")) {
                    mostrarMensaje(Alert.AlertType.WARNING, "DNI inválido", "El DNI debe tener exactamente 8 dígitos numéricos.");
                    return;
                }
                if ("RUC".equals(tipoDoc) && !docIngresado.matches("\\d{11}")) {
                    mostrarMensaje(Alert.AlertType.WARNING, "RUC inválido", "El RUC debe tener exactamente 11 dígitos numéricos.");
                    return;
                }

                try {
                    boolean ok = usuarioDAO.registrarCliente(
                            txtClave.getText(),
                            txtNombres.getText().trim(),
                            esJuridica ? null : txtApellidos.getText().trim(),
                            cmbTipoPersona.getValue(),
                            tipoDoc,
                            docIngresado,
                            cmbCondicion.getValue(),
                            txtDireccion.getText().trim(),
                            txtTelefono.getText().trim(),
                            txtCorreo.getText().trim()
                    );
                    if (ok) {
                        txtUserLogin.setText(docIngresado);
                        txtPassLogin.setText(txtClave.getText());
                        mostrarMensaje(Alert.AlertType.INFORMATION, "Registro exitoso", "El registro fue correcto.");
                    }
                } catch (SQLException ex) {
                    System.err.println("Detalle SQL: " + ex.getMessage());
                    String mensaje = (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
                            ? "Este documento ya tiene una cuenta de cliente registrada."
                            : "Hubo un error de conexión con la base de datos.";
                    mostrarMensaje(Alert.AlertType.ERROR, "Error de registro", mensaje);
                }
            }
        });
    }

    private void configurarFiltroDocumento(TextField campo, ComboBox<String> cmbTipoDoc) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            int maxLen = "RUC".equals(cmbTipoDoc.getValue()) ? 11 : 8;
            if (nuevoTexto.matches("\\d*") && nuevoTexto.length() <= maxLen) {
                return change;
            }
            return null;
        }));
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}