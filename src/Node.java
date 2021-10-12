import java.io.Serializable;
import java.util.Objects;

/**
 * This class wraps a concrete object and supplies getters and setters
 * @param <T>
 */

/**
 * The interface Serializable doesn't force us to implement any specific method.
 * Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.
 */
public class Node<T> implements Serializable {
    private T data;
    private Node<T> parent;


    public Node(T someObject, final Node<T> discoveredBy){
        this.data = someObject;
        this.parent = discoveredBy;
    }

    public Node(T someObject){
        this(someObject,null);
    }

    public T getData() {
        return data;
    }

    public Node(){
        this(null);
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getParent() {
        return parent;
    }

    public void setParent(Node<T> parent) {
        this.parent = parent;
    }


    /**
     * This is used when accessing objects multiple times with comparisons.
     * Equals objects have the same hashcode.
     * This method returns the hashcode of the current object, which is equal to the primitive int value.
     * @return
     */
    @Override
    public int hashCode() {
        return data != null ? data.hashCode():0;
    }

    /**
     * The function compares between objects (between all their data members) and returns boolean value
     * @param o Object represent the object we compare to
     * @return boolean answer
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node<?> state1 = (Node<?>) o;
        return Objects.equals(data, state1.data);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}
