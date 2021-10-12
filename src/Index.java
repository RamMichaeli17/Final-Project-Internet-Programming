import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a location in a matrix based on row and column
 */

/**
 * The interface Serializable doesn't force us to implement any specific method.
 * Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.
 */
public class Index implements Serializable {

    int row, column;

    public Index(int row, int column){
        if(row < 0 || column < 0)
            throw new IllegalArgumentException("Row/Column cannot be negative");
        this.row = row;
        this.column = column;
    }

    public int getRow() {  return row;  }

    public int getColumn() {return column;}

    /**
     * The function compares between objects (between all their data members) and returns boolean value
     * Both equals() and “==” operator in Java are used to compare objects to check equality.
     * == operator is for reference comparison (address comparison) - checks if both objects point to the same memory location.
     * equals() method for content comparison - evaluates to the comparison of values in the objects.
     * @param o Object represent the object we compare to
     * @return boolean answer
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Index index = (Index) o;
        if (row != index.row) return false;
        return column == index.column;
    }

    /**
     * Equals objects have the same hashcode.
     * This method returns the hashcode of the current object, which is equal to the primitive int value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString(){
        return "(" + row + "," + column + ")";
    }

}
