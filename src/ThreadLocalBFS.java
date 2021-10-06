import java.util.*;

public class ThreadLocalBFS<T> {
    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<Node<T>>());
    final ThreadLocal<Set<Node<T>>> threadLocalVisited = ThreadLocal.withInitial(() -> new LinkedHashSet<Node<T>>());

    public List<List<T>> BFS (Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        int sizeOfMinPath=Integer.MAX_VALUE;
        List<List<T>> minPaths = new ArrayList<>();
        ArrayList<Node<T>> path = new ArrayList<>();
        threadLocalVisited.get().add(src);
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
                if(!threadLocalVisited.get().contains(singleReachableNode)){
                    threadLocalVisited.get().add(singleReachableNode);
                    threadLocalQueue.get().add(singleReachableNode);
                    ArrayList<T> newPath = new ArrayList<>(path);
                    newPath.add(singleReachableNode.getData());
                    Paths.add(newPath);
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
