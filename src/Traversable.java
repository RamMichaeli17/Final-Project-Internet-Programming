import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 */
public interface Traversable<T> {

    /**
     * This method returns the start node
     * @return Node<T>
     */
    public Node<T> getOrigin();

    /**
     * This method give us the reachable nodes from current node include diagonals.
     * @param someNode represents current node
     * @return Collection<Node<T>> -a collection (list) of all the reachable nodes
     */
    public Collection<Node<T>>  getReachableNodes(Node<T> someNode);

    public Collection<Node<T>>  getNeighbors(Node<T> someNode);
    /**
     * This method
     * @param index
     */
    public void setStartIndex(Index index);

    public void setEndIndex(Index index);

    public int getSize();

    public int getValue(Node<T> someNode);

    public int getValueN(T someNode);

    /**
     * This method give us the destination node (makes the index be a node)
     * @return Node<T> destination node
     *
     */
    public Node<T> getDestination();

      public Collection<Node<Index>> getReachableWeight(Node<T> someNode);

}
