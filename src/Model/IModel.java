package Model;

import javafx.scene.input.KeyCode;

import java.io.FileNotFoundException;
import java.util.List;

public interface IModel {

    //Maze
    void generateMaze(int width , int height);
    int [][] getMaze();

    //Character
    void moveCharacter(KeyCode movement);
    int getCharacterPositionRow();
    int getCharacterPositionCol();
    int getGoalPositionRow();
    int getGoalPositionCol();

    //Game
    boolean saveGame(String name);
    List<String> getAllSavedMazes();
    void loadGame(String name) throws FileNotFoundException;

    //
    void close();

}
