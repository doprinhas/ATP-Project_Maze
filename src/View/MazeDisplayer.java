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
            gc.clearRect(characterColPosition*cellWidth +1, characterRowPosition*cellHeight +1, cellWidth-1, cellHeight-1);
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

    public void redrawCharacter() {
        Image charImage = getImage(ImageFileNameCharacter.get());
        gc.drawImage(charImage , characterColPosition * cellHeight , characterRowPosition * cellWidth , cellWidth , cellHeight);
    }

    public void drawMaze() {
        if ( maze == null )
            return;

        Image wallImage = getImage(ImageFileNameWall.get());
        Image charImage = getImage(ImageFileNameCharacter.get());
        if ( wallImage == null || charImage == null )
            return;

        gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        canvasHeight = getHeight();
        canvasWidth = getWidth();
        cellHeight = canvasHeight / maze.length;
        cellWidth = canvasWidth / maze[0].length;

        for ( int i = 0 ; i < maze.length ; i++ )
            for ( int j = 0 ; j < maze[i].length ; j++ )
                if ( maze[i][j] == 1 )
                    gc.drawImage( wallImage , i * cellWidth , j * cellHeight , cellHeight , cellWidth );



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
