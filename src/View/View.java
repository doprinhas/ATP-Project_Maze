package View;


import Server.Configurations;
import ViewModel.ViewModel;
import algorithms.search.Solution;
import com.sun.deploy.ui.ProgressDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.awt.*;
import java.awt.ScrollPane;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class View implements Observer, IView{

    public MazeDisplayer mazeDisplayer;
    public javafx.scene.control.ScrollPane ScrollPane;

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

    private double zoom;
    private double zoomChange;
    private final double ZOOM_PERCENTAGE = 0.5;


    @FXML

    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;

    public void initialize( ViewModel viewModel , Stage mainStage , Scene mainScene ){
        if ( viewModel == null || mainStage == null || mainScene == null)
            return;

        this.zoom = 1;
        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;

        ScrollPane.setStyle("-fx-background: black;");
        ScrollPane.setPannable(true);
        bindProperties();
        setResizeEvent();

    }

    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
        lbl_FloorNum.textProperty().bind(this.characterPositionFloor);
    }

    public void setResizeEvent() {
        this.ScrollPane.widthProperty().addListener((observable) -> {
            mazeDisplayer.reDrawMaze(ScrollPane , zoom);
        });

        this.ScrollPane.heightProperty().addListener((observable) -> {
            mazeDisplayer.reDrawMaze(ScrollPane , zoom);
        });

    }

    @Override
    public void update( Observable o, Object arg ) {

            if (arg != null && arg.equals("Character Moved"))
                moveCharacter( viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn() );

            else if (arg != null && arg.equals("Maze Solution"))
                displaySolution( viewModel.getSolution() );

            else
                displayMaze( viewModel.getMaze() );

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

        zoom = 1;

        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setGoalPosition(viewModel.getGoalPositionRow() , viewModel.getGoalPositionCol());
        mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow() , viewModel.getCharacterPositionColumn());
        mazeDisplayer.drawMaze(ScrollPane , zoom);

        btn_solveMaze.setDisable(false);
        btn_generateMaze.setDisable(false);
    }



    //<editor-fold desc="View -> ViewModel">
    public void generateMaze() {

        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);

        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        this.characterPositionRow.set("0");
        this.characterPositionColumn.set("0");

        mazeDisplayer.setSol(null);
        viewModel.generateMaze( heigth , width);

    }

    public void solveMaze() {

        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);


        viewModel.solveMaze();
        showAlert("Solving Maze..." , Alert.AlertType.INFORMATION);

    }

    public void displaySolution( Solution sol ){

        mazeDisplayer.setSol(sol);
        mazeDisplayer.drawSolution();

        btn_generateMaze.setDisable(false);
        btn_solveMaze.setDisable(false);

    }
    //</editor-fold>

    public static void showAlert( String alertMessage , Alert.AlertType type) {
        Alert alert = new Alert(type);
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

    public void numericOnly( KeyEvent event /*, TextField field*/) {

        if (event.getSource().toString().contains("txtfld_rowsNum"))
            checkInput(event , txtfld_rowsNum);
        if (event.getSource().toString().contains("txtfld_columnsNum"))
            checkInput(event , txtfld_columnsNum);

    }

    private void checkInput( KeyEvent event , TextField textField ){
        KeyCode c = event.getCode();
        String text = textField.getText();
        if (c.isLetterKey() || text.length() > 3) {
            textField.setText(text.substring(0 , text.length() - 1));
            showAlert("Please Enter Numbers Smaller Then 1000!" , Alert.AlertType.WARNING);
        }
    }

    public void zoomInOut( ScrollEvent event ){

        if (!event.isControlDown())
            return;

        double delta = event.getDeltaY();
        if ( delta > 0 )
            zoom += ZOOM_PERCENTAGE;
        else if ( delta < 0 )
            zoom -= ZOOM_PERCENTAGE;
        if ( zoom <= 1 )
            zoom = 1;
        else if (zoom >= 6)
            zoom = 6;

        System.out.println(zoom);
        mazeDisplayer.reDrawMaze( ScrollPane , zoom );
        event.consume();
    }

    public static void close() throws InterruptedException {
        MazeDisplayer.close();
    }

}
