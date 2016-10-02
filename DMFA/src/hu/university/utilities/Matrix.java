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
        values[row][column] = value;
    }

    public double GetValue(int row, int column)
    {
        return values[row][column];
    }
}
