import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * This class represents a Thread-safe DFS algorithm.
 *
 *  When we need to make sure that every thread has its own local data structures  - synchronization is not the solution.
 *  using TLS- Thread Local Storage, each thread has his own storage
 */
public class ThreadLocalDFSVisit<T> {

    //lambda expression
    final ThreadLocal<Stack<Node<T>>> threadLocalStack = ThreadLocal.withInitial(() -> new Stack<Node<T>>());

    // method reference
    //    final ThreadLocal<Stack<Node<T>>> threadLocalStack2 = ThreadLocal.withInitial(Stack::new);
    final ThreadLocal<Set<Node<T>>> threadLocalSet = ThreadLocal.withInitial(LinkedHashSet::new);

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(); //using lock in callable

    /**
     * parallelDFSVisitTraverse function finds SCC parallely.
     * why hashSet? HashSet is a collection of items where every item is unique - we doesn't want multiplication
     *
     * submit is a function that return value, due to we're using Callable.
     *
     * Future represents the result of an asynchronous computation. When the asynchronous task is created,a Java Future object is returned.
     * This Future object functions as a handle to the result of the asynchronous task
     *
     * @param SomeGraph represent current Graph (we relate matrix as graph)
     * @param listOfIndex -list of indexes their value is 1 (connected components are indexes with value 1).
     * @return HashSet<HashSet<T>> - list of all SCCs in the current graph
     */
    public HashSet<HashSet<T>> parallelDFSTraverse(Traversable<T> SomeGraph, List<Index> listOfIndex){

        HashSet<Future<HashSet<T>>> futureListOfScc = new HashSet<>();
        HashSet<HashSet<T>> listIndexScc = new HashSet<>();
        int listSize=listOfIndex.size();
        for(int i=0; i<listSize; i++) {
            int finalI=i;
            Callable<HashSet<T>> MyCallable = () -> {
                readWriteLock.writeLock().lock();
                SomeGraph.setStartIndex(listOfIndex.get(finalI));

                System.out.println("This thread: "+ Thread.currentThread().getName());

                //traverse method warp by callable
                HashSet<Index> singleSCC = (HashSet<Index>) this.traverse(SomeGraph);
                readWriteLock.writeLock().unlock();
                return (HashSet<T>) singleSCC;

            };
            Future<HashSet<T>> futureSCC = threadPoolExecutor.submit(MyCallable);
            futureListOfScc.add(futureSCC);
        }
        for (Future<HashSet<T>> futureScc : futureListOfScc) {
                try {
                    //Future.get() is a blocking call-
                    //it will block until results of computation are available, or the computation was interrupted (cancelled or resulting in exception).
                    listIndexScc.add(futureScc.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        this.threadPoolExecutor.shutdown();
        return listIndexScc;
    }

    /**
     * traverse -this function execute DFS method by ThreadLocal
     * @param someGraph represent current Graph (we relating matrix as graph)
     *
     * @return List<T> - list of connected component(path).
     */

    public Set<T> traverse(Traversable<T> someGraph) {
        /*
        push origin to the Stack V
        while stack is not empty: V
            removed = pop operation V
            insert to finished V
            invoke getReachableNodes method on removed node V
            for each reachableNode: V
                if current reachableNode is NOT in stack (just discovered)
                &&  current reachableNode is NOT in finished
                push to stack
         */
        threadLocalStack.get().push(someGraph.getOrigin());
        while (!threadLocalStack.get().isEmpty()) {
            //pop is for stack, poll is for queue
            Node<T> popped = threadLocalStack.get().pop();
            threadLocalSet.get().add(popped);
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(popped);
            for (Node<T> singleReachableNode : reachableNodes) {
                if (!threadLocalSet.get().contains(singleReachableNode) && !threadLocalStack.get().contains(singleReachableNode)) {
                    threadLocalStack.get().push(singleReachableNode);
                }
            }
        }
        Set<T> connectedComponent = new HashSet<>();
        for (Node<T> node : threadLocalSet.get()) connectedComponent.add(node.getData());

        //A scan cycle does not mean that the copy of the data structure has been deleted, so it is necessary to delete the data in the copy
        //Old data is saved in that specific thread's copy of the data stracture- we ought to clear between traversals using the same threads.
        threadLocalStack.get().clear(); //there is no reason to clear the stack other than readability
        threadLocalSet.get().clear();
        return connectedComponent;
        }

    /**
     * findSCCs- this function get a 2D matrix and finds all scc in this matrix
     * @param source -primitiveMatrix
     * @return list of SCCs
     */

    public List<HashSet<Index>> findSCCs(int[][] source)
    {
        HashSet<HashSet<Index>> allSCCs;
        List<Index> listOne;
        //convert primitive matrix to Matrix
        Matrix sourceMatrix = new Matrix(source);
        sourceMatrix.printMatrix();

        //parallelDFSTraverse need to get traversable<T> , list<HashSet<Index>>> :
        TraversableMatrix myTraversableM = new TraversableMatrix(sourceMatrix); //convert Matrix to TraversableMatrix
        listOne = sourceMatrix.findAllOnes(); //each connected component contains only nodes with value==1
        System.out.println(listOne);

        //set the first index - "Initialize start index"
        myTraversableM.setStartIndex(myTraversableM.getStartIndex());
        ThreadLocalDFSVisit<Index> algo = new ThreadLocalDFSVisit<>();

        //call to parallelDFSTraverse method
        allSCCs = algo.parallelDFSTraverse(myTraversableM, listOne);
        List<HashSet<Index>> list = allSCCs.stream().sorted(Comparator.comparingInt(HashSet::size))
                .collect(Collectors.toList());

        return list;

    }
    /**
     * submarine: the function count number of valid submarines:
     *  * 1. Minimum of two "1" vertically.
     *  * 2. Minimum of two "1" horizontally.
     *  * 3. There cannot be "1" diagonally unless arguments 1 and 2 are implied.
     *  * 4. The minimal distance between two submarines is at least one index("0").
     *
     * @param hashSetList type: HashSet<HashSet<Index>> list of SCC
     * @param tempArray type: int[][] the matrix that we send in the beginning
     * @return int
     */
    public int subCheck(List<HashSet<Index>> hashSetList, int[][] tempArray) {
        int countSub = hashSetList.size();// size of the optional submarine
        int minRow = Integer.MAX_VALUE, minCol = Integer.MAX_VALUE, maxRow = Integer.MIN_VALUE, maxCol = Integer.MIN_VALUE;
        int flag = 0;// that flag will be 1 if some scc isn't a submarine and after that countSub--
        for (HashSet<Index> s : hashSetList) {// run on each SCC
            for (Index index : s) {
                if (s.size() == 1)// SCC==1 not a sub
                    flag = 1;

                if (flag == 1)
                    countSub--;

                flag = 0;

                if (index.row <= minRow) //Shape boundaries of the submarine in the form of a square or rectangle
                    minRow = index.row;
                if (index.column <= minCol)
                    minCol = index.column;
                if (index.row > maxRow)
                    maxRow = index.row;
                if (index.column > maxCol)
                    maxCol = index.column;
            }

            for (int i = minRow; i <= maxRow; i++) {// checking on tempArray if we have a submarine
                for (int j = minCol; j <= maxCol; j++) {
                    if (tempArray[i][j] == 0) {
                        flag = 1;
                    }
                }
            }

            if (flag == 1)
                countSub--;
            flag = 0;
            minRow = Integer.MAX_VALUE;
            minCol = Integer.MAX_VALUE;
            maxRow = Integer.MIN_VALUE;
            maxCol = Integer.MIN_VALUE;
        }
        if (countSub < 0)
            countSub = 0;
        return countSub;
    }

}

