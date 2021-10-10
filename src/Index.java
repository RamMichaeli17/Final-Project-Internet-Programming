import java.io.Serializable;

/**
 * Represents a location in a Matrix based on row and column
 */
public class Index implements Serializable {
    //Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.
    int row, column;

    //constructor
    public Index(int row, int column){
        if(row < 0 || column < 0)
            throw new IllegalArgumentException("row/column cannot be negative");
        this.row = row;
        this.column = column;
    }

    // public int getRow() {  return row;  }

   // public int getColumn() {return column;}
    /**
     * The function compares between objects (between all their data members) and returns boolean value
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
    //equals objects have the same hashcode
    //This method returns the hashcode of the current object, which is equal to the primitive int value.
    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString(){
        return "(" + row + "," + column + ")";
    }

}
