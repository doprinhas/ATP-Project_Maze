package View;


import Server.Configurations;
import ViewModel.ViewModel;
import algorithms.search.Solution;
import com.sun.deploy.ui.ProgressDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class View implements Observer, IView{

    public MazeDisplayer mazeDisplayer;
    public javafx.scene.layout.Pane pane;

    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.TextField txtfld_FloorNum;

    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Label lbl_FloorNum;

    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;

    public StringProperty characterPositionRow = new SimpleStringProperty("0");
    public StringProperty characterPositionColumn = new SimpleStringProperty("0");
    public StringProperty characterPositionFloor = new SimpleStringProperty("0");

    private static ExecutorService pool;

    @FXML

    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;

    public void initialize( ViewModel viewModel , Stage mainStage , Scene mainScene ){
        if ( viewModel == null || mainStage == null || mainScene == null)
            return;

        pool = Executors.newFixedThreadPool(1);

        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        bindProperties();
        setResizeEvent();
    }

    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
        lbl_FloorNum.textProperty().bind(this.characterPositionFloor);
    }

    public void setResizeEvent() {
        this.pane.widthProperty().addListener((observable) -> {
            mazeDisplayer.reDrawMaze(pane);
        });

        this.pane.heightProperty().addListener((observable) -> {
            mazeDisplayer.reDrawMaze(pane);
        });
    }

    @Override
    public void update( Observable o, Object arg ) {

            if (arg != null && arg.equals("Character Moved"))
                moveCharacter( viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn() );

            else if (arg != null && arg.equals("Maze Solution"))
                pool.execute( () -> displaySolution( viewModel.getSolution() ));

            else
                pool.execute( () -> displayMaze( viewModel.getMaze() ));

    }

    public void moveCharacter( int row , int col ){
        mazeDisplayer.setCharacterPosition(row, col);
        this.characterPositionRow.set(row + "");
        this.characterPositionColumn.set(col + "");
//        this.characterPositionFloor.set(characterPositionFloor.getValue());
        mazeDisplayer.drawCharacter();
    }

    @Override
    public void displayMaze( int[][] maze ) {

        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setGoalPosition(viewModel.getGoalPositionRow() , viewModel.getGoalPositionCol());
        mazeDisplayer.drawMaze(pane);

        mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow() , viewModel.getCharacterPositionColumn());
        this.characterPositionRow.set(characterPositionRow.getValue() + "");
        this.characterPositionColumn.set(characterPositionColumn.getValue() + "");

        btn_solveMaze.setDisable(false);
        btn_generateMaze.setDisable(false);
    }

    //<editor-fold desc="View -> ViewModel">
    public void generateMaze() {

        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);

        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());

        mazeDisplayer.setSol(null);
        viewModel.generateMaze(width, heigth);

    }

    public void solveMaze() {

        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);

        //showAlert("Solving Maze...");
        viewModel.solveMaze();

    }

    public void displaySolution( Solution sol ){

        mazeDisplayer.setSol(sol);
        mazeDisplayer.drawSolution();

        btn_generateMaze.setDisable(false);

    }
    //</editor-fold>

    public static void showAlert( String alertMessage ) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed( KeyEvent keyEvent ) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    public void About( ActionEvent actionEvent ) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 400, 350);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {
        }
    }

    public void mouseClicked( MouseEvent mouseEvent ) {
        this.mazeDisplayer.requestFocus();
    }
    //endregion

    public void numericOnly( KeyEvent event ) {
        KeyCode c = event.getCode();
    }

    public static void close() throws InterruptedException {
        pool.shutdown();
        pool.awaitTermination(3 , TimeUnit.SECONDS);
    }

}
