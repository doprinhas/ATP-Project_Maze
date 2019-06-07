package View;


import ViewModel.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

public class View implements Observer, IView{

    public MazeDisplayer mazeDisplayer;

    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;

    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;

    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;

    public StringProperty characterPositionRow = new SimpleStringProperty("1");
    public StringProperty characterPositionColumn = new SimpleStringProperty("1");


    @FXML

    private ViewModel viewModel;
    private Scene mainScene;
    private Stage mainStage;

    public void initialize( ViewModel viewModel , Stage mainStage , Scene mainScene ){
        if ( viewModel == null || mainStage == null || mainScene == null)
            return;

        this.viewModel = viewModel;
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        bindProperties();
        setResizeEvent();
    }

    private void bindProperties() {
        lbl_rowsNum.textProperty().bind(this.characterPositionRow);
        lbl_columnsNum.textProperty().bind(this.characterPositionColumn);
    }

    public void setResizeEvent() {
        this.mainScene.widthProperty().addListener((observable, oldValue, newValue) -> {
//            mazeDisplayer.drawMaze();
        });

        this.mainScene.heightProperty().addListener((observable, oldValue, newValue) -> {
//            mazeDisplayer.drawMaze();
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg != null && ((String)arg).equals("Character Moved")) {
            moveCharacter();
            btn_generateMaze.setDisable(false);
        }
        else {
            displayMaze(viewModel.getMaze());
            btn_generateMaze.setDisable(false);
        }
    }

    public void moveCharacter(){
        mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
        mazeDisplayer.redrawCharacter();
    }

    @Override
    public void displayMaze(int[][] maze) {
        mazeDisplayer.setMaze(maze);
        mazeDisplayer.setCharacterPosition(viewModel.getCharacterPositionRow(), viewModel.getCharacterPositionColumn());
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
        btn_solveMaze.setDisable(false);
    }

    //<editor-fold desc="View -> ViewModel">
    public void generateMaze() {
        int heigth = Integer.valueOf(txtfld_rowsNum.getText());
        int width = Integer.valueOf(txtfld_columnsNum.getText());
        btn_generateMaze.setDisable(true);
        btn_solveMaze.setDisable(true);
        viewModel.generateMaze(width, heigth);
    }

    public void solveMaze(ActionEvent actionEvent) {

        showAlert("Solving maze..");
    }
    //</editor-fold>

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }


    public void About(ActionEvent actionEvent) {
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

    public void mouseClicked(MouseEvent mouseEvent) {
        this.mazeDisplayer.requestFocus();
    }
    //endregion

}
