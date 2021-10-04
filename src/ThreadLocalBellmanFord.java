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
        List<Node<T>> path = new ArrayList<>();
        LinkedList<List<Node<T>>> listPaths = new LinkedList<>();
        path.add(src);
        queueThreadLocal.get().offer(path);
        while (!queueThreadLocal.get().isEmpty()) {

            path = queueThreadLocal.get().poll();
            Node<T> last = path.get(path.size() - 1);
            // If last vertex is the desired destination
            // then print the path
            if (last.equals(dst)) {
                listPaths.add(path);
            }
            Collection<Node<T>> neighborsIndices = someGraph.getReachableNodesWithoutDiagonal(last);
            for (Node<T> neighbor : neighborsIndices) {
                if (isNotVisited(neighbor, path)) {
                    List<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(neighbor);
                    queueThreadLocal.get().offer(newpath);
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
      /*  for (Node<T> v : path)
            if (path.contains(n))
                return false;

        return true;*/

        //geeks4geeks
          int size = path.size();
              for(int i = 0; i < size; i++)
                 if (path.get(i) == n)
                    return false;

              return true;
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
    public LinkedList<List<Node<T>>> findPathsBellmanFord(Traversable<T> someGraph, Node<T> src, Node<T> dst) {
        //initial to 0
        //AtomicInteger sum= new AtomicInteger(0) == AtomicInteger sum= new AtomicInteger()

        AtomicInteger sumPath = new AtomicInteger();
        AtomicInteger currMinSum = new AtomicInteger();
        AtomicInteger totalMinSum = new AtomicInteger();
        currMinSum.set(Integer.MAX_VALUE);
        LinkedList<Future<List<Node<T>>>> futureList = new LinkedList<>();
        LinkedList<List<Node<T>>> listPaths = findPaths(someGraph, src, dst);
        LinkedList<List<Node<T>>> listMinTotalWeight = new LinkedList<>();
        LinkedList<List<Node<T>>> listMinTotalWeightfurure = new LinkedList<>();
        for (List<Node<T>> list : listPaths) {
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sumPath.set(SumPathWeight(someGraph, list));

                if (sumPath.get() <= currMinSum.get()) {
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
            Future<List<Node<T>>> futurePath = threadPoolExecutor.submit(callable);
            futureList.add(futurePath);
        }

        for (Future<List<Node<T>>> futureP : futureList) {

            try {
                if (futureP.get() != null)
                    listMinTotalWeightfurure.add(futureP.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }

        int sum = 0;
        for (List<Node<T>> listp : listMinTotalWeightfurure) {
            for (Node<T> nodeT : listp) {
                sum = sum + someGraph.getValueN(nodeT.getData());
            }
            if (sum == totalMinSum.get()) {
                listMinTotalWeight.add(listp);
            }
            sum = 0;

        }
        this.threadPoolExecutor.shutdown();
        return listMinTotalWeight;
    }



     public static void main(String[] args) {

        int[][] myArray = {
                {100, 100, 100},
                {500, 900, 300}
        };
        Traversable someGraph= new TraversableMatrix(new Matrix(myArray));

        ThreadLocalBellmanFord <Index> bellmanFord = new ThreadLocalBellmanFord<>();
      //  Index start=new Index(0,1);
      //  Index end = new Index(1,2);
        Node<Index> src= new Node(new Index(0,0));
        Node<Index> dest =new Node(new Index(1,2));

        LinkedList<List<Node<Index>>> minPathsBellmanFord= bellmanFord.findPathsBellmanFord(someGraph,src,dest);
        System.out.println(minPathsBellmanFord);

     }


}


