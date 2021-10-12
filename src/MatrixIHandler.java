import java.io.*;
import java.util.*;

/**
 * This class implements IHandler interface.
 * This class handles with matrix related tasks.
 * In this class we use the 'adapter' design pattern
 */

public class MatrixIHandler implements IHandler {
    private Matrix matrix;
    private Index startIndex, endIndex;
    /**
     * By using volatile we are asking to save this boolean in RAM and not in a local thread.
     * Using volatile is a way of making class thread safe.
     * Advantage: by saving to the RAM we will always keep the most updated copy of the variable.
     * Disadvantage: the time of accessing to the RAM is bigger than accessing to the CPU cache.
     */
    private volatile boolean doWork = true;

    private void resetMembers() {
        this.matrix = null;
        this.startIndex = null;
        this.endIndex = null;
        this.doWork = true;
    }


    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        /*
        Send data as bytes.
        Read data as bytes then transform to meaningful data
        ObjectInputStream and ObjectOutputStream can read and write both primitives and objects
         */
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);
        // in case we want to reuse the requestHandler of the same user
        this.resetMembers();
        boolean doWork = true;
        // handle client's tasks
        while(doWork){
          //We use switch-case in order to get commands from client (each task has a case).

            switch (objectInputStream.readObject().toString()){

                case "1":{ //Find all strongly connected components
                    //we convert 2D array to primitive matrix
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();
                    System.out.println("Task 1 - Find all strongly connected components is running...\nServer: Got 2d array from client");
                    List<HashSet<Index>> listOFSCCs;
                    //calling method will find the SCCs
                    ThreadLocalDFSVisit threadLocalDFSVisit=new ThreadLocalDFSVisit();
                    listOFSCCs=threadLocalDFSVisit.findSCCs(primitiveMatrix);
                    //transfers to client the answer
                    objectOutputStream.writeObject(listOFSCCs);
                    System.out.println("Task 1 finished\n");
                    break;
                }

                case "2.1": { //Find all shortest paths from source to destination
                    int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                    System.out.println("Task 2.1 - Find all shortest paths from source to destination is running...\nServer: Got 2d array from client");
                    this.matrix=new Matrix(primitiveMatrix);
                    matrix.printMatrix();
                    Index src, dest;
                    src=(Index)objectInputStream.readObject();
                    System.out.println("From client - source index is: "+ src);
                    dest=(Index)objectInputStream.readObject();
                    System.out.println("From client - destination index is: "+ dest);
                    TraversableMatrix traversable21 = new TraversableMatrix(this.matrix);
                    traversable21.setStartIndex(src);
                    traversable21.setEndIndex(dest);
                    ThreadLocalBFS threadLocalBFS = new ThreadLocalBFS();
                    List<List<Index>> minPaths;
                    minPaths = threadLocalBFS.findShortestPathsBFS(traversable21,traversable21.getOrigin(),traversable21.getDestination());
                    objectOutputStream.writeObject(minPaths);
                    System.out.println("Task 2.1 finished\n");
                    break;
                }

                case "2.2": { //*Parallel* Find all shortest paths from source to destination
                    int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                    System.out.println("Task 2.2 - Parallel - Find all shortest paths from source to destination is running...\nServer: Got 2d array from client");
                    this.matrix=new Matrix(primitiveMatrix);
                    matrix.printMatrix();
                    Index src, dest;
                    src=(Index)objectInputStream.readObject();
                    System.out.println("From client - source index is: "+ src);
                    dest=(Index)objectInputStream.readObject();
                    System.out.println("From client - destination index is: "+ dest);
                    TraversableMatrix traversable22 = new TraversableMatrix(this.matrix);
                    traversable22.setStartIndex(src);
                    traversable22.setEndIndex(dest);
                    ParallelBFS parallelBFS = new ParallelBFS();
                    List<List<Index>> minPaths;
                    minPaths = parallelBFS.findShortestPathsParallelBFS(traversable22,traversable22.getOrigin(),traversable22.getDestination());
                    objectOutputStream.writeObject(minPaths);
                    System.out.println("Task 2.2 finished\n");
                    break;
                }

                case "3":{ //Find number of battleships

                    int[][] tempArray = (int[][]) objectInputStream.readObject();//the matrix that we send(now we read)
                    System.out.println("Task 3 - Find number of battleships is running...\nServer: Got 2d array from client");
                    List<HashSet<Index>> listOFHashsets;
                    ThreadLocalDFSVisit<Index> threadLocalDFSVisit = new ThreadLocalDFSVisit<>();
                    listOFHashsets=threadLocalDFSVisit.findSCCs(tempArray);//list of SCC
                    int size = threadLocalDFSVisit.battleshipCheck(listOFHashsets, tempArray);
                    objectOutputStream.writeObject(size);
                    System.out.println("Task 3 finished\n");
                    break;
                }

                case "4":{ //Find all lightest paths from source to destination
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();
                    System.out.println("Task 4 - Find all lightest paths from source to destination is running...\nServer: Got 2d array from client");
                    this.matrix=new Matrix(primitiveMatrix);
                    matrix.printMatrix();
                    Index src, dest;
                    src=(Index)objectInputStream.readObject();
                    System.out.println("From client - source index is: "+ src);
                    dest=(Index)objectInputStream.readObject();
                    System.out.println("From client - destination index is: "+ dest);
                    TraversableMatrix traversable4 = new TraversableMatrix(this.matrix);
                    traversable4.setStartIndex(src);
                    traversable4.setEndIndex(dest);
                    ThreadLocalBellmanFord threadLocalBellmanFord= new ThreadLocalBellmanFord();
                    LinkedList<List<Index>> minWeightList;
                    minWeightList = threadLocalBellmanFord.findLightestPathsBellmanFord(traversable4, traversable4.getOrigin(), traversable4.getDestination());
                    objectOutputStream.writeObject(minWeightList);
                    System.out.println("Task 4 finished\n");
                    break;
                }

                case "stop":{
                    doWork = false;
                    break;
                }
            }
        }
    }
}
