import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadLocalBFS<T> {
    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<List<Node<T>>>());

    public List<List<Node<T>>> findShortestPathsBFS(Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        int sizeOfMinPath=Integer.MAX_VALUE;
        List<List<Node<T>>> minPaths = new ArrayList<>();
        ArrayList<Node<T>> path = new ArrayList<>();
        path.add(src);
        threadLocalQueue.get().add(path);
        while(!threadLocalQueue.get().isEmpty()) {
            path = (ArrayList<Node<T>>) threadLocalQueue.get().poll();
            Node<T> polled = path.get(path.size()-1);
            if(polled.equals(dest))
                if(sizeOfMinPath<path.size())
                    break;
                else {
                    sizeOfMinPath = path.size();
                    minPaths.add(path);
                }
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(polled);
            for (Node<T> singleReachableNode : reachableNodes) {
                if(!path.contains(singleReachableNode)){
                    ArrayList<Node<T>> newPath = new ArrayList<>(path);
                    newPath.add(singleReachableNode);
                    threadLocalQueue.get().add(newPath);
                }
            }
        }
        if (minPaths.isEmpty())
            System.out.println("No path exist between the source "+src+" and the destination "+dest);
        return minPaths;

    }

//    public static void main(String[] args) {
//        int[][] myArray = {
//                {1,1,1,1,0},
//                {1,1,1,0,0},
//                {1,1,1,0,0},
//                {0,0,1,0,0}
//        };
//
//        TraversableMatrix myMatrixGraph = new TraversableMatrix(new Matrix(myArray));
//        System.out.println(myMatrixGraph);
//        myMatrixGraph.setStartIndex(new Index(0,0));
//        ThreadLocalBFS<Index> threadLocalBFS = new ThreadLocalBFS<>();
//        Node<Index> src = new Node (new Index(0,0));
//        Node<Index> dest = new Node (new Index(3,2));
//        System.out.println(threadLocalBFS.BFS(myMatrixGraph,src,dest));
//
//    }


    /*
    create a queue Q
    mark v as visited and put v into Q
    while Q is non-empty
    remove the head u of Q
    mark and enqueue all (unvisited) neighbours of u
     */

}
