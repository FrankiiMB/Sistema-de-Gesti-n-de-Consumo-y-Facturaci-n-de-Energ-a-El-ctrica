package aplication;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import aplication.ContextoDemo.ActorItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginView {

    private final MainApp app;

    public LoginView(MainApp app) {
        this.app = app;
    }

    public Parent getView() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #FFFFFF;");
        
        // Panel azul izquierdo con imagen + texto centrado
        VBox panelAzul = new VBox(12);
        panelAzul.setPrefWidth(480);
        panelAzul.setAlignment(Pos.CENTER);
        panelAzul.setPadding(new Insets(30));
        panelAzul.setStyle("-fx-background-color: #003B73;");

        // Cargar imagen desde src/main/resources/logo.png
        ImageView imgLogo = new ImageView();
        var stream = getClass().getResourceAsStream("/logo.png");
        if (stream != null) {
            imgLogo.setImage(new Image(stream));
            imgLogo.setFitWidth(140); // Ancho de la imagen (ajústalo a tu gusto)
            imgLogo.setPreserveRatio(true); // Mantiene la proporción sin deformarla
        }

        Label lblInicio = new Label("Hidrandina S.A.");
        lblInicio.setStyle("-fx-text-fill: white; -fx-font-size: 32px; -fx-font-weight: bold;");

        Label lblSubEmpresa = new Label("Sistema de Gestión de Consumo y Facturación Eléctrica");
        lblSubEmpresa.setStyle("-fx-text-fill: #D9E2EC; -fx-font-size: 14px;");
        lblSubEmpresa.setWrapText(true);
        lblSubEmpresa.setAlignment(Pos.CENTER);

        // El orden aquí define qué va arriba: primero imgLogo, luego lblInicio
        panelAzul.getChildren().addAll(imgLogo, lblInicio, lblSubEmpresa);
        root.setLeft(panelAzul);
        
        VBox form = new VBox(10);
        form.setMaxWidth(500);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(40));

        Label lblTitulo = new Label("Ingreso al sistema");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        Label lblSub = new Label("Ingresa tus datos para iniciar sesión.");
        lblSub.setStyle("-fx-text-fill: #52606D; -fx-font-size: 13px;");
        VBox.setMargin(lblSub, new Insets(0, 0, 12, 0));

        Label lblRol = new Label("Rol / Actor:");
        lblRol.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");

        ComboBox<ActorItem> cmbRol = new ComboBox<>(FXCollections.observableArrayList(ContextoDemo.getActores()));
        cmbRol.setMaxWidth(Double.MAX_VALUE);
        cmbRol.setPrefHeight(36);
        cmbRol.getSelectionModel().selectFirst();

        Label lblUser = new Label("Usuario / Código");
        lblUser.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");
        VBox.setMargin(lblUser, new Insets(6, 0, 0, 0));

        TextField txtUser = new TextField();
        txtUser.setPromptText("Ejemplo: T-0001");
        txtUser.setPrefHeight(36);

        Label lblPass = new Label("Contraseña");
        lblPass.setStyle("-fx-font-weight: bold; -fx-text-fill: #1F2933;");
        VBox.setMargin(lblPass, new Insets(6, 0, 0, 0));

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("•••••");
        txtPass.setPrefHeight(36);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #D64545; -fx-font-size: 12px;");

        Button btnIngresar = new Button("Iniciar Sesión");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setPrefHeight(40);
        btnIngresar.setStyle("-fx-background-color: #004A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        VBox.setMargin(btnIngresar, new Insets(10, 0, 0, 0));

        btnIngresar.setOnAction(e -> {
            ActorItem sel = cmbRol.getValue();
            if (sel == null) return;

            if (ContextoDemo.VALIDAR_CREDENCIALES) {
                if (!sel.codigo.equalsIgnoreCase(txtUser.getText().trim()) || !sel.clave.equals(txtPass.getText())) {
                    lblError.setText("Credenciales inválidas. Código esperado: " + sel.codigo + " | Clave: " + sel.clave);
                    return;
                }
            }

            ContextoDemo.setActorActual(sel);
            app.mostrarDashboard();
        });

        form.getChildren().addAll(lblTitulo, lblSub, lblRol, cmbRol, lblUser, txtUser, lblPass, txtPass, lblError, btnIngresar);
        root.setCenter(new StackPane(form));
        return root;
    }
}