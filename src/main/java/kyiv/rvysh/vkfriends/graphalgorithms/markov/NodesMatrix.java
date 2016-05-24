package kyiv.rvysh.vkfriends.graphalgorithms.markov;

/**
 *
 * @author Jiri Krizek
 */
public interface NodesMatrix {
    
    public void setEpsilon(double EPSILON);
    
    public boolean isBinary();
    
    public String toStringWithoutZeros();
    
    public double[] getRowNum(int row);
    
    public void set(int row, int col, double value);
    public double set(int index, double value);
    
    public NodesMatrix copyMatrix();

    public int getNumCols();

    public int getNumRows();
    
    public int getNumElements();
    
    public double get(int index);
    public double get(int row, int col);
    
    public NodesMatrix multiply(NodesMatrix a, NodesMatrix b);

    public NodesMatrix sumCols();

    public void removeZeroRows();
    
}
