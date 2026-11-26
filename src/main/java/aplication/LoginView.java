package aplication;

import database.UsuarioDAO;
import database.UsuarioDAO.UsuarioDTO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
        panelIzquierdo.setStyle("-fx-background-color: rgb(16, 59, 96);");

        ImageView imgLogo = new ImageView();
        var stream = getClass().getResourceAsStream("/logo.png");
        if (stream != null) {
            imgLogo.setImage(new Image(stream));
            imgLogo.setFitWidth(140);
            imgLogo.setPreserveRatio(true);
        }

        Label lblInicio = new Label("Hidrandina S.A.");
        lblInicio.setFont(Font.font("System", FontWeight.BOLD, 28));
        lblInicio.setStyle("-fx-text-fill: #FFFFFF;");

        Label lblSubEmpresa = new Label("Sistema de Gestión de Consumo y Facturación Eléctrica");
        lblSubEmpresa.setWrapText(true);
        lblSubEmpresa.setAlignment(Pos.CENTER);
        lblSubEmpresa.setStyle("-fx-text-fill: #CBD5E1;");

        panelIzquierdo.getChildren().addAll(imgLogo, lblInicio, lblSubEmpresa);
        root.setLeft(new BorderPane(panelIzquierdo, null, new Separator(Orientation.VERTICAL), null, null));

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
        txtUser.setPromptText("Ingrese su usuario");

        Label lblPass = new Label("Contraseña:");
        VBox.setMargin(lblPass, new Insets(6, 0, 0, 0));

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("Ingrese su contraseña");

        TextField txtPassVisible = new TextField();
        txtPassVisible.setPromptText("Ingrese su contraseña");
        txtPassVisible.setVisible(false);
        txtPassVisible.setManaged(false);
        txtPassVisible.textProperty().bindBidirectional(txtPass.textProperty());

        ToggleButton btnVerPass = new ToggleButton("👁");
        btnVerPass.setFocusTraversable(false);
        btnVerPass.setOnAction(e -> {
            boolean ver = btnVerPass.isSelected();
            txtPassVisible.setVisible(ver);
            txtPassVisible.setManaged(ver);
            txtPass.setVisible(!ver);
            txtPass.setManaged(!ver);
        });

        StackPane stackPassLogin = new StackPane(txtPass, txtPassVisible);
        HBox.setHgrow(stackPassLogin, Priority.ALWAYS);
        HBox boxPassLogin = new HBox(6, stackPassLogin, btnVerPass);

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
        txtPassVisible.setOnAction(e -> accionLogin.run());

        form.getChildren().addAll(
                lblTitulo, lblSub,
                lblUser, txtUser, lblPass, boxPassLogin,
                lblError, btnIngresar, linkCrearCuenta
        );

        root.setCenter(new StackPane(form));
        return root;
    }

    private void abrirDialogoRegistroCliente(TextField txtUserLogin, PasswordField txtPassLogin) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Registro de Clientes");
        dialog.setHeaderText("Crear cuenta");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node btnCloseOculto = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        btnCloseOculto.setVisible(false);
        btnCloseOculto.setManaged(false);

        VBox contenedor = new VBox(14);
        contenedor.setPadding(new Insets(18, 22, 16, 22));
        contenedor.setPrefWidth(450);

        //Indicador visual de pasos
        Label lblCirculo1 = new Label("1");
        lblCirculo1.setAlignment(Pos.CENTER);
        lblCirculo1.setPrefSize(28, 28);
        lblCirculo1.setStyle("-fx-background-color: #0078B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14;");

        Separator lineaPasos = new Separator();
        lineaPasos.setPrefWidth(160);

        Label lblCirculo2 = new Label("2");
        lblCirculo2.setAlignment(Pos.CENTER);
        lblCirculo2.setPrefSize(28, 28);
        lblCirculo2.setStyle("-fx-background-color: #E0E5EA; -fx-text-fill: #788590; -fx-font-weight: bold; -fx-background-radius: 14;");

        HBox filaCirculos = new HBox(10, lblCirculo1, lineaPasos, lblCirculo2);
        filaCirculos.setAlignment(Pos.CENTER);

        Label lblPaso1 = new Label("Identificación");
        lblPaso1.setStyle("-fx-font-weight: bold; -fx-text-fill: #0078B9;");

        Label lblPaso2 = new Label("Contraseña");
        lblPaso2.setStyle("-fx-font-weight: bold; -fx-text-fill: #788590;");

        HBox filaTextos = new HBox(125, lblPaso1, lblPaso2);
        filaTextos.setAlignment(Pos.CENTER);

        VBox stepper = new VBox(4, filaCirculos, filaTextos);

        //Paso 1
        GridPane gridPaso1 = new GridPane();
        gridPaso1.setHgap(12);
        gridPaso1.setVgap(10);
        gridPaso1.getColumnConstraints().addAll(new ColumnConstraints(150), new ColumnConstraints(245));

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

        gridPaso1.add(new Label("Tipo de persona:"), 0, 0);     gridPaso1.add(cmbTipoPersona, 1, 0);
        gridPaso1.add(new Label("Tipo de documento:"), 0, 1);   gridPaso1.add(cmbTipoDoc, 1, 1);
        gridPaso1.add(new Label("N° de documento:"), 0, 2);     gridPaso1.add(txtNumDoc, 1, 2);
        gridPaso1.add(lblNombres, 0, 3);                        gridPaso1.add(txtNombres, 1, 3);
        gridPaso1.add(lblApellidos, 0, 4);                      gridPaso1.add(txtApellidos, 1, 4);
        gridPaso1.add(new Label("Dirección:"), 0, 5);           gridPaso1.add(txtDireccion, 1, 5);
        gridPaso1.add(new Label("Teléfono (opcional):"), 0, 6); gridPaso1.add(txtTelefono, 1, 6);
        gridPaso1.add(new Label("Correo (opcional):"), 0, 7);   gridPaso1.add(txtCorreo, 1, 7);
        gridPaso1.add(new Label("Condición:"), 0, 8);           gridPaso1.add(cmbCondicion, 1, 8);

        Button btnSiguiente = new Button("Siguiente →");
        btnSiguiente.setStyle("-fx-background-color: #0078B9; -fx-text-fill: white; -fx-font-weight: bold;");
        Button btnCancelar = new Button("Cancelar");
        btnCancelar.setOnAction(e -> dialog.close());

        HBox botonesPaso1 = new HBox(10, btnSiguiente, btnCancelar);
        botonesPaso1.setAlignment(Pos.CENTER_RIGHT);
        botonesPaso1.setPadding(new Insets(8, 0, 0, 0));

        VBox panePaso1 = new VBox(12, gridPaso1, botonesPaso1);

        // Paso 2
        PasswordField txtClaveOculta = new PasswordField();
        txtClaveOculta.setPromptText("Cree una contraseña");
        TextField txtClaveVisible = new TextField();
        txtClaveVisible.setPromptText("Cree una contraseña");
        txtClaveVisible.setVisible(false);
        txtClaveVisible.setManaged(false);
        txtClaveVisible.textProperty().bindBidirectional(txtClaveOculta.textProperty());

        ToggleButton btnVer1 = new ToggleButton("👁");
        btnVer1.setFocusTraversable(false);
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
        txtConfirmOculta.setPromptText("Repita su contraseña");
        TextField txtConfirmVisible = new TextField();
        txtConfirmVisible.setPromptText("Repita su contraseña");
        txtConfirmVisible.setVisible(false);
        txtConfirmVisible.setManaged(false);
        txtConfirmVisible.textProperty().bindBidirectional(txtConfirmOculta.textProperty());

        ToggleButton btnVer2 = new ToggleButton("👁");
        btnVer2.setFocusTraversable(false);
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
        Label lblReglaMayus = new Label("• Una letra mayúscula");
        Label lblReglaMinus = new Label("• Una letra minúscula");
        Label lblReglaNum = new Label("• Un número");
        Label lblReglaCoincide = new Label("• Las contraseñas coinciden");

        VBox boxRequisitos = new VBox(5,
                new Label("Requisitos de contraseña:"),
                lblReglaLong, lblReglaMayus, lblReglaMinus, lblReglaNum, lblReglaCoincide
        );
        boxRequisitos.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10; -fx-background-radius: 6; -fx-border-color: #E2E8F0; -fx-border-radius: 6;");

        Button btnVolver = new Button("← Volver");
        Button btnRegistrarme = new Button("Registrarme");
        btnRegistrarme.setDisable(true);
        btnRegistrarme.setStyle("-fx-background-color: #0078B9; -fx-text-fill: white; -fx-font-weight: bold;");

        Runnable validarClaves = () -> {
            String c1 = txtClaveOculta.getText();
            String c2 = txtConfirmOculta.getText();

            boolean okLong = c1.length() >= 6;
            boolean okMayus = c1.matches(".*[A-ZÁÉÍÓÚÑ].*");
            boolean okMinus = c1.matches(".*[a-záéíóúñ].*");
            boolean okNum = c1.matches(".*[0-9].*");
            boolean okCoincide = !c1.isEmpty() && c1.equals(c2);

            actualizarRegla(lblReglaLong, okLong, "Mínimo 6 caracteres");
            actualizarRegla(lblReglaMayus, okMayus, "Una letra mayúscula");
            actualizarRegla(lblReglaMinus, okMinus, "Una letra minúscula");
            actualizarRegla(lblReglaNum, okNum, "Un número");
            actualizarRegla(lblReglaCoincide, okCoincide, "Las contraseñas coinciden");

            btnRegistrarme.setDisable(!(okLong && okMayus && okMinus && okNum && okCoincide));
        };

        txtClaveOculta.textProperty().addListener((obs, o, n) -> validarClaves.run());
        txtConfirmOculta.textProperty().addListener((obs, o, n) -> validarClaves.run());

        HBox botonesPaso2 = new HBox(10, btnVolver, btnRegistrarme);
        botonesPaso2.setAlignment(Pos.CENTER_RIGHT);
        botonesPaso2.setPadding(new Insets(8, 0, 0, 0));

        VBox panePaso2 = new VBox(10,
                new Label("Contraseña:"), boxPass1,
                new Label("Confirmar contraseña:"), boxPass2,
                boxRequisitos,
                botonesPaso2
        );
        panePaso2.setVisible(false);
        panePaso2.setManaged(false);

        // Transición Paso 1 a Paso 2
        btnSiguiente.setOnAction(e -> {
            boolean esJuridica = "JURIDICA".equals(cmbTipoPersona.getValue());
            String tipoDoc = cmbTipoDoc.getValue();
            String docIngresado = txtNumDoc.getText().trim();

            if (txtNombres.getText().isBlank() || (!esJuridica && txtApellidos.getText().isBlank()) || docIngresado.isBlank()) {
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

            lblCirculo1.setText("✔");
            lblCirculo1.setStyle("-fx-background-color: #43A532; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14;");
            lblPaso1.setStyle("-fx-font-weight: bold; -fx-text-fill: #43A532;");

            lblCirculo2.setStyle("-fx-background-color: #0078B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14;");
            lblPaso2.setStyle("-fx-font-weight: bold; -fx-text-fill: #0078B9;");

            panePaso1.setVisible(false);
            panePaso1.setManaged(false);
            panePaso2.setVisible(true);
            panePaso2.setManaged(true);

            if (dialog.getDialogPane().getScene() != null && dialog.getDialogPane().getScene().getWindow() != null) {
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });

        // Transición Paso 2 a Paso 1
        btnVolver.setOnAction(e -> {
            lblCirculo1.setText("1");
            lblCirculo1.setStyle("-fx-background-color: #0078B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 14;");
            lblPaso1.setStyle("-fx-font-weight: bold; -fx-text-fill: #0078B9;");

            lblCirculo2.setStyle("-fx-background-color: #E0E5EA; -fx-text-fill: #788590; -fx-font-weight: bold; -fx-background-radius: 14;");
            lblPaso2.setStyle("-fx-font-weight: bold; -fx-text-fill: #788590;");

            panePaso2.setVisible(false);
            panePaso2.setManaged(false);
            panePaso1.setVisible(true);
            panePaso1.setManaged(true);

            if (dialog.getDialogPane().getScene() != null && dialog.getDialogPane().getScene().getWindow() != null) {
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });

        // Guardado final en la base de datos
        btnRegistrarme.setOnAction(e -> {
            boolean esJuridica = "JURIDICA".equals(cmbTipoPersona.getValue());
            String tipoDoc = cmbTipoDoc.getValue();
            String docIngresado = txtNumDoc.getText().trim();

            try {
                boolean ok = usuarioDAO.registrarCliente(
                        txtClaveOculta.getText(),
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
                    txtPassLogin.setText(txtClaveOculta.getText());
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Registro exitoso", "El registro fue correcto.");
                    dialog.close();
                }
            } catch (SQLException ex) {
                String mensaje = (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
                        ? "Este documento ya tiene una cuenta de cliente registrada."
                        : "Hubo un error de conexión.";
                mostrarMensaje(Alert.AlertType.ERROR, "Error de registro", mensaje);
            }
        });

        StackPane stackPasos = new StackPane(panePaso1, panePaso2);
        contenedor.getChildren().addAll(stepper, new Separator(), stackPasos);

        dialog.getDialogPane().setContent(contenedor);
        dialog.showAndWait();
    }

    private void actualizarRegla(Label lbl, boolean cumple, String texto) {
        lbl.setText((cumple ? "✔ " : "• ") + texto);
        lbl.setStyle(cumple ? "-fx-text-fill: #16A34A; -fx-font-weight: bold;" : "-fx-text-fill: #64748B;");
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