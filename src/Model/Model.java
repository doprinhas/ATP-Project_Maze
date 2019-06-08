package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import sun.nio.ch.ThreadPool;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;


public class Model extends Observable implements IModel{

    private Maze maze;
    private Position characterPos;

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    private ExecutorService pool;


    public Model() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

        pool = Executors.newFixedThreadPool(Configurations.getThreadPoolSize());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    @Override
    public void generateMaze(int width , int height) {
        try {
            pool.execute(() -> communicateWithServer_MazeGenerating(width, height));

        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
        }
    }

    private synchronized void communicateWithServer_MazeGenerating(int width, int height)
    {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{width, height};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[100000];
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
                        characterPos = maze.getStartPosition();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setChanged();
                    notifyObservers("New Maze");
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[][] getMaze() {
        return maze.toIntArray();
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                characterPos.changeRowBy(-1);
                break;
            case DOWN:
                characterPos.changeRowBy(1);
                break;
            case RIGHT:
                characterPos.changeColumnBy(1);
                break;
            case LEFT:
                characterPos.changeColumnBy(-1);
                break;
            case HOME:
                characterPos = maze.getStartPosition();
        }
        setChanged();
        notifyObservers("Character Moved");
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPos.getRowIndex();
    }

    @Override
    public int getCharacterPositionCol() {
        return characterPos.getColumnIndex();
    }

    @Override
    public void close() {

    }
}
