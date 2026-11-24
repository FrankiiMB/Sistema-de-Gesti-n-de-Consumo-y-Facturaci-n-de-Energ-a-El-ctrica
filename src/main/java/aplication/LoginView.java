package aplication;

import database.UsuarioDAO;
import database.UsuarioDAO.RolDTO;
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
import java.util.List;

public class LoginView {

    private final MainApp app;
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFFFFF;");

        // Panel azul izquierdo con logo e identidad
        VBox panelAzul = new VBox(12);
        panelAzul.setPrefWidth(480);
        panelAzul.setAlignment(Pos.CENTER);
        panelAzul.setPadding(new Insets(30));
        panelAzul.setStyle("-fx-background-color: #003B73;");

        ImageView imgLogo = new ImageView();
        var stream = getClass().getResourceAsStream("/logo.png");
        if (stream != null) {
            imgLogo.setImage(new Image(stream));
            imgLogo.setFitWidth(140);
            imgLogo.setPreserveRatio(true);
        }

        Label lblInicio = new Label("Hidrandina S.A.");
        lblInicio.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label lblSubEmpresa = new Label("Sistema de Gestión de Consumo y Facturación Eléctrica");
        lblSubEmpresa.setStyle("-fx-text-fill: #D9E2EC; -fx-font-size: 14px;");
        lblSubEmpresa.setWrapText(true);
        lblSubEmpresa.setAlignment(Pos.CENTER);

        panelAzul.getChildren().addAll(imgLogo, lblInicio, lblSubEmpresa);
        root.setLeft(panelAzul);

