package aplication;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class MainApp extends Application {

    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.stage.setTitle("Sistema de Gestión de Consumo y Facturación de Energía Eléctrica");

        // Asignar el logo como icono de la ventana y barra de tareas
        var iconStream = getClass().getResourceAsStream("/logo.png");
        if (iconStream != null) {
            this.stage.getIcons().add(new Image(iconStream));
        }

        mostrarLogin();
        this.stage.show();
    }

    public void mostrarLogin() {
        stage.setScene(new Scene(new LoginView(this).getView(), 1020, 650));
    }

    public void mostrarDashboard() {
        stage.setScene(new Scene(new DashboardView(this).getView(), 1020, 650));
    }

    public static void main(String[] args) {
        launch(args);
    }
}