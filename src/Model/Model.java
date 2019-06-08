package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;


public class Model extends Observable implements IModel{

    private Maze maze;

    private int characterPositionRow;
    private int characterPositionColumn;

    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;

    final private String SAVE_PATH = System.getProperty("java.io.tmpdir") + "\\Saved";

    private ExecutorService pool;


    public Model() {
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());

        pool = Executors.newFixedThreadPool(Configurations.getThreadPoolSize());

        File savedDir = new File(SAVE_PATH);
        if(!savedDir.exists())
        {
            try
            {
                if (!savedDir.mkdir())
                    throw new Exception("Cant create folder");
            }
            catch(Exception e)
            {
                System.out.println("Cant create folder");
                e.printStackTrace();
            }
        }
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
                        Position characterPos = maze.getStartPosition();
                        characterPositionRow = characterPos.getRowIndex();
                        characterPositionColumn = characterPos.getColumnIndex();


                        maze.print();

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
                if(maze.isAPass(characterPositionRow-1, characterPositionColumn))
                    characterPositionRow--;
                break;
            case DOWN:
                if(maze.isAPass(characterPositionRow+1, characterPositionColumn))
                    characterPositionRow++;
                break;
            case RIGHT:
                if(maze.isAPass(characterPositionRow, characterPositionColumn+1))
                    characterPositionColumn++;
                break;
            case LEFT:
                if(maze.isAPass(characterPositionRow, characterPositionColumn-1))
                    characterPositionColumn--;
                break;
            case HOME:
                characterPositionRow = maze.getStartPosition().getRowIndex();
                characterPositionColumn = maze.getStartPosition().getColumnIndex();
        }
        setChanged();
        notifyObservers("Character Moved");
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
    public int getGoalPositionRow(){ return maze.getGoalPosition().getRowIndex(); }

    @Override
    public int getGoalPositionCol(){ return maze.getGoalPosition().getColumnIndex(); }

    @Override
    public boolean saveGame(String name) {
        try {
            File saveMaze = new File(SAVE_PATH + "\\" + name + "Maze");
            File saveCharPos = new File(SAVE_PATH + "\\" + name + "CharPos");
            if(!saveMaze.createNewFile())
                return false;
            if(!saveCharPos.createNewFile())
            {
                saveMaze.delete();
                return false;
            }

            FileOutputStream out = new FileOutputStream(SAVE_PATH + "\\" + name + "Maze");
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(out);
            compressor.write(maze.toByteArray());

            FileWriter fileWriter = new FileWriter(saveCharPos);
            fileWriter.write(characterPositionRow + "\n");
            fileWriter.write(characterPositionColumn);


            compressor.close();

            fileWriter.flush();
            fileWriter.close();

            return true;
        }
        catch(IOException e)
        {
            File saveMaze = new File(SAVE_PATH + "\\" + name + "Maze");
            File saveCharPos = new File(SAVE_PATH + "\\" + name + "CharPos");
            if(saveMaze.exists())
                saveMaze.delete();
            if(saveCharPos.exists())
                saveCharPos.delete();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> getAllSavedMazes() {
        File savedMazeFolder = new File(SAVE_PATH);
        List<String> res = new ArrayList<>();

        for (File fileEntry : savedMazeFolder.listFiles()) {
            String fileName = fileEntry.getName();
            if (fileName.endsWith("Maze"))
                res.add(fileName.substring(0,fileName.length()-4));
        }

        return res;
    }

    @Override
    public void loadGame(String name) throws FileNotFoundException{

        FileInputStream inStreamMaze = new FileInputStream(SAVE_PATH + "\\" + name + "Maze");
        MyDecompressorInputStream decompressor = new MyDecompressorInputStream(inStreamMaze);

        File charPosFile = new File(SAVE_PATH + "\\" + name + "charPos");
        FileReader fileReader = new FileReader(charPosFile);
        BufferedReader br = new BufferedReader(fileReader);

        int arraySize = 10000;
        byte[] inMaze;
        do {
            inMaze = new byte[arraySize];
            arraySize += 10000;
        }
        while(decompressor.read(inMaze) != arraySize); // Checking if the byteArray is big enough

        try {
            characterPositionRow = Integer.parseInt(br.readLine());
            characterPositionColumn = Integer.parseInt(br.readLine());
            maze = new Maze(inMaze);

            setChanged();
            notifyObservers("Load Maze");
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public void close() {
        try{
            pool.shutdown();
            pool.awaitTermination(3, TimeUnit.SECONDS);
            stopServers();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
