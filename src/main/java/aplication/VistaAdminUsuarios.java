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
        VBox root = new VBox(12);
        root.setPadding(new Insets(20, 30, 20, 30));

        Label lblTitulo = new Label("Gestión de Cuentas de Trabajadores y Contratistas");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 18));

        TitledPane panelCrear = new TitledPane();
        panelCrear.setText("Crear nueva cuenta");
        panelCrear.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        var puestosDisponibles = usuarioDAO.listarRoles().stream()
                .filter(r -> !r.nombreRol.equals("Administrador") && !r.nombreRol.equals("Cliente / Titular del suministro"))
                .collect(Collectors.toList());

        ComboBox<RolDTO> cmbPuesto = new ComboBox<>(FXCollections.observableArrayList(puestosDisponibles));
        cmbPuesto.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> cmbTipoDoc = new ComboBox<>(FXCollections.observableArrayList("DNI", "RUC"));
        cmbTipoDoc.setValue("DNI");
        cmbTipoDoc.setMaxWidth(Double.MAX_VALUE);

        TextField txtNumDoc = new TextField();
        txtNumDoc.setPromptText("N° DNI (8 dígitos)");
        configurarFiltroDocumento(txtNumDoc, cmbTipoDoc);

        Label lblNombres = new Label("Nombres:");
        TextField txtNombres = new TextField();
        txtNombres.setPromptText("Nombres del trabajador");

        Label lblApellidos = new Label("Apellidos:");
        TextField txtApellidos = new TextField();
        txtApellidos.setPromptText("Apellidos del trabajador");

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono (opcional)");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo (opcional)");

        TextField txtUsuarioAuto = new TextField();
        txtUsuarioAuto.setEditable(false);

        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Ingrese contraseña");

        Button btnGuardar = new Button("Registrar");
        btnGuardar.setMinWidth(Region.USE_PREF_SIZE);

        Runnable actualizarCodigoGenerado = () -> {
            RolDTO rolSel = cmbPuesto.getValue();
            if (rolSel != null) {
                txtUsuarioAuto.setText(usuarioDAO.generarSiguienteCodigoTrabajador(rolSel.prefijo));
            }
        };

        cmbPuesto.valueProperty().addListener((obs, ant, nuevo) -> actualizarCodigoGenerado.run());

        if (!puestosDisponibles.isEmpty()) {
            cmbPuesto.getSelectionModel().selectFirst();
            actualizarCodigoGenerado.run();
        }

        cmbTipoDoc.valueProperty().addListener((obs, ant, nuevo) -> {
            boolean esRuc = "RUC".equals(nuevo);
            lblNombres.setText(esRuc ? "Razón Social:" : "Nombres:");
            txtNombres.setPromptText(esRuc ? "Nombre de la empresa contratista" : "Nombres del trabajador");
            txtNumDoc.setPromptText(esRuc ? "N° RUC (11 dígitos)" : "N° DNI (8 dígitos)");

            if (!esRuc && txtNumDoc.getText().length() > 8) {
                txtNumDoc.setText(txtNumDoc.getText().substring(0, 8));
            }

            lblApellidos.setVisible(!esRuc);
            lblApellidos.setManaged(!esRuc);
            txtApellidos.setVisible(!esRuc);
            txtApellidos.setManaged(!esRuc);
            if (esRuc) txtApellidos.clear();
        });

        grid.add(new Label("Puesto / Rol asignado:"), 0, 0); grid.add(cmbPuesto, 1, 0);
        grid.add(new Label("Tipo Doc.:"), 2, 0);             grid.add(cmbTipoDoc, 3, 0);
        grid.add(new Label("N° Documento:"), 0, 1);          grid.add(txtNumDoc, 1, 1);
        grid.add(lblNombres, 2, 1);                          grid.add(txtNombres, 3, 1);
        grid.add(lblApellidos, 0, 2);                        grid.add(txtApellidos, 1, 2);
        grid.add(new Label("Teléfono (opcional):"), 2, 2);   grid.add(txtTelefono, 3, 2);
        grid.add(new Label("Correo (opcional):"), 0, 3);     grid.add(txtCorreo, 1, 3);
        grid.add(new Label("Usuario / Código:"), 2, 3);      grid.add(txtUsuarioAuto, 3, 3);
        grid.add(new Label("Contraseña:"), 0, 4);            grid.add(txtClave, 1, 4);
        grid.add(btnGuardar, 3, 4);

        panelCrear.setContent(grid);

        TableView<UsuarioDTO> tabla = new TableView<>();

        TableColumn<UsuarioDTO, String> colCod = new TableColumn<>("Usuario / Código");
        colCod.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().codigoUsuario));

        TableColumn<UsuarioDTO, String> colDoc = new TableColumn<>("DNI / RUC");
        colDoc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().tipoDocumento + ": " + d.getValue().numeroDocumentoIdentidad));

        TableColumn<UsuarioDTO, String> colNom = new TableColumn<>("Nombre / Razón Social");
        colNom.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreCompleto()));

        TableColumn<UsuarioDTO, String> colCat = new TableColumn<>("Categoría");
        colCat.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().categoriaRol));

        TableColumn<UsuarioDTO, String> colPuesto = new TableColumn<>("Puesto / Rol");
        colPuesto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nombreRol));

        tabla.getColumns().addAll(colCod, colDoc, colNom, colCat, colPuesto);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));

        btnGuardar.setOnAction(e -> {
            String tipoDoc = cmbTipoDoc.getValue();
            boolean esRuc = "RUC".equals(tipoDoc);
            String docIngresado = txtNumDoc.getText().trim();

            if (cmbPuesto.getValue() == null || txtNombres.getText().isBlank()
                    || (!esRuc && txtApellidos.getText().isBlank())
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
                String tipoPersona = esRuc ? "JURIDICA" : "NATURAL";
                String codigoAsignado = txtUsuarioAuto.getText().trim();

                boolean ok = usuarioDAO.registrarTrabajador(
                        codigoAsignado,
                        txtClave.getText(),
                        cmbPuesto.getValue().idRol,
                        txtNombres.getText().trim(),
                        esRuc ? null : txtApellidos.getText().trim(),
                        tipoPersona,
                        tipoDoc,
                        docIngresado,
                        null,
                        txtTelefono.getText().trim(),
                        txtCorreo.getText().trim()
                );
                if (ok) {
                    tabla.setItems(FXCollections.observableArrayList(usuarioDAO.listarUsuarios()));
                    txtNombres.clear(); txtApellidos.clear(); txtNumDoc.clear();
                    txtTelefono.clear(); txtCorreo.clear(); txtClave.clear();
                    actualizarCodigoGenerado.run();
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Registro exitoso", "El registro fue correcto. Código asignado: " + codigoAsignado);
                }
            } catch (SQLException ex) {
                System.err.println("Detalle SQL: " + ex.getMessage());
                String mensaje = (ex.getSQLState() != null && ex.getSQLState().startsWith("23"))
                        ? "El número de documento ya está registrado."
                        : "Hubo un error de conexión con la base de datos.";
                mostrarMensaje(Alert.AlertType.ERROR, "Error de registro", mensaje);
            }
        });

        root.getChildren().addAll(lblTitulo, panelCrear, new Label("Cuentas Registradas:"), tabla);
        return root;
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