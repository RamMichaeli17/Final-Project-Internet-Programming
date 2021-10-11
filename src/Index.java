import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a location in a matrix based on row and column
 */

//הסבר מה זה סריזאבל
public class Index implements Serializable {
    //Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.
    int row, column;

    //Constructor
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
     * @param o Object represent the object we compare to
     * @return boolean answer
     */

    //להסביר את המטודה של איקוולס האם משווה כתובות ? פרמיטבי/לא וכו'
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
