package Model;

import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Model extends Observable implements IModel{

    public Model() {

    }

    //<editor-fold desc="Servers">
    public void startServers() {

    }

    public void stopServers() {

    }
    //</editor-fold>

    //<editor-fold desc="Character">
    private int characterPositionRow = 1;
    private int characterPositionColumn = 1;
    //</editor-fold>

    //<editor-fold desc="Maze">
    private int[][] maze = { // a stub...
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1},
            {0, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1},
            {1, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
            {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 1},
            {1, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1}
    };

    private int[][] generateRandomMaze(int width, int height) {
        Random rand = new Random();
        maze = new int[width][height];
        for (int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {
                maze[i][j] = Math.abs(rand.nextInt() % 2);
            }
        }
        return maze;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    @Override
    public int[][] getMaze() {
        return maze;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionCol() {
        return characterPositionColumn;
    }

    @Override
    public void close() {
    }
    //</editor-fold>

    //<editor-fold desc="Model Functionality">
    @Override
    public void generateMaze(int width, int height) {
        //Generate maze
        maze = generateRandomMaze(width,height);

        setChanged(); //Raise a flag that I have changed
        notifyObservers("Generate Maze"); //Wave the flag so the observers will notice

    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                characterPositionRow--;
                break;
            case DOWN:
                characterPositionRow++;
                break;
            case RIGHT:
                characterPositionColumn++;
                break;
            case LEFT:
                characterPositionColumn--;
                break;
            case HOME:
                characterPositionRow = 0;
                characterPositionColumn = 0;
        }
        setChanged();
        notifyObservers("Character Moved");
    }
    //</editor-fold>
}
