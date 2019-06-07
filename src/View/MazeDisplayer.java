package View;

import algorithms.mazeGenerators.Maze;
import javafx.scene.canvas.Canvas;

public class MazeDisplayer extends Canvas {
    
    private Maze maze;
    private int characterRowPosition;
    private int characterColPosition;
    
    public void setMaze(Maze maze){
        if (maze != null){
            this.maze = maze;
            redraw();
        }
    }

    private void redraw() {
    }
}
