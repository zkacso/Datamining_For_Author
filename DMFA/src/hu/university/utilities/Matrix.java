package hu.university.utilities;

public class Matrix
{
    private double[][] values;
    protected final int rowCount;
    protected final int columnCount;

    public Matrix(int rows, int columns)
    {
        this.values = new double[rows][columns];
        this.rowCount = rows;
        this.columnCount = columns;
    }

    public void SetValue(int row, int column, double value)
    {
        if(row >= rowCount || column >= columnCount)
            throw new IndexOutOfBoundsException("Matrix row/column index is out of range.");

        values[row][column] = value;
    }

    public double GetValue(int row, int column)
    {
        if(row >= rowCount || column >= columnCount)
            throw new IndexOutOfBoundsException("Matrix row/column index is out of range.");

        return values[row][column];
    }
}
