import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements' adapter/wrapper/decorator design pattern
 */
public class TraversableMatrix implements Traversable<Index> {
    protected final Matrix matrix;
    protected Index startIndex, endIndex;

    public TraversableMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Index startIndex) {
        this.startIndex = startIndex;
    }


    public Index getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(Index endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    public int getValue(Node<Index> someNode) {
        return matrix.getValue(new Index(someNode.getData().row,someNode.getData().column));
    }

    @Override
    public int getValueN(Index someNode) {
        return this.matrix.getValue(someNode);
    }

    @Override
    public Node<Index> getOrigin() throws NullPointerException{
        if (this.startIndex == null) throw new NullPointerException("Start index is not initialized");
        return new Node<>(this.startIndex);

    }

    @Override
    public Node<Index> getDestination() throws NullPointerException{
        if (this.endIndex == null) throw new NullPointerException("End index is not initialized");
        return new Node<>(this.endIndex);
    }

    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        List<Node<Index>> reachableIndices = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
            if (matrix.getValue(index) == 1) {
                // A neighboring index whose value is 1
                Node<Index> indexNode = new Node<>(index, someNode);
                reachableIndices.add(indexNode);
            }
        }
        return reachableIndices;
    }

    @Override
    public Collection<Node<Index>> getNeighbors(Node<Index> someNode) {
        List<Node<Index>> NeighborIndex = new ArrayList<>();
        for (Index index : this.matrix.getNeighbors(someNode.getData())) {
            Node<Index> indexNode = new Node<>(index, someNode);
            NeighborIndex.add(indexNode);
        }
        return NeighborIndex;
    }

    @Override
    public String toString() {
        return matrix.toString();
    }

   public int getSize(){
        return this.getSize();
    }

}
