package aplication;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("HIDRANDINA S.A. - SGCFE");

        this.primaryStage.setMinWidth(980);
        this.primaryStage.setMinHeight(640);

        this.mainScene = new Scene(new Pane(), 1120, 700);
        this.primaryStage.setScene(mainScene);

        mostrarLogin();
        this.primaryStage.show();
    }

    public void mostrarLogin() {
        mainScene.setRoot(new LoginView(this).getView());
    }

    public void mostrarDashboard() {
        mainScene.setRoot(new DashboardView(this).getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}