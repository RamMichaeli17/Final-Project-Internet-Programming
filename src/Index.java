import java.io.Serializable;

/**
 * Represents a location in a Matrix based on row and column
 */
public class Index implements Serializable {
    int row, column;

    public Index(int row, int column){
        if(row < 0 || column < 0)
            throw new IllegalArgumentException("row/column cannot be negative");
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Index index = (Index) o;

        if (row != index.row) return false;
        return column == index.column;
    }

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


    public static void main(String[] args) {
        Index index = new Index(1,1);
        String name = "Yossi";
        System.out.println(index.equals(index));
        System.out.println(index.equals(name));
        System.out.println(index);

        int[] numArray = new int[]
                {20,15,963};
        System.out.println(numArray);

        int[][] twoDimensionalArray = new int[][]
                {
                        {1,2,3},
                        {4,5,6},
                        {7,8,9}
                };
    }
}
