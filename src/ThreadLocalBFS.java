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
}
