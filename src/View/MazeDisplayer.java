package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class MazeDisplayer extends Canvas {
    
    private int[][] maze;
    private int characterRowPosition;
    private int characterColPosition;

    public MazeDisplayer() {
        characterRowPosition = 0;
        characterColPosition = 0;
    }
    
    public void setMaze( int[][] maze ){
        if ( maze != null ){
            this.maze = maze;
            drawMaze();
        }
    }

    public void setCharacterPosition( int row , int col ){
        if ( row < maze.length && col < maze[row].length ){
            characterColPosition = col;
            characterRowPosition = row;
            redrawCharacter();
        }
    }

    // drawer variables
    GraphicsContext gc;

    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    private void redrawCharacter() {

    }

    private void drawMaze() {
        if ( maze == null )
            return;

        gc = getGraphicsContext2D();

        canvasHeight = getHeight();
        canvasWidth = getWidth();
        cellHeight = canvasHeight / maze.length;
        cellWidth = canvasWidth / maze[0].length;

        try {
            Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));

        } catch (FileNotFoundException e) {

        }
    }

    private Image getImage( String path ){
        Image image = null;
        try {
            image = new Image(new FileInputStream(path));
        } catch (FileNotFoundException e) {

        }
        return image;
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameCharacter = new SimpleStringProperty();

    public void setImageFileNameWall( String imageFileNameWall ) {
        if ( imageFileNameWall != null )
            this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameCharacter( String imageFileNameCharacter ) {
        if ( imageFileNameCharacter != null )
            this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public String getImageFileNameWall() { return ImageFileNameWall.get(); }

}
