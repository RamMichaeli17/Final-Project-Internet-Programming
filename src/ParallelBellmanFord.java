import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implement kind of Bellman-Ford algorithm.
 * We need to find all the lightest paths between 2 nodes(from source to destination)
 * How?
 * (1)find all paths from source to destination - with findPaths method
 * (2)find the weight of each path - with SumPathWeight method
 * (3)find all min paths by loop all over the paths and check the min weight - in findLightestPathsParallelBellmanFord method - parallel
 *
 */
public class ParallelBellmanFord<T> {

    final ThreadLocal<Queue<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<>());

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * findPaths: The function finds paths from src to dest by ThreadLocal
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return LinkedList<List < Node < T>>> - all paths between source to destination
     */

    public LinkedList<List<Node<T>>> findPaths(Traversable<T> someGraph, Node<T> src, Node<T> dest) {

        List<Node<T>> path = new ArrayList<>(); //this list will hold a singlePath
        LinkedList<List<Node<T>>> listPaths = new LinkedList<>(); //this list will hold all paths between source to destination
        path.add(src);  //each path will start with the source node
        threadLocalQueue.get().offer(path); //add path to threadLocal (at first it holds the source node)
        while (!threadLocalQueue.get().isEmpty()) {
            path = threadLocalQueue.get().poll(); //take the first path in the queue
            Node<T> last = path.get(path.size() - 1); //get the last node in the path
            // If last vertex is the desired destination
            // then add the path to lists of paths (because we want to reach the destination...)
            if (last.equals(dest)) {
                listPaths.add(path);
            }
            Collection<Node<T>> neighborsIndices = someGraph.getNeighbors(last); //get all neighbors of the last node
            for (Node<T> neighbor : neighborsIndices) {
                if (!path.contains(neighbor)) { // if the current path doesn't contain the neighbor
                    List<Node<T>> newPath = new ArrayList<>(path); // here we create a new path on the basis of the old path (current path)
                    newPath.add(neighbor);
                    threadLocalQueue.get().offer(newPath); //add path to threadLocal
                }

            }
        }
        threadLocalQueue.get().clear();
        return listPaths; //all the paths between source to destination
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

    /**
     * findLightestPathsParallelBellmanFord: the function calls to findPaths method and
     * finds the lightest weight of paths in a parallel way
     * each search wrap in callable
     *
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dest represent final/ destination index
     * @return LinkedList<List < Node < T>>> - all the lightest paths between source node to destination
     */

    public LinkedList<List<Node<T>>> findLightestPathsParallelBellmanFord(Traversable<T> someGraph, Node<T> src, Node<T> dest) {

        /**
         * The primary use of AtomicInteger is when we are in multi-threaded context, and we need to perform atomic operations on an int value without using synchronized keyword.
         * Using the AtomicInteger is equally faster and more readable than performing the same using synchronization.
         * We are using AtomicInteger as an atomic counter which is being used by multiple threads concurrently.
         */
        AtomicInteger weightOfPath = new AtomicInteger(); //will hold weight of specific path
        AtomicInteger currMinWeight = new AtomicInteger(); //will hold current minimum weight of path
        AtomicInteger totalMinWeight = new AtomicInteger(); //will hold the minimum of all the paths

        currMinWeight.set(Integer.MAX_VALUE); //Integer.MAX_VALUE=2147483647 - set the max value for the weight

        LinkedList<List<Node<T>>> listPaths = findPaths(someGraph, src, dest); //will hold all paths between source to destination
        LinkedList<List<Node<T>>> listMinTotalWeight = new LinkedList<>(); //will hold all lightest paths between source to destination

        LinkedList<Future<List<Node<T>>>> futureList = new LinkedList<>(); //Future list ,submit value later
        LinkedList<List<Node<T>>> listMinTotalWeightFuture = new LinkedList<>(); //will hold all lightest future paths between source to destination

        for (List<Node<T>> list : listPaths) { //pass all over the lists to find the min weight
            //callable returns a value
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                weightOfPath.set(sumPathWeight(someGraph, list)); //check weight of current path
                if (weightOfPath.get() <= currMinWeight.get()) {
                    //that's mean we need to update values of currMinWeight & totalMinSUm because we found a lighter path
                    //here we are using double-check locking to make sure the lock operates as expected.
                    //usually, we are doing double-check locking in a multi-threaded system.
                    currMinWeight.set(weightOfPath.get());
                    totalMinWeight.set(currMinWeight.get());
                    readWriteLock.writeLock().unlock();
                    if (totalMinWeight.get() < currMinWeight.get())
                        totalMinWeight.set(currMinWeight.get());
                    return list;
                } else { // the path is irrelevant to us
                    weightOfPath.set(0);
                    readWriteLock.writeLock().unlock();
                    return null;
                }

            };
            Future<List<Node<T>>> futurePath = threadPoolExecutor.submit(callable); //submit value in Future thread
            futureList.add(futurePath); //add the future path to future list
        }

        for (Future<List<Node<T>>> futureP : futureList) {

            try {
                if (futureP.get() != null)
                    listMinTotalWeightFuture.add(futureP.get()); //add future path to future list just if the path is not null
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }
        int currentWeight = 0; //reset currentWeight variable for next calculate on future list
        for (List<Node<T>> currentList : listMinTotalWeightFuture) {
            for (Node<T> nodeT : currentList) {
                currentWeight = currentWeight + someGraph.getValueN(nodeT.getData());
            }
            //we have already found the smallest weight, so we only need to check if the weight equals to the smallest one.
            if (currentWeight == totalMinWeight.get()) {
                listMinTotalWeight.add(currentList);
            }
            currentWeight = 0; //reset currentWeight variable for next iteration in the loop
        }
        this.threadPoolExecutor.shutdown();
        return listMinTotalWeight;
    }
}


