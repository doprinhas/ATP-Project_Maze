import Model.*;
import Server.Configurations;
import View.View;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //ViewModel -> Model
        Configurations.setDefaultConfigurations();
        Configurations.setSolveAlgorithm("Best first search");
        Configurations.setThreadPoolSize(1);
        Model model = new Model();
        model.startServers();
        ViewModel viewModel = new ViewModel(model);
        model.addObserver(viewModel);

        //Loading Main Windows
        primaryStage.setTitle("The Maze!");
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("View/View.fxml").openStream());
        Scene scene = new Scene(root, 800, 700);
        scene.getStylesheets().add(getClass().getResource("View/View.css").toExternalForm());
        primaryStage.setScene(scene);

        //View -> ViewModel
        View view = fxmlLoader.getController();
        view.initialize(viewModel,primaryStage,scene);
        viewModel.addObserver(view);
        //--------------
        setStageCloseEvent(primaryStage, model);
        //
        //Show the Main Window
        primaryStage.show();
    }

    private void setStageCloseEvent(Stage primaryStage, Model model) {
        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
                // Close the program properly
                model.close();
            } else {
                // ... user chose CANCEL or closed the dialog
                event.consume();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
