import java.util.*;

public class ThreadLocalBFS<T> {
    final ThreadLocal<LinkedList<Node<T>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<Node<T>>());
    final ThreadLocal<Set<Node<T>>> threadLocalVisited = ThreadLocal.withInitial(() -> new LinkedHashSet<Node<T>>());

    public List<List<T>> BFS (Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        int counter=Integer.MAX_VALUE;
        List<List<T>> Paths = new ArrayList<>();
        ArrayList<T> path = new ArrayList<>();
        threadLocalVisited.get().add(src);
        threadLocalQueue.get().add(src);
        while(!threadLocalQueue.get().isEmpty()) {
            Node<T> polled = threadLocalQueue.get().poll();
            path.add(polled.getData());
            if(polled.equals(dest))
                if(counter<path.size())
                    break;
                else {
                    counter = path.size();
                    minPaths.add(path);
                }
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(polled);
            for (Node<T> singleReachableNode : reachableNodes) {
                if(!threadLocalVisited.get().contains(singleReachableNode)){
                    threadLocalVisited.get().add(singleReachableNode);
                    threadLocalQueue.get().add(singleReachableNode);
                    ArrayList<T> newPath = new ArrayList<>(path);
                    newPath.add(singleReachableNode.getData());
                }

            }


        }
        return minPaths;

    }


    /*
    create a queue Q
    mark v as visited and put v into Q
    while Q is non-empty
    remove the head u of Q
    mark and enqueue all (unvisited) neighbours of u
     */

}
