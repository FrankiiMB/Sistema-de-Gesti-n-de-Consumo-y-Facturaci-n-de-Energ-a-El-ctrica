package aplication;

import database.UsuarioDAO;
import database.UsuarioDAO.RolDTO;
import database.UsuarioDAO.UsuarioDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class VistaAdminUsuarios {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Parent vistaGestionTrabajadores() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(25, 35, 25, 35));
        root.setStyle("-fx-background-color: #F4F7FA;");

        Label lblTitulo = new Label("Gestión de Cuentas de Trabajadores y Contratistas");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        lblTitulo.setStyle("-fx-text-fill: #003B73;");

        Label lblSub = new Label("Registre el personal interno, asigne su puesto/rol y guarde sus credenciales en SQL Server.");
        lblSub.setStyle("-fx-text-fill: #52606D; -fx-font-size: 12px;");

        TitledPane panelCrear = new TitledPane();
        panelCrear.setText("Crear nueva cuenta de trabajador / contratista");
        panelCrear.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Cargar desde SQL Server solo los puestos internos (excluye Administrador y Cliente)
        var puestosDisponibles = usuarioDAO.listarRoles().stream()
                .filter(r -> !r.nombreRol.equals("Administrador") && !r.nombreRol.equals("Cliente / Titular del suministro"))
                .collect(Collectors.toList());

        ComboBox<RolDTO> cmbPuesto = new ComboBox<>(FXCollections.observableArrayList(puestosDisponibles));
        if (!puestosDisponibles.isEmpty()) cmbPuesto.getSelectionModel().selectFirst();
        cmbPuesto.setMaxWidth(Double.MAX_VALUE);

        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Nombres o Razón Social");

        TextField txtApellidos = new TextField();
        txtApellidos.setPromptText("Apellidos");

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue("DNI");

        TextField txtNumDoc = new TextField();
        txtNumDoc.setPromptText("N° DNI o RUC");

        TextField txtCodigo = new TextField();
        txtCodigo.setPromptText("Ej. T-0001 o K-0001");

        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Contraseña asignada");

        Button btnGuardar = new Button("Registrar Trabajador en Base de Datos");
        btnGuardar.setStyle("-fx-background-color: #198754; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 18; -fx-cursor: hand;");

        grid.add(new Label("Puesto / Rol asignado:"), 0, 0); grid.add(cmbPuesto, 1, 0);
        grid.add(new Label("Nombres / Empresa:"), 2, 0); grid.add(txtNombres, 3, 0);
        grid.add(new Label("Apellidos:"), 0, 1); grid.add(txtApellidos, 1, 1);
        grid.add(new Label("Tipo Doc.:"), 2, 1); grid.add(cmbTipoDoc, 3, 1);
        grid.add(new Label("N° Documento:"), 0, 2); grid.add(txtNumDoc, 1, 2);
        grid.add(new Label("Usuario / Código:"), 2, 2); grid.add(txtCodigo, 3, 2);
        grid.add(new Label("Contraseña:"), 0, 3); grid.add(txtClave, 1, 3);
        grid.add(btnGuardar, 3, 3);

        panelCrear.setContent(grid);

        // Tabla conectada a SQL Server
        TableView<UsuarioDTO> tabla = new TableView<>();

        TableColumn<UsuarioDTO, String> colCod = new TableColumn<>("Usuario / Código");
        colCod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().codigoUsuario));

        TableColumn<UsuarioDTO, String> colNom = new TableColumn<>("Nombre Completo");
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));

        TableColumn<UsuarioDTO, String> colDoc = new TableColumn<>("Documento");
        colDoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoDocumento + ": " + d.getValue().numeroDocumentoIdentidad));

        TableColumn<UsuarioDTO, String> colCat = new TableColumn<>("Categoría");
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().categoriaRol));

        TableColumn<UsuarioDTO, String> colPuesto = new TableColumn<>("Puesto / Rol");
        colPuesto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombreRol));

        tabla.getColumns().addAll(colCod, colNom, colDoc, colCat, colPuesto);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));

        btnGuardar.setOnAction(e -> {
            if (cmbPuesto.getValue() == null || txtNombres.getText().isBlank()
                    || txtNumDoc.getText().isBlank() || txtCodigo.getText().isBlank() || txtClave.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Complete puesto, nombres, documento, usuario y contraseña.").showAndWait();
                return;
            }
            try {
                String tipoPersona = "RUC".equals(cmbTipoDoc.getValue()) ? "JURIDICA" : "NATURAL";
                boolean ok = usuarioDAO.registrarUsuario(
                        txtCodigo.getText().trim(), txtClave.getText(), cmbPuesto.getValue().idRol,
                        txtNombres.getText().trim(), txtApellidos.getText().trim(),
                        tipoPersona, cmbTipoDoc.getValue(), txtNumDoc.getText().trim(),
                        null, ""
                );
                if (ok) {
                    tabla.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));
                    txtNombres.clear(); txtApellidos.clear(); txtNumDoc.clear(); txtCodigo.clear(); txtClave.clear();
                    new Alert(Alert.AlertType.INFORMATION, "Trabajador guardado correctamente en SQL Server.").showAndWait();
                }
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                String mensaje = (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
                        ? "El código de usuario o documento ya está registrado."
                        : "Hubo un error de conexión con la base de datos.";
                new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
            }
        });

        root.getChildren().addAll(lblTitulo, lblSub, panelCrear, new Label("Cuentas Registradas en SQL Server:"), tabla);
        return root;
    }
}