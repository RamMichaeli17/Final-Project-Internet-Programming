import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class handles server.Matrix-related tasks
 */
public class MatrixIHandler implements IHandler {
    private Matrix matrix;
    private Index startIndex, endIndex;
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

        this.resetMembers();

        boolean doWork = true;
        // handle client's tasks
        while(doWork){
          //We use switch-case in order to get commands from client (each task has case).

            switch (objectInputStream.readObject().toString()){
                //TODO: each case for each task
                /*case "matrix":{
                    // client will now send a 2d array. handler will create a matrix object
                    int[][] tempArray = (int[][])objectInputStream.readObject();
                    System.out.println("Server: Got 2d array");
                    this.matrix = new Matrix(tempArray);
                    this.matrix.printMatrix();
                    break;
                }

                case "getNeighbors":{
                    // handler will receive an index, then compute its neighbors
                    Index tempIndex = (Index)objectInputStream.readObject();
                    if(this.matrix!=null){
                        List<Index> neighbors = new ArrayList<>(this.matrix.getNeighbors(tempIndex));
                        System.out.println("Server: neighbors of "+ tempIndex + ":  " + neighbors);
                        // send to socket's outputstream
                        objectOutputStream.writeObject(neighbors);
                    }
                    break;

                }*/

                case "1":{ //dfs
                    //we convert 2D array to primitive matrix
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();

                    System.out.println("Task1 is running...\nServer: Got 2d array from client");
                    List<HashSet<Index>> listOFSCCs;
                    //calling method will find the SCCs
                    ThreadLocalDFSVisit threadLocalDFSVisit=new ThreadLocalDFSVisit();
                    listOFSCCs=threadLocalDFSVisit.findSCCs(primitiveMatrix);
                    //transfers to client the answer
                    objectOutputStream.writeObject(listOFSCCs);
                    System.out.println("Task1 finish\n");
                    break;
                }
                case "2": { //bfs
                    int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client");
                    this.matrix=new Matrix(primitiveMatrix);
                    matrix.printMatrix();
                    Index src, dest;
                    System.out.println("Please enter source index");
                    src=(Index)objectInputStream.readObject();
                    System.out.println("From client - source index is :"+ src);
                    System.out.println("Please enter destination index");
                    dest=(Index)objectInputStream.readObject();
                    System.out.println("From client - destination index is :"+ dest);
                    TraversableMatrix traversable2 = new TraversableMatrix(this.matrix);
                    traversable2.setStartIndex(src);
                    traversable2.setEndIndex(dest);
                    ParallelBFS bfs2 = new ParallelBFS();
                    List<List<Index>> minPaths;
                    minPaths = bfs2.parallelBFS(traversable2,traversable2.getOrigin(),traversable2.getDestination());
                    System.out.println(minPaths);
                    objectOutputStream.writeObject(minPaths);
//                    System.out.println("From server - find shortest paths:");
//                    System.out.println("start" + traversable2.getOrigin() + "end " + traversable2.getDestination());
                    //TODO: create an object of BFS class and call it function
                    System.out.println("Task2 finish");
                    break;
                }
                case "3":{ //submarines task

                    int[][] tempArray = (int[][]) objectInputStream.readObject();//the matrix that we send(now we read)
                    List<HashSet<Index>> listOFHashsets;
                    ThreadLocalDFSVisit<Index> sub = new ThreadLocalDFSVisit<>();
                    listOFHashsets=sub.findSCCs(tempArray);//list of SCC
                    int size = sub.subCheck(listOFHashsets, tempArray);
                    objectOutputStream.writeObject(size);
                    System.out.println("Task3 finish");
                    break;
                }
                case "4":{ //the lightest paths with bfs / bellmanFord
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();
                    Index src, dest;
                    System.out.println("Server: Got 2d array from client");
                    this.matrix=new Matrix(primitiveMatrix);
                    matrix.printMatrix();
                    System.out.println("Please enter source index");
                    src=(Index)objectInputStream.readObject();
                    System.out.println("From client - source index is :"+ src);
                    System.out.println("Please enter destination index");
                    dest=(Index)objectInputStream.readObject();
                    System.out.println("From client - destination index is :"+ dest);
                    TraversableMatrix traversable4 = new TraversableMatrix(this.matrix);
                    traversable4.setStartIndex(src);
                    traversable4.setEndIndex(dest);
                    ThreadLocalBellmanFord bellmanFord= new ThreadLocalBellmanFord();
                    LinkedList<List<Index>> minWeightList;
                    //TODO: solve the problem- the method return an empty list
                    minWeightList = bellmanFord.findPathsBellmanFord(traversable4, traversable4.getOrigin(), traversable4.getDestination());
                    System.out.println(minWeightList);
                    objectOutputStream.writeObject(minWeightList);
                    System.out.println("Task4 finish\n");
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
