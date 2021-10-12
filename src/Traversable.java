import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 */
public interface Traversable<T> {
    public Node<T> getOrigin();

    public Collection<Node<T>>  getReachableNodes(Node<T> someNode);

    public Collection<Node<T>>  getNeighbors(Node<T> someNode);

    public void setStartIndex(Index index);

    public void setEndIndex(Index index);

    public int getSize();

    public int getValue(Node<T> someNode);

    public int getValueN(T someNode);

    public Node<T> getDestination();

}
