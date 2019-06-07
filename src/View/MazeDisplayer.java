package View;

import javafx.scene.canvas.Canvas;

public class MazeDisplayer extends Canvas {
    
    private int[][] maze;
    private int characterRowPosition;
    private int characterColPosition;
    
    public void setMaze(int[][] maze){
        if (maze != null){
            this.maze = maze;
            redraw();
        }
    }

    private void redraw() {
    }
}
