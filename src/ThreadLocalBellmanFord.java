import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implement kind of Bellman-Ford algorithm.
 * We need to find all the lightest paths between 2 nodes(from source to destination)
 * How?
 * (1)find the sum of each path - with SumPathWeight method
 * (2)find all lightest paths by loop all over the paths and check the min weight - in findPathsBellmanFord method
 */
public class ThreadLocalBellmanFord<T>  {

    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<List<Node<T>>>());
    /**
     * findLightestPathsBellmanFord: The function finds all the lightest paths from source to destination
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return List<List<Node<T>>> - all lightest paths between source to destination
     */

    public List<List<Node<T>>> findLightestPathsBellmanFord(Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        int weightOfLightestPath=Integer.MAX_VALUE;
        List<List<Node<T>>> currentLightestPaths = new ArrayList<>();
        ArrayList<Node<T>> path = new ArrayList<>();
        path.add(src);
        threadLocalQueue.get().add(path);
        while(!threadLocalQueue.get().isEmpty()) {
            path = (ArrayList<Node<T>>) threadLocalQueue.get().poll();
            Node<T> polled = path.get(path.size()-1);
            if(polled.equals(dest))
                if(weightOfLightestPath<sumPathWeight(someGraph,path)) {
                    continue;}
                else {
                    weightOfLightestPath = sumPathWeight(someGraph,path);
                    currentLightestPaths.add(path);
                }
            Collection<Node<T>> neighbors = someGraph.getNeighbors(polled);
            for (Node<T> neighbor : neighbors) {
                if(!path.contains(neighbor)){
                    ArrayList<Node<T>> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    threadLocalQueue.get().add(newPath);
                }
            }
        }
        List<List<Node<T>>> lightestPaths = new ArrayList<>();
        for (List<Node<T>> currentPath : currentLightestPaths)
        {
            if (sumPathWeight(someGraph,currentPath)==weightOfLightestPath)
                lightestPaths.add(currentPath);
        }
        threadLocalQueue.get().clear();
        return lightestPaths;
    }

    /**
     * sumPathWeight: This function calculate a weight of a specific path by its nodes.
     *
     * @param someGraph represents a graph
     * @param path represents a specific path
     * @return weight of the path
     */
    public int sumPathWeight(Traversable<T> someGraph, List<Node<T>> path) {
        int weight = 0;
        for (Node<T> node : path) {
            //we pass on the nodes in the specific path and summarize the wight
            weight = weight + someGraph.getValueN(node.getData());
        }
        return weight;
    }
}


