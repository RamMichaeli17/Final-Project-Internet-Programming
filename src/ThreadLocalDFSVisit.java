import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * When we need to make sure that every thread has its own local data structures  - synchronization is not the solution.
 * TLS- Thread Local Storage
 */
public class ThreadLocalDFSVisit<T> {
    // ForkJoinPool
    // SparkRDD

    final ThreadLocal<Stack<Node<T>>> threadLocalStack = ThreadLocal.withInitial(() -> new Stack<Node<T>>()); // lambda expression
    //    final ThreadLocal<Stack<Node<T>>> threadLocalStack2 = ThreadLocal.withInitial(Stack::new); // method reference
    final ThreadLocal<Set<Node<T>>> threadLocalSet = ThreadLocal.withInitial(LinkedHashSet::new);

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(); //using lock in callable

    /**
     * submit is a function that return value.
     * reminder -callable returns value.
     *
     * Future represents the result of an asynchronous computation. When the asynchronous task is created,a Java Future object is returned.
     * This Future object functions as a handle to the result of the asynchronous task
     *
     * parallelDFSVisitTraverse is function that find SCC parallely.
     * why hashSet? HashSet is a collection of items where every item is unique - we doesn't want multiplication
     *
     * @param SomeGraph represent current Graph (we relating matrix as graph)
     * @param listOfIndex represent indexes to run on them
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
                    //get() is a blocking call, its the 'input' of the callable
                    listIndexScc.add(futureScc.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            this.threadPoolExecutor.shutdown();
        return listIndexScc;
    }

    /**
     * traverse: The function execute DFS method by ThreadLocal
     * @param someGraph represent current Graph (we relating matrix as graph)
     *
     * @return List<T> - list of connected component(path).
     */

    public List<T> traverse(Traversable<T> someGraph) {
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
        List<T> connectedComponent = new ArrayList<>();
        for (Node<T> node : threadLocalSet.get()) connectedComponent.add(node.getData());

        //add explain why we need this
        threadLocalStack.get().clear(); //there is no reason to clear the stack other than readability
        threadLocalSet.get().clear();

        return connectedComponent;
        }


    /**
     * submarine: the function count number of valid submarines
     * @param hashSetList type: HashSet<HashSet<Index>>
     * @param primitiveMatrix type: int[][]
     * @return int
     */
    public int CheckSub(HashSet<HashSet<Index>> hashSetList, int[][] primitiveMatrix) {
        int count = hashSetList.size(), minRow = 10000, minCol = 10000, maxRow = -1, maxCol = -1;
        int flag = 0;
        for (HashSet<Index> s : hashSetList) {
            for (Index index : s) {

                if (s.size() == 1) {
                    flag = 1;
                }
                if (flag == 1)
                    count--;
                flag = 0;
                if (index.row <= minRow)
                    minRow = index.row;
                if (index.column <= minCol)
                    minCol = index.column;
                if (index.row > maxRow)
                    maxRow = index.row;
                if (index.column > maxCol)
                    maxCol = index.column;
            }

            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (primitiveMatrix[i][j] == 0) {
                        flag = 1;
                    }

                }

            }
            if (flag == 1)
                count--;
            flag = 0;
            minRow = 10000;
            minCol = 10000;
            maxRow = -1;
            maxCol = -1;
        }
        if (count < 0)
            count = 0;
        return count;
    }

}


/**Option 2 */

//
//public class SubCheck {
//
//    /**
//     * @param primitiveMatrix 2 dimension array
//     * This function uses the first algorithm (task 1) in order to receive all connected component in the given matrix
//     * for each component call to auxiliary function (isValidSubmarine) and increase the results if it's
//     * a valid submarine (square or rectangle)
//     */
//
//    public static int countValidSubmarines(int[][] primitiveMatrix) throws IllegalArgumentException {
//
//        /**List<HashSet<Index>> connectedComponents = find connectednode(primitiveMatrix);
//         */
//        int result = 0;
//        // Validate the connected components
//        // Go over all the components and checks if it's a valid submarine
//        // If the connected component is a square or a rectangle add 1 to result
//        // Using auxiliary function: isValidSubmarine
//        for(HashSet<Index> component : connectedComponents) {
//            result += isValidSubmarine(component);
//        }
//        return result;
//    }
//
//
//
//    /**
//     * Auxiliary function for the submarine algorithm
//     * @param connectedComponent HashSet of Index objects (connected component)
//     * This function receives a HashSet of indices (connected component) and checks if is contains
//     * at least two indices and its a rectangle or a square
//     */
//    private static int isValidSubmarine(HashSet<Index> connectedComponent) {
//        // the component should contain at least two elements (indices)
//        if (connectedComponent.size() < 2) {
//            return 0;
//        }
//        // find the edges of the component to check if it is a rectangle
//        int rightEdge = Collections.max(connectedComponent, Comparator.comparingInt(Index::getColumn)).getColumn();
//        int leftEdge = Collections.min(connectedComponent, Comparator.comparingInt(Index::getColumn)).getColumn();
//        int bottomEdge = Collections.max(connectedComponent, Comparator.comparingInt(Index::getRow)).getRow();
//        int topEdge = Collections.min(connectedComponent, Comparator.comparingInt(Index::getRow)).getRow();
//
//        // calculate the number of expected element of the rectangle
//        int numOfExpectedElements = (rightEdge - leftEdge + 1) * (bottomEdge - topEdge + 1);
//
//        // If the expected number of element equals to number of the element in the component (size)
//        // so it's a valid submarine
//        if (connectedComponent.size() == numOfExpectedElements) {
//            return 1;
//        }
//        return 0;
//    }
//
//
//
//}
//
//
//
