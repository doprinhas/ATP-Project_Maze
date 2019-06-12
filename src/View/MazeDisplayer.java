package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class MazeDisplayer extends Canvas {
    
    private int[][] maze;
    private Solution mazeSol;
    private int characterRowPosition;
    private int characterColPosition;
    private int characterFloorPosition;
    private int endPositionRow;
    private int ensPositionCol;
    private boolean isFinished;

    private String FINISHD_MAZE_MASSAGE = "Well Done! You Finished The Maze\n" +
                                            "Generate new maze to start over...";


    public MazeDisplayer() {
        characterRowPosition = 0;
        characterColPosition = 0;
        isFinished = false;
        gc = getGraphicsContext2D();
    }

    public void setMaze( int[][] maze ){
        if ( maze != null )
            this.maze = maze;
        isFinished = false;
    }

    public void setSol ( Solution sol ){
        this.mazeSol = sol;
    }

    public void setGoalPosition( int row , int col ){

        if ( row < maze.length && col < maze[0].length ){
            endPositionRow = row;
            ensPositionCol = col;
        }

    }

    public void setCharacterPosition( int row , int col ){

        if ( (row < maze.length && col < maze[row].length) && !isFinished ){
            gc.clearRect(characterColPosition*cellWidth +1, characterRowPosition*cellHeight +1, cellWidth-2, cellHeight-2);
            characterColPosition = col;
            characterRowPosition = row;
            drawCharacter();
        }
        if ( isFinished || (row == endPositionRow && col == ensPositionCol) ) {
            isFinished = true;
            View.showAlert(FINISHD_MAZE_MASSAGE);
        }

    }

    // drawer variables
    GraphicsContext gc;

    private double canvasHeight;
    private double canvasWidth;
    private double cellHeight;
    private double cellWidth;

    public void drawCharacter() {
        Image charImage = getImage(getImageFileNameCharacter());
        gc.drawImage(charImage , characterColPosition * cellWidth +1 ,characterRowPosition * cellHeight +1 ,
                cellWidth -2, cellHeight-2);
    }

    private void drawGoalPos() {
        Image goalPos = getImage(getImageFileNameEnd());
        gc.drawImage( goalPos  , ensPositionCol * cellWidth +1 , endPositionRow * cellHeight +1 ,
                cellWidth -2, cellHeight-2);
    }

    public void drawSolution(){

        Image solImage = getImage(getImageFileNameSol());
        Position p;
        ArrayList<AState> path = mazeSol.getSolutionPath();
        for (AState pos : path){
            p = (Position)pos.getM_state();
            gc.drawImage( solImage  , p.getColumnIndex() * cellWidth +1, p.getRowIndex() * cellHeight +1,
                    cellWidth -2, cellHeight-2);
        }
        drawCharacter();
        drawGoalPos();
    }

    public void drawMaze( javafx.scene.layout.Pane pane ) {
        if ( maze == null )
            return;

        Image wallImage = getImage(getImageFileNameWall());
        Image endImage = getImage(getImageFileNameEnd());
        if ( wallImage == null  || endImage == null )
            return;

        setCanvasSize(pane);
        gc.clearRect(0, 0, getWidth(), getHeight());

        //drawing maze walls
        for ( int i = 0 ; i < maze.length ; i++ )
            for ( int j = 0 ; j < maze[i].length ; j++ )
                if ( maze[i][j] == 1 )
                    gc.drawImage( wallImage  , j * cellWidth , i * cellHeight , cellWidth , cellHeight);
        //drawing finish point and character
        drawGoalPos();
        drawCharacter();
    }

    public void reDrawMaze( javafx.scene.layout.Pane pane ){
        drawMaze(pane);
        if (mazeSol != null)
            drawSolution();
    }

    private void setCanvasSize( javafx.scene.layout.Pane pane ){
        setWidth(pane.getWidth());
        setHeight(pane.getHeight());
        canvasHeight = getHeight();
        canvasWidth = getWidth();
        cellHeight = canvasHeight / maze.length;
        cellWidth = canvasWidth / maze[0].length;
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
    private StringProperty ImageFileNameEnd = new SimpleStringProperty();
    private StringProperty ImageFileNameSol = new SimpleStringProperty();

    public void setImageFileNameWall( String imageFileNameWall ) {
        if ( imageFileNameWall != null )
            this.ImageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNameEnd( String imageFileNameEnd ) {
        if ( imageFileNameEnd != null )
            this.ImageFileNameEnd.set(imageFileNameEnd);
    }

    public void setImageFileNameCharacter( String imageFileNameCharacter ) {
        if ( imageFileNameCharacter != null )
            this.ImageFileNameCharacter.set(imageFileNameCharacter);
    }

    public void setImageFileNameSol( String imageFileNameSol ) {
        if ( imageFileNameSol != null )
            this.ImageFileNameSol.set(imageFileNameSol);
    }

    public String getImageFileNameCharacter() {
        return ImageFileNameCharacter.get();
    }

    public String getImageFileNameWall() { return ImageFileNameWall.get(); }

    public String getImageFileNameEnd() { return ImageFileNameEnd.get(); }

    public String getImageFileNameSol() { return ImageFileNameSol.get(); }

    //imageFileSolWay

}
