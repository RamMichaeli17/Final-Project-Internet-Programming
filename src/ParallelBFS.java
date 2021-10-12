import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ParallelBFS<T> {
    final ThreadLocal<LinkedList<List<Node<T>>>> threadLocalQueue = ThreadLocal.withInitial(() -> new LinkedList<List<Node<T>>>());

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();


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
            return allPaths;
    }

    public List<List<Node<T>>> findShortestPathsParallelBFS(Traversable<T> someGraph, Node<T> src, Node<T> dest) {
        //למה אטומיק
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




//    public static void main(String[] args) {
//        int[][] myArray = {
//                {1,0,0},
//                {1,1,0},
//                {1,1,0}
//        };
//
//        TraversableMatrix myMatrixGraph = new TraversableMatrix(new Matrix(myArray));
//        System.out.println(myMatrixGraph);
//        myMatrixGraph.setStartIndex(new Index(0,0));
//        ThreadLocalBFS_2<Index> threadLocalBFS = new ThreadLocalBFS_2<>();
//        Node<Index> src = new Node (new Index(0,0));
//        Node<Index> dest = new Node (new Index(2,2));
//        System.out.println(threadLocalBFS.parallelBFS(myMatrixGraph,src,dest));
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
