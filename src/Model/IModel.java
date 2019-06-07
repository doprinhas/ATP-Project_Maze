package Model;

import javafx.scene.input.KeyCode;

public interface IModel {

    //Maze
    void generateMaze(int width , int height);
    int [][] getMaze();

    //Character
    void moveCharacter(KeyCode movement);
    int getCharacterPositionRow();
    int getCharacterPositionCol();

    //
    void close();

}
