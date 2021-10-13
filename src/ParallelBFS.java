import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implement kind of BFS algorithm.
 * We need to find all the shortest paths between 2 nodes(from source to destination)
 * How?
 * (1)find all paths from source to destination - with findPaths method
 * (2)find all shortest paths by loop all over the paths and check size of the path- in findShortestPathsParallelBFS method - parallel
 */
public class ParallelBFS<T> {
    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<List<Node<T>>>());

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * findAllPaths: The function finds paths from src to dest by ThreadLocal
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return List<List < Node < T>>> - all paths between source to destination
     */
    public List<List<Node<T>>> findAllPaths (Traversable<T> someGraph,Node<T> src, Node<T> dest)
    {
        ArrayList<Node<T>> path = new ArrayList<>();
        List<List<Node<T>>> allPaths = new ArrayList<>();
        path.add(src);
        threadLocalQueue.get().add(path);
        while(!threadLocalQueue.get().isEmpty()) {
            path = (ArrayList<Node<T>>) threadLocalQueue.get().poll();
            Node<T> polled = path.get(path.size()-1);
            if(polled.equals(dest))
                allPaths.add(path);
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(polled);
            for (Node<T> singleReachableNode : reachableNodes) {
                if (!path.contains(singleReachableNode)) {
                    ArrayList<Node<T>> newPath = new ArrayList<>(path);
                    newPath.add(singleReachableNode);
                    threadLocalQueue.get().add(newPath);
                }
            }
        }
        threadLocalQueue.get().clear();
            return allPaths;
    }
    /**
     * findShortestPathsParallelBFS: the function calls to findPaths method and
     * finds the shortest paths in a parallel way
     * each search wrap in callable
     *
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return List<List < Node < T>>> - all the shortest paths between source node to destination
     */
    public List<List<Node<T>>> findShortestPathsParallelBFS(Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        /**
         * The primary use of AtomicInteger is when we are in multi-threaded context, and we need to perform atomic operations on an int value without using synchronized keyword.
         * Using the AtomicInteger is equally faster and more readable than performing the same using synchronization.
         * We are using AtomicInteger as an atomic counter which is being used by multiple threads concurrently.
         */
        AtomicInteger sizeOfMinPath = new AtomicInteger();
        AtomicInteger sizeOfPath = new AtomicInteger();
        sizeOfMinPath.set(Integer.MAX_VALUE);
        List<Future<List<Node<T>>>> futureList = new ArrayList<>();
        List<List<Node<T>>> allPaths = findAllPaths(someGraph,src,dest);
        List<List<Node<T>>> minPaths = new ArrayList<>();
        for (List<Node<T>> list: allPaths)
        {
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sizeOfPath.set(list.size());
                if(sizeOfPath.get()<=sizeOfMinPath.get()) {
                    sizeOfMinPath.set(sizeOfPath.get());
                    readWriteLock.writeLock().unlock();
                    return list;
                }
                else {
                    readWriteLock.writeLock().unlock();
                    return null;
                }
            };
            Future<List<Node<T>>> futurePath =threadPoolExecutor.submit(callable);
            futureList.add(futurePath);
        }
        for (Future<List<Node<T>>> futurePath:futureList) {
            try {
                if (futurePath.get()!=null)
                    minPaths.add(futurePath.get());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.threadPoolExecutor.shutdown();
        if (minPaths.isEmpty())
            System.out.println("No path exist between the source "+src+" and the destination "+dest);
        return minPaths;
    }
}
