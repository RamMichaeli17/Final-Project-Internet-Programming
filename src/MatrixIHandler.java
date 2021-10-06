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
    public List<HashSet<Index>> findSCCs(int[][] source)
    {
        HashSet<HashSet<Index>> listOFHashsets;
        List<Index> listOne;
        Matrix sourceMatrix = new Matrix(source);
        sourceMatrix.printMatrix();

        //parallelDFSTraverse need to get traversable<T> , list<HashSet<Index>>> :
        TraversableMatrix myTraversableM = new TraversableMatrix(sourceMatrix);
        listOne = sourceMatrix.findAllOnes();
        System.out.println(listOne);
        //set the first index
        myTraversableM.setStartIndex(myTraversableM.getStartIndex());
        ThreadLocalDFSVisit<Index> algo = new ThreadLocalDFSVisit<>();

        //call to parallelDFSTraverse method
        listOFHashsets = algo.parallelDFSTraverse(myTraversableM, listOne);
        List<HashSet<Index>> list = listOFHashsets.stream().sorted(Comparator.comparingInt(HashSet::size))
                .collect(Collectors.toList());

        return list;

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
                }

                 case "getReachables":{
                    // handler will receive an index, then compute its neighbors
                    Index tempIndex = (Index)objectInputStream.readObject();
                    if(this.matrix!=null){
                        List<Index> reachables = new ArrayList<>(this.matrix.getReachables(tempIndex));
                        System.out.println("Server: neighbors of "+ tempIndex + ":  " + reachables);
                        // send to socket's outputstream
                        objectOutputStream.writeObject(reachables);
                    }
                }

                //cases added
                case "start index":{
                    this.startIndex = (Index)objectInputStream.readObject();
                    break;
                }

                case "end index":{
                    this.endIndex = (Index)objectInputStream.readObject();
                    break;
                }*/

                case "1":{ //dfs
                    //we convert 2D array to primitive matrix
                    int[][] primitiveMatrix = (int[][])objectInputStream.readObject();

                    System.out.println("Task1 is running...\nServer: Got 2d array from client");
                    List<HashSet<Index>> listOFSCCs;
                    //calling method will find the SCCs

                    listOFSCCs=findSCCs(primitiveMatrix);
                    //transfers to client the answer
                    objectOutputStream.writeObject(listOFSCCs);
                    System.out.println("Task1 finish\n");
                    break;
                }
              /*  case "2": { //bfs
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

                    System.out.println("From server - find shortest paths:");
                    System.out.println("start" + traversable2.getOrigin() + "end " + traversable2.getDestination());
                    //TODO: create an object of BFS class and call it function

                    System.out.println("Task2 finish");
                    break;
                }
                case "3":{ //submarines
                    int[][] primitiveMatrix = (int[][]) objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client");
                    //TODO: pull task 3 ,create an object of it class and call it function

                    System.out.println("Task3 finish");
                  break;
                }*/
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
