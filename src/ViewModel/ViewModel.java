package ViewModel;

import Model.IModel;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    private IModel model;

    public ViewModel(IModel model){
        this.model = model;
    }

    //<editor-fold desc="Take care Observable">
    @Override
    public void update(Observable o, Object arg) {
        if (o==model){
            //Notify my observer (View) that I have changed
            setChanged();
            notifyObservers(arg);
        }
    }
    //</editor-fold>

    //<editor-fold desc="ViewModel Functionality">
    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    public int[][] getMaze() {
        return model.getMaze();
    }

    public int getCharacterPositionRow() {
        //return characterPositionRowIndex;
        return model.getCharacterPositionRow();
    }

    public int getCharacterPositionColumn() {
        //return characterPositionColumnIndex;
        return model.getCharacterPositionCol();
    }

    public int getGoalPositionRow(){ return model.getGoalPositionRow(); }

    public int getGoalPositionCol(){ return model.getGoalPositionCol(); }
    //</editor-fold>

}
