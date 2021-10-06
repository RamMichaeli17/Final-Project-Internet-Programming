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
    private ThreadPoolExecutor threadPool; // handle each client in a separate thread
    private IHandler requestHandler; // what is the type of clients' tasks

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public TcpServer(int port){
        this.port = port;
        // initialize data members (although they are initialized by default), for readability
        stopServer = false;
        this.threadPool = null;
        requestHandler = null;

    }

    public void supportClients(IHandler handler) {
        this.requestHandler = handler;
       /*   2 Kinds of sockets
            Server Socket - a server sockets listens and wait for incoming connections
            1. server socket binds to specific port number
            2. server socket listens to incoming connections
            3. server socket accepts incoming connections if possible

            Operational socket (client socket)
            Server Socket API:
             1. create socket
             2. bind to a specific port number
             3. listen for incoming connections (a client initiates a tcp connection with server)
             4. try to accept (if 3-way handshake is successful)
             5. return operational socket (2 way pipeline)
        */

        //accept() warp by another thread
        new Thread(() ->{

           this.threadPool = new ThreadPoolExecutor(3,5,
                    10, TimeUnit.SECONDS, new LinkedBlockingQueue());

            try {
                ServerSocket serverSocket = new ServerSocket(this.port); // bind
                /*
                listen to incoming connection and accept if possible
                be advised: accept is a blocking call
                TODO: wrap in another thread
                */
                while(!stopServer){

                    Socket serverClientConnection = serverSocket.accept();   //accept()
                    // define a task and submit to our threadPool

                    /*server will handle each client in a separate thread
                       define every client as a Runnable task to execute*/

                    Runnable clientHandling = ()->{
                        System.out.println("Server: Handling a client");
                        try {
                            //right now we have a problem with the line below
                            requestHandler.handle(serverClientConnection.getInputStream(),
                                    serverClientConnection.getOutputStream());
                            // terminate connection with client
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

    /* public void stop(){
        if(!stopServer){
            stopServer = true;
            if(threadPool!=null)
            if(threadPool!=null)
                threadPool.shutdown();
        }

    }*/
    public void stop(){
        if(!stopServer){
            try{
                //double-checked locking
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
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping the server");
        webServer.stop();
    }



}

