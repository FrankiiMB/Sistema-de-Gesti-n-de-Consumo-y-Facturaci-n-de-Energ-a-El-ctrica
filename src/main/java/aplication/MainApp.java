package aplication;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("HIDRANDINA S.A.");

        var iconStream = getClass().getResourceAsStream("/logo.png");
        if (iconStream != null) {
            this.primaryStage.getIcons().add(new Image(iconStream));
        }

        this.primaryStage.setMinWidth(980);
        this.primaryStage.setMinHeight(640);

        this.mainScene = new Scene(new Pane(), 1120, 700);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.setOnCloseRequest(e -> ContextoSistema.cerrarSesion());

        mostrarLogin();
        this.primaryStage.show();
    }

    public void mostrarLogin() {
        primaryStage.setTitle("HIDRANDINA S.A. - Ingreso al Sistema");
        mainScene.setRoot(new LoginView(this).getView());
    }

    public void mostrarDashboard() {
        primaryStage.setTitle("HIDRANDINA S.A. - SGCFE");
        mainScene.setRoot(new DashboardView(this).getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}