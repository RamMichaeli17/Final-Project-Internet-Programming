import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a Matrix Entity and functions that use a 2D array (primitiveMatrix)
 */

public class Matrix implements Serializable {
    //Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.

    int[][] primitiveMatrix;

    //constructor
    public Matrix(int[][] oArray){
        List<int[]> list = new ArrayList<>();
        for (int[] row : oArray) {
            int[] clone = row.clone();
            list.add(clone);
        }
        primitiveMatrix = list.toArray(new int[0][]);
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (int[] row : primitiveMatrix) {
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * getNeighbors() -this function finds all the indexes above, below ,on the sides and diagonally to specific index (without considering the data - 'if index[X][Y]==1')
     * @param index type of Index, represents start index
     * @return list (Collection) of all neighbors of specific index
     */
    public Collection<Index> getNeighbors(final Index index){
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{ //below
            extracted = primitiveMatrix[index.row+1][index.column];
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ //right
            extracted = primitiveMatrix[index.row][index.column+1];
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ //above
            extracted = primitiveMatrix[index.row-1][index.column];
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{ //left
            extracted = primitiveMatrix[index.row][index.column-1];
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}

        //diagonals - 4 cases :
        try{
            //up-right
            extracted = primitiveMatrix[index.row+1][index.column+1];
            list.add(new Index(index.row+1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            //up-left
            extracted = primitiveMatrix[index.row-1][index.column-1];
            list.add(new Index(index.row-1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            //down-left
            extracted = primitiveMatrix[index.row+1][index.column-1];
            list.add(new Index(index.row+1,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            //down-right
            extracted = primitiveMatrix[index.row-1][index.column+1];
            list.add(new Index(index.row-1,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }

    public int getValue(final Index index){
        return primitiveMatrix[index.row][index.column];
    }

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public final int[][] getPrimitiveMatrix() {
        return primitiveMatrix;
    }

    /**
     * This function returns a list of indexes that their value is 1
     * @return list of all the indexes with value = '1'
     */
    public List<Index> findAllOnes() {
        List<Index> listAllOnes = new ArrayList<>();
        Index index;
        for (int i = 0; i < this.primitiveMatrix.length; i++) {
            for (int j = 0; j < this.primitiveMatrix[i].length; j++) {
                if (primitiveMatrix[i][j] == 1) {
                    index = new Index(i, j);
                    listAllOnes.add(index);
                }
            }
        }
        return listAllOnes;
    }
}
