import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class implementation kind of Bellman-Ford algorithm.
 * We need to find all the min paths between 2 nodes( from source to destination)
 * How?
 * (1)find all paths from source to destination - with findPaths method
 * (2)find the sum of each path - with SumPathWeight method
 * (3)find all min paths by loop all over the paths and checked the min sum - in findPathsBellmanFord method - parallel
 *
 */
public class ThreadLocalBellmanFord <T> implements Serializable {
    //Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.

    protected final ThreadLocal<Queue<List<Node<T>>>> queueThreadLocal =
            ThreadLocal.withInitial(() -> new LinkedList<>());

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    /**
     * findPaths: The function finds paths that start and end like the indexes input(src,dst)
     * by ThreadLocal
     *
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dst represent final/ destination index
     * @return LinkedList<List < Node < T>>> - all paths between source to destination
     *
     * maybe we should return List<List<Node<T>>> (?)
     */

    public LinkedList<List<Node<T>>> findPaths(Traversable<T> someGraph, Node<T> src, Node<T> dst) {

        List<Node<T>> path = new ArrayList<>(); //this list will hold a singlePath
        LinkedList<List<Node<T>>> listPaths = new LinkedList<>(); //this list will hold all paths between source to destination
        path.add(src);  //each path will start with the same Node
        queueThreadLocal.get().offer(path); //add path to threadLocal (at first it holds the source node)
        while (!queueThreadLocal.get().isEmpty()) {
            //while we do not pass all over the neighbors

            path = queueThreadLocal.get().poll(); //take the first node in the queue
            Node<T> last = path.get(path.size() - 1); //get the size of path
            // If last vertex is the desired destination
            // then add the path to lists of paths (because we want to reach the destination...)
            if (last.equals(dst)) {
                listPaths.add(path);
            }
            Collection<Node<T>> neighborsIndices = someGraph.getNeighbors(last); //get all neighbors of last index/node
            for (Node<T> neighbor : neighborsIndices) {
                if (isNotVisited(neighbor, path)) {// if there is neighbors that we do not visit him(not contains in path) we keeping it in a new path
                    //why? because we're looking for all paths ...
                    List<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(neighbor);
                    queueThreadLocal.get().offer(newpath); //add path to threadLocal
                }

            }
        }
        return listPaths; //all the paths between source to destination
    }

    /**
     * isNotVisited: The function check if a specific node is exist in path
     *
     * @param n represents node to check if exist in the path
     * @param path type: List<Node<T>>
     * @return boolean
     */
    public boolean isNotVisited(Node<T> n, List<Node<T>> path) {
     //decide which one is better
        for (Node<T> node : path)
            if (path.contains(n))
                return false;

        return true;

        //geeks4geeks
        /* int size = path.size();
              for(int i = 0; i < size; i++)
                 if (path.get(i) == n)
                    return false;

              return true;*/
    }

    /**
     * SumPathWeight: The function calculate sum of a specific path by its nodes
     *
     * @param someGraph represents a graph
     * @param list represents a specific path
     * @return sum of the path
     */
    public int SumPathWeight(Traversable<T> someGraph, List<Node<T>> list) {
        int sum = 0;
        for (Node<T> node : list) {
            //we pass on the nodes in the specific path and summarize the wight
            sum = sum + someGraph.getValueN(node.getData());
        }
        return sum;
    }


    /**
     * findPathsBellmanFord: the function calls to findPaths method and
     * finds the lightest weight of paths in a parallel way
     * each search wrap in callable
     *
     * @param someGraph represent a graph
     * @param src represent start index
     * @param dst represent final/ destination index
     * @return LinkedList<List < Node < T>>> - all the lightest weight paths between source node to destination
     */

    public LinkedList<List<Node<T>>> findLightestPathsBellmanFord(Traversable<T> someGraph, Node<T> src, Node<T> dst) {

        //initial to 0
        //AtomicInteger sum= new AtomicInteger(0) == AtomicInteger sum= new AtomicInteger()

        AtomicInteger sumPath = new AtomicInteger(); //will hold sum of specific path
        AtomicInteger currMinSum = new AtomicInteger(); //will hold current minimum sum of path
        AtomicInteger totalMinSum = new AtomicInteger(); //will hold the minimum of all the paths

        currMinSum.set(Integer.MAX_VALUE); //Integer.MAX_VALUE=2147483647

        LinkedList<List<Node<T>>> listPaths = findPaths(someGraph, src, dst); //will hold all paths between source to destination
        LinkedList<List<Node<T>>> listMinTotalWeight = new LinkedList<>(); //will hold all lightest paths between source to destination

        LinkedList<Future<List<Node<T>>>> futureList = new LinkedList<>(); //Future list , submit value later
        LinkedList<List<Node<T>>> listMinTotalWeightfurure = new LinkedList<>(); //will hold all lightest Future paths between source to destination

        for (List<Node<T>> list : listPaths) { //pass all over lists to find the min sum
            //callable returns value
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sumPath.set(SumPathWeight(someGraph, list)); //check sum of current path

                if (sumPath.get() <= currMinSum.get()) {
                    //thats mean we need to update values of currMinSum & totalMinSUm
                    currMinSum.set(sumPath.get());
                    totalMinSum.set(currMinSum.get());
                    readWriteLock.writeLock().unlock();
                    if (totalMinSum.get() < currMinSum.get())
                        totalMinSum.set(currMinSum.get());
                    return list;
                } else {
                    sumPath.set(0);
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
                    listMinTotalWeightfurure.add(futureP.get()); //add future path to future list just if the path is not null
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }
        int sum = 0; //reset sum variable for next calculate on future list
        for (List<Node<T>> listp : listMinTotalWeightfurure) {
            for (Node<T> nodeT : listp) {
                sum = sum + someGraph.getValueN(nodeT.getData());
            }
            //we have already found the min sum, so we need just to check if the sums equals
            if (sum == totalMinSum.get()) {
                listMinTotalWeight.add(listp);
            }
            sum = 0; //reset sum variable for next calculate in for loop

        }
        this.threadPoolExecutor.shutdown();
        return listMinTotalWeight;
    }

}