        // Formulario derecho
        VBox form = new VBox(10);
        form.setMaxWidth(420);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(40));

        Label lblTitulo = new Label("Ingreso al sistema");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        Label lblSub = new Label("Ingrese su usuario y contraseña asignados.");
        lblSub.setStyle("-fx-text-fill: #52606D; -fx-font-size: 13px;");
        VBox.setMargin(lblSub, new Insets(0, 0, 12, 0));

        Label lblRol = new Label("Rol / Actor:");
        lblRol.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");

        List<RolDTO> rolesBD = usuarioDAO.listarRoles();
        ComboBox<RolDTO> cmbRol = new ComboBox<>(FXCollections.observableArrayList(rolesBD));
        cmbRol.setMaxWidth(Double.MAX_VALUE);
        cmbRol.setPrefHeight(36);
        if (!rolesBD.isEmpty()) {
            cmbRol.getSelectionModel().selectFirst();
        }

        Label lblUser = new Label("Usuario / Código:");
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");
        VBox.setMargin(lblUser, new Insets(6, 0, 0, 0));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Ingrese su usuario o código");
        txtUser.setPrefHeight(36);

        Label lblPass = new Label("Contraseña:");
        lblPass.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");
        VBox.setMargin(lblPass, new Insets(6, 0, 0, 0));

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Ingrese su contraseña");
        txtPass.setPrefHeight(36);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #D64545; -fx-font-size: 12px;");
        lblError.setWrapText(true);

        Button btnIngresar = new Button("Iniciar Sesión");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setPrefHeight(40);
        btnIngresar.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        VBox.setMargin(btnIngresar, new Insets(10, 0, 0, 0));

        // Enlace "Crear cuenta" (visible SOLO cuando se selecciona Cliente)
        Hyperlink linkCrearCuenta = new Hyperlink("¿Eres cliente nuevo? Crear cuenta");
        linkCrearCuenta.setMaxWidth(Double.MAX_VALUE);
        linkCrearCuenta.setAlignment(Pos.CENTER);
        linkCrearCuenta.setStyle("-fx-text-fill: #004A80; -fx-font-weight: bold; -fx-font-size: 13px;");
        linkCrearCuenta.setVisible(false);
        linkCrearCuenta.setManaged(false);

        cmbRol.valueProperty().addListener((obs, ant, nuevo) -> {
            if (nuevo != null) {
                boolean esCliente = nuevo.nombreRol.equals("Cliente / Titular del suministro");
                linkCrearCuenta.setVisible(esCliente);
                linkCrearCuenta.setManaged(esCliente);
                lblError.setText("");
            }
        });

        linkCrearCuenta.setOnAction(e -> abrirDialogoRegistroCliente(cmbRol.getValue(), txtUser, txtPass));

        // Validación obligatoria contra SQL Server
        btnIngresar.setOnAction(e -> {
            RolDTO rolSel = cmbRol.getValue();
            String user = txtUser.getText().trim();
            String pass = txtPass.getText();

            if (rolSel == null) {
                lblError.setText("No se pudo conectar con la base de datos para cargar los roles.");
                return;
            }
            if (user.isEmpty() || pass.isEmpty()) {
                lblError.setText("Debe ingresar obligatoriamente su usuario y contraseña.");
                return;
            }

            try {
                UsuarioDTO autenticado = usuarioDAO.autenticar(user, pass, rolSel.idRol);
                if (autenticado == null) {
                    lblError.setText("Credenciales incorrectas o el usuario no pertenece al rol seleccionado.");
                    return;
                }
                SesionActual.setUsuario(autenticado);
                app.mostrarDashboard();
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                lblError.setText("Hubo un error de conexión con la base de datos.");
            }
        });

        form.getChildren().addAll(
                lblTitulo, lblSub, lblRol, cmbRol,
                lblUser, txtUser, lblPass, txtPass,
                lblError, btnIngresar, linkCrearCuenta
        );

        root.setCenter(new StackPane(form));
        return root;
    }

    private void abrirDialogoRegistroCliente(RolDTO rolCliente, TextField txtUserLogin, PasswordField txtPassLogin) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Registro de Cliente - Hidrandina S.A.");
        dialog.setHeaderText("Crear cuenta de Cliente / Titular del suministro");

        ButtonType btnGuardar = new ButtonType("Registrarme", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ComboBox<String> cmbTipoPersona = new ComboBox<>(FXCollections.observableArrayList("NATURAL", "JURIDICA"));
        cmbTipoPersona.setValue("NATURAL");

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue("DNI");

        TextField txtNumDoc = new TextField();
        TextField txtNombres = new TextField();
        TextField txtApellidos = new TextField();
        TextField txtDireccion = new TextField();

        ComboBox<String> cmbCondicion = new ComboBox<>(FXCollections.observableArrayList("PROPIETARIO", "INQUILINO"));
        cmbCondicion.setValue("PROPIETARIO");

        TextField txtUsuario = new TextField();
        txtUsuario.setPromptText("Ej. C-0001 o su DNI");

        PasswordField txtClave = new PasswordField();

        grid.add(new Label("Tipo de persona:"), 0, 0); grid.add(cmbTipoPersona, 1, 0);
        grid.add(new Label("Tipo de documento:"), 0, 1); grid.add(cmbTipoDoc, 1, 1);
        grid.add(new Label("N° de documento:"), 0, 2); grid.add(txtNumDoc, 1, 2);
        grid.add(new Label("Nombres / Razón Social:"), 0, 3); grid.add(txtNombres, 1, 3);
        grid.add(new Label("Apellidos:"), 0, 4); grid.add(txtApellidos, 1, 4);
        grid.add(new Label("Dirección:"), 0, 5); grid.add(txtDireccion, 1, 5);
        grid.add(new Label("Condición:"), 0, 6); grid.add(cmbCondicion, 1, 6);
        grid.add(new Label("Usuario de acceso:"), 0, 7); grid.add(txtUsuario, 1, 7);
        grid.add(new Label("Contraseña:"), 0, 8); grid.add(txtClave, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(resp -> {
            if (resp == btnGuardar) {
                if (txtNombres.getText().isBlank() || txtNumDoc.getText().isBlank()
                        || txtUsuario.getText().isBlank() || txtClave.getText().isBlank()) {
                    new Alert(Alert.AlertType.WARNING, "Complete todos los campos obligatorios.").showAndWait();
                    return;
                }
                try {
                    boolean ok = usuarioDAO.registrarUsuario(
                            txtUsuario.getText().trim(), txtClave.getText(), rolCliente.idRol,
                            txtNombres.getText().trim(), txtApellidos.getText().trim(),
                            cmbTipoPersona.getValue(), cmbTipoDoc.getValue(),
                            txtNumDoc.getText().trim(), cmbCondicion.getValue(), txtDireccion.getText().trim()
                    );
                    if (ok) {
                        txtUserLogin.setText(txtUsuario.getText().trim());
                        txtPassLogin.setText(txtClave.getText());
                        new Alert(Alert.AlertType.INFORMATION, "Cliente guardado en SQL Server. Ya puede iniciar sesión.").showAndWait();
                    }
                } catch (SQLException ex) {
                    System.err.println("Detalle SQL: " + ex.getMessage());
                    String mensaje = (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
                            ? "El usuario o número de documento ya se encuentra registrado."
                            : "Hubo un error de conexión con la base de datos.";
                    new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
                }
            }
        });
    }
}