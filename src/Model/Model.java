package Model;

import javafx.scene.input.KeyCode;

import java.util.Observable;

public class Model extends Observable implements IModel{

    @Override
    public void generaeMaze(int width , int height) {
    }

    @Override
    public int[][] getMaze() {
        return new int[0][];
    }

    @Override
    public void moveCharacter(KeyCode movement) {

    }

    @Override
    public int getCharacterPositionRow() {
        return 0;
    }

    @Override
    public int getCharacterPositionCol() {
        return 0;
    }

    @Override
    public void close() {

    }
}
