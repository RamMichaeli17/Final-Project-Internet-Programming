import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class send messages and tasks to the server by the client.
 * There are 4 tasks that the client can choose - each task is on 2D matrix
 * all the tasks wrapped by switch-case
 */

public class Client {

    /**
     * @param matrix
     * @return Index with the row and column the client entered
     */
    public static Index indexRequest(Matrix matrix){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter row for Index");
        int row = scanner.nextInt();
        while(!(row>=0 && row< matrix.primitiveMatrix.length)){
            System.out.println("Not valid value! Please enter existing row at matrix");
            row = scanner.nextInt();
        }
        System.out.println("Please enter column for Index");
        int column = scanner.nextInt();
        while(!(column>=0 && column< matrix.primitiveMatrix[0].length)){
            System.out.println("Not valid value! Please enter column at matrix");
            column = scanner.nextInt();
        }
        return new Index(row , column);
    }

    private static void printOptionToClient() {
        System.out.println("Please choose one task: (all the tasks work on matrix)");
        System.out.println("1--> Find all SCCs [first task]");
        System.out.println("2.1--> Find all shortest paths from source to destination [second task]");
        System.out.println("2.2--> *Parallel* Find all shortest paths from source to destination [second task]");
        System.out.println("3--> Find number of battleships [third task]");
        System.out.println("4--> Find all lightest paths from source to destination [fourth task]");
        System.out.println("stop--> Exit the program");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException ,ClassCastException{

        Scanner scanner = new Scanner(System.in); //for the client inputs
        /**
         * local host, loopback address.
         * operational socket.
         */
        Socket socket =new Socket("127.0.0.1",8010);
        System.out.println("client: Created Socket");

        //warp InputStream & OutputStream in order to send/receive meaningful data
        ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());

        int[][] source1 = {
                {1, 0, 0},
                {0, 0, 1},
                {0, 0, 1}

                // output - [[(0,0)], [(1,2), (2,2)]]

//                {1, 0, 0, 0, 1},
//                {0, 0, 1, 0, 0},
//                {0, 0, 1, 0, 0},
//                {0, 0, 1, 0, 1},
//                {0, 0, 1, 0, 1},

                // output - [[(0,0)],[(0,4)] [(1,2), (2,2), (3,2), (4,2)], [(3,4), (4,4)]]

        };


        int[][] source2 = {
                {1,0,0},
                {1,1,0},
                {1,1,0}

                //output from (0,0) to (2,0) will be:
                // [[(0,0), (1,0), (2,0)], [(0,0), (1,1), (2,0)]]

//                {1, 0, 0, 0, 0},
//                {0, 1, 1, 0, 0},
//                {0, 1, 1, 0, 0},
//                {0, 0, 1, 0, 0},
//                {0, 0, 1, 0, 0}

                //output from (0,0) to (4,2) will be:
                // [[(0,0), (1,1), (2,1), (3,2), (4,2)], [(0,0), (1,1), (2,2), (3,2), (4,2)]]
        };


        int[][] source3 = {
                {0,0,0},
                {0,1,1},
                {0,1,1}

                //output - 2

//                {1, 1, 0, 0, 1},
//                {0, 0, 0, 0, 1},
//                {0, 0, 1, 0, 0},
//                {1, 0, 1, 0, 0},
//                {1, 0, 1, 0, 1}

                //output - 4
        };


        int[][] source4 ={
                {100,100,100},
                {300,900,500},
                {100,100,100}

                // output from (1,0) to (1,2) will be:
                // [[(1,0),(0,1),(1,2)],[(1,0),(2,1),(1,2)]] , with weight 900 - it also includes diagonals.

//
//                {100, 500, 100, 500},
//                {200, 500, 100, 100},
//                {300, 500, 100, 100},
//                {400, 500, 200, 800}

                // output from (0,0) to (1,2) will be:
                // [[(0,0), (0,1), (1,2)], [(0,0), (1,1), (1,2)]] , with weight 700 - it also includes diagonals.

        };


        boolean flag = false;
        while(!flag){ // while !stop
            printOptionToClient(); //print menu for client
            String result= scanner.next(); //next() input for string
            switch(result){
                case "1": {
                    System.out.println("From client: Task 1 is running...");
                    toServer.writeObject("1"); //inputStream from client to server- chosen task
                    toServer.writeObject(source1); //inputStream from client to server- matrix
                    //server transfers data to client.
                    HashSet<HashSet<Index>> listOfSCCs = new HashSet<>((List<HashSet<Index>>) fromServer.readObject());
                    System.out.println("From server: Strongly connected components are: " + listOfSCCs);
                    System.out.println("From client: Task 1 finished\n");
                    scanner.nextLine();
                    break;
                }

                case "2.1": {
                    System.out.println("From client: Task 2.1 is running...");
                    toServer.writeObject("2.1");
                    toServer.writeObject(source2);
                    Matrix matrix = new Matrix(source2);
                    System.out.println("Source node:");
                    Index startIndex = indexRequest(matrix); //input
                    toServer.writeObject(startIndex);
                    System.out.println("Destination node:");
                    Index endIndex= indexRequest(matrix); //input
                    toServer.writeObject(endIndex);
                    List<List<Index>> minPaths = new ArrayList<>((List<List<Index>>) fromServer.readObject());
                    System.out.println("From server: Shortest paths from source " + startIndex + " to destination " + endIndex + " are:\n" + minPaths);
                    System.out.println("From client: Task 2.1 finished\n");
                    scanner.nextLine();
                    break;
                }

                case "2.2": {
                    System.out.println("From client: Task 2.2 is running...");
                    toServer.writeObject("2.2");
                    toServer.writeObject(source2);
                    Matrix matrix = new Matrix(source2);
                    System.out.println("Source node:");
                    Index startIndex = indexRequest(matrix); //input
                    toServer.writeObject(startIndex);
                    System.out.println("Destination node:");
                    Index endIndex= indexRequest(matrix); //input
                    toServer.writeObject(endIndex);
                    List<List<Index>> minPaths = new ArrayList<>((List<List<Index>>) fromServer.readObject());
                    System.out.println("From server: Shortest paths from source " + startIndex + " to destination " + endIndex + " are:\n" + minPaths);
                    System.out.println("From client: Task 2.2 finished\n");
                    scanner.nextLine();
                    break;
                }

                case "3": {
                    System.out.println("From client: Task 3 is running...");
                    toServer.writeObject("3");
                    toServer.writeObject(source3);//the matrix that we send
                    int sizeS = (int) fromServer.readObject();
                    System.out.println("From Server: Number of battleships is: " + sizeS);
                    System.out.println("From client: Task 3 finished\n");
                    scanner.nextLine();
                    break;
                }

                case "4": {
                    System.out.println("From client: Task 4 is running...");
                    toServer.writeObject("4");
                    toServer.writeObject(source4); //inputStream from client to server- matrix
                    Matrix matrix= new Matrix(source4);
                    System.out.println("Source node:");
                    Index startIndex = indexRequest(matrix); //input
                    toServer.writeObject(startIndex);
                    System.out.println("Destination node:");
                    Index endIndex= indexRequest(matrix); //input
                    toServer.writeObject(endIndex);
                    LinkedList<List<Index>> minWeightList = new LinkedList<>((LinkedList<List<Index>>) fromServer.readObject());
                    System.out.println("From server: Lightest paths from source " + startIndex + " to destination " + endIndex + " are:\n" + minWeightList);
                    System.out.println("From client: Task 4 finished\n");
                    scanner.nextLine();
                    break;
                }
                case "stop": {
                    flag = true;
                    toServer.writeObject("stop");
                    fromServer.close();
                    toServer.close();
                    socket.close();
                    System.out.println("Client: Closed operational socket");
                    break;
                }

            }
        }
    }
}

