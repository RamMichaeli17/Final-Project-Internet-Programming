import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TcpServer {

    private final int port; // initialize in constructor
    private volatile boolean stopServer; // volatile - stopServer variable is saved in RAM memory
    private ThreadPoolExecutor threadPool; // Handle each client in a separate thread
    private IHandler requestHandler; // Handles a family of tasks

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public TcpServer(int port){
        this.port = port;
        // initialize data members (although they are initialized by default) for readability
        this.threadPool = null;
        stopServer = false;
        requestHandler = null;
    }

    public void supportClients(IHandler handler) {
        this.requestHandler = handler;
       /*   There are 2 Kinds of sockets:

           (1) Server Socket - a server sockets listens and wait for incoming connections
            1. server socket binds to specific port number
            2. server socket listens to incoming connections
            3. server socket accepts incoming connections if possible

            Server Socket API:
             1. create socket
             2. bind to a specific port number
             3. listen for incoming connections (a client initiates a tcp connection with server)
             4. try to accept (if 3-way handshake is successful)
             5. return operational socket (2 way pipeline)

           (2) Operational socket (client socket)
       */

        //warp clientHandling Runnable by another thread -
        new Thread(() ->{

           this.threadPool = new ThreadPoolExecutor(3,5,
                    10, TimeUnit.SECONDS, new LinkedBlockingQueue());

            try {
                ServerSocket serverSocket = new ServerSocket(this.port); // bind
                /*
                listen to incoming connection and accept if possible
                be advised: accept is a blocking call
                why?
                When you call accept(), and there isn't already a client connecting to you,it will return 'Operation Would Block'
                to tell you that it can't complete accept() without waiting...
                TODO: wrap in another thread
                */
                while(!stopServer){

                    Socket serverClientConnection = serverSocket.accept();

                    // define a task and submit to our threadPool

                    /*server will handle each client in a separate thread
                       define every client as a Runnable task to execute*/

                    Runnable clientHandling = ()->{
                        System.out.println("Server: Handling a client");
                        try {
                            requestHandler.handle(serverClientConnection.getInputStream(),
                                    serverClientConnection.getOutputStream());
                            // finished handling client, now terminate connection with client
                            // close all streams
                            serverClientConnection.getInputStream().close();
                            serverClientConnection.getOutputStream().close();
                            serverClientConnection.close(); //
                        } catch (IOException | ClassNotFoundException ioException) {
                            ioException.printStackTrace();
                        }
                    };
                    threadPool.execute(clientHandling);
                }
                serverSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        }).start();
    }

    /**
     * stop method responsible stop the server action/ operation
     * implementation using double-check locking
     */
    public void stop(){
        //The finally-block always executes when the try block exits.
        //This ensures that the finally-block is executed even if an unexpected exception occurs.
        // But finally is useful for more than just exception handling — it allows the programmer to avoid having cleanup code
        // accidentally bypassed by a return, continue, or break. Putting cleanup code in a finally-block is always a good practice,
        // even when no exceptions are anticipated.
        if(!stopServer){
            try{
                readWriteLock.writeLock().lock();
                if(!stopServer){
                    if(threadPool!=null)
                        threadPool.shutdown();
                }
            }catch (SecurityException se){
                se.printStackTrace();
            }finally {
                stopServer = true;
                readWriteLock.writeLock().unlock();
                System.out.println("Server shut down successfully");
            }
        }
    }

    public static void main(String[] args) {
        TcpServer webServer = new TcpServer(8010);
        webServer.supportClients(new MatrixIHandler());
        //In order for threads to run and the server not close directly
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping the server");
        webServer.stop();
    }

}

