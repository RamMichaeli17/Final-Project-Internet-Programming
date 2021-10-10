import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class send messages and tasks to the server by client.
 * There is 4 task the client can choose - each task is on 2D matrix
 * all tasks are warp by switch-case
 */

    public class Client {

        //think if we need this for task 4 or another task (?)
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
        System.out.println("Please choose one task: (all the tasks works on matrix)");
        System.out.println("1--> Find all SCCs [first task]");
        System.out.println("2--> Find all shortest paths from one index to another [second task]");
        System.out.println("3--> Find count of submarines [third task]");
        System.out.println("4--> Find the lightest paths from one index to another [fourth task]");
        System.out.println("stop--> Exit the program");
    }

        public static void main(String[] args) throws IOException, ClassNotFoundException ,ClassCastException{

            Scanner scanner = new Scanner(System.in); //for the client inputs
            Socket socket =new Socket("127.0.0.1",8010);
            System.out.println("client: Created Socket");

            //warp InputStream & OutputStream in order to send/receive meaningful data
            ObjectOutputStream toServer=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream fromServer=new ObjectInputStream(socket.getInputStream());

            // sending #1 matrix
            int[][] source = {
                    {1, 0, 0},
                    {0, 0, 1},
                    {0, 0, 1},
            };
            // output - [[(0,0), (2,2), (1,2), (0,1)], [(2,0)]]

            int[][] source4 ={
                    {100,100,100},
                    {300,900,500},
                    {100,100,100},
            };
            // output from (1,0) to (1,2) will be:
            // [[(1,0),(0,1),(1,2)],[(1,0),(2,1),(1,2)]] , with weight 900 - it also include diagonals.

            boolean flag = false;
            while(!flag){ // while !stop
                printOptionToClient(); //print menu for client
                String result= scanner.next(); //next() input for string
                switch(result){
                    case "1": {
                        System.out.println("From client\nTask 1 is running...");
                        toServer.writeObject("1"); //inputStream from client to server- chosen task
                        toServer.writeObject(source); //inputStream from client to server- matrix
                        //listOfSCCs - holds hashSet of hashSet (each hashSet is connected component)
                        //server transfers data to client.
                        HashSet<HashSet<Index>> listOfSCCs =
                                new HashSet<>((List<HashSet<Index>>) fromServer.readObject());

                         /* A hashSet is unsorted Collection. In java 8 we can do like this:
                           fooHashSet.stream()
                          .sorted(Comparator.comparing(Foo::getSize)) //comparator - how you want to sort it
                          .collect(Collectors.toList()); //collector - what you want to collect it to
                        */

                        //sort listOfSCCs
                       List<HashSet<Index>> list = listOfSCCs.stream().sorted(Comparator.comparingInt(HashSet::size))
                                .collect(Collectors.toList());
                        System.out.println("from server: Connected Components are- " + list);
                        System.out.println("from client: task 1 is finished\n");
                        scanner.nextLine();
                        break;
                    }

                   case "2": {
                        System.out.println("From client\nTask 2 is running...");
                        toServer.writeObject("2");
                        //TODO: add the rest of code for this case
                        break;
                    }

                    case "3": {
                        System.out.println("From client\nTask 3 is running...");
                        toServer.writeObject("3");
                        toServer.writeObject(source);//the matrix that we send
                        int sizeS = (int) fromServer.readObject();
                        System.out.println("from Server - Number of submarines is:  " + sizeS);
                        System.out.println("from client: Task 3 finish");
                        //TODO: add the rest of code for this case
                        break;
                    }

                    case "4": {
                        System.out.println("From client\n Task 4 is running...");
                        toServer.writeObject("4");
                        toServer.writeObject(source4); //inputStream from client to server- matrix
                        Matrix matrix= new Matrix(source4);
                        Index startIndex = indexRequest(matrix); //input
                        toServer.writeObject(startIndex);
                        Index endIndex= indexRequest(matrix); //input
                        toServer.writeObject(endIndex);

                        LinkedList<List<Index>> minWeightList = new LinkedList<>((LinkedList<List<Index>>) fromServer.readObject());
                        System.out.println("from Server - The easiest routes are: " + minWeightList);
                        toServer.writeObject(minWeightList);
                        System.out.println("from client: Task 4 finish");
                        scanner.nextLine();
                        break;
                    }
                    case "stop": {
                        flag = true;
                        toServer.writeObject("stop");
                        fromServer.close();
                        toServer.close();
                        socket.close();
                        System.out.println("client: Closed operational socket");
                        break;
                    }

                }
            }
        }
}

