import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a Matrix Entity and functions that use primitiveMatrix
 */

public class Matrix implements Serializable {
    //Classes that do not implement Serializable interface will not have any of their state serialized or deserialized.

    /**
     * Neighboring Indices are up,down, left,right and diagonals
     *   1 1 0
     *   0 1 1
     *   1 0 0
     *
     *
     * [[(0,0),(1,1) ,(1,2)],
     * [(3,0),(3,1),(3,2)]]
     *
     */
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

    /*public Matrix() {
        Random r = new Random();
        primitiveMatrix = new int[5][5];
        for (int i = 0; i < primitiveMatrix.length; i++) {
            for (int j = 0; j < primitiveMatrix[0].length; j++) {
                primitiveMatrix[i][j] = r.nextInt(2);
            }
        }
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
        System.out.println("\n");
    }*/

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
     * getNeighbors() -this function finds all the indexes above, below ,on the sides and diagonally to specific index that equally to 1
     * @param index type of Index, represents start index
     * @return list (Collection) of all neighbors of specific index
     *
     *  [ 1 0 1
     *    0 1 0
     *    1 0 1 ]
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

    //if we need to find neighbors without diagonals:
   /* public Collection<Index> getNeighborsWithoutDiagonal(final Index index) {
        Collection<Index> list = new ArrayList<>();
        int extracted = -1;
        try{
            extracted = primitiveMatrix[index.row+1][index.column];
            list.add(new Index(index.row+1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column+1];
            list.add(new Index(index.row,index.column+1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row-1][index.column];
            list.add(new Index(index.row-1,index.column));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        try{
            extracted = primitiveMatrix[index.row][index.column-1];
            list.add(new Index(index.row,index.column-1));
        }catch (ArrayIndexOutOfBoundsException ignored){}
        return list;
    }*/

    /**
     * getReachable() - this function finds all the neighbors of specific index that their value equals to 1
     * @param index type of Index, represents start index
     * @return
     */
    public Collection<Index> getReachables(Index index) {
        ArrayList<Index> filteredIndices = new ArrayList<>();
        this.getNeighbors(index).stream().filter(i-> getValue(i)==1)
                .map(neighbor->filteredIndices.add(neighbor)).collect(Collectors.toList());
        return filteredIndices;
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
     * this method run on 'this' which represents matrix
     * use primitiveMatrix
     * @return list of all the indexes with value = '1'
     */
    public List<Index> findAllOnes() {
        List<Index> listAllOnes= new ArrayList<>();
        //we convert the matrix to list, and filtered each index in the list-
        //if the value==1 then we can map it to our returned list
        //the mapping is done into listAllOnes
        this.matrixToList(this.primitiveMatrix).stream().filter(i-> getValue(i)==1)
                .map(listAllOnes::add).collect(Collectors.toList());
        return listAllOnes;
    }

    /**
     * convert 2D array to List
     * we use this method in findAllOnes
     * @param primitiveMatrix type of matrix
     * @return list of indexes (?)
     */
    private List<Index> matrixToList(int[][] primitiveMatrix) {
        ArrayList<Index> asList= new ArrayList<>();
        //We will go over each index in the matrix and wrap it with Index
        //We will add each one to our list
        for(int i=0; i<primitiveMatrix.length;i++){
            for(int j=0; j<primitiveMatrix[i].length;j++)
                asList.add(new Index(i,j));
        }
        return asList;
    }
}
