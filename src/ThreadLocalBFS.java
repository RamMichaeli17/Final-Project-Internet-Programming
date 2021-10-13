import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * This class implement kind of BFS algorithm.
 * We need to find all the shortest paths between 2 nodes(from source to destination)
 * How?
 * find all shortest paths by loop all over the paths and check size of the path- in findShortestPathsBFS method
 */
public class ThreadLocalBFS<T> {
    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<List<Node<T>>>());

    /**
     * findShortestPathsBFS: The function finds all the shortest paths from source to destination
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return List<List < Node < T>>> - all shortest paths between source to destination
     */
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
        threadLocalQueue.get().clear();
        return minPaths;

    }
}
