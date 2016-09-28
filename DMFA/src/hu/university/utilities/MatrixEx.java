package hu.university.utilities;

public class MatrixEx extends Matrix implements Cloneable
{
    double[] columnSums;
    double[] rowSums;
    public MatrixEx(int rowCount, int columnCount)
    {
        super(rowCount,columnCount);

        this.rowSums = new double[rowCount];
        this.columnSums = new double[columnCount];
    }

    public void SetValue(int row, int column, double value)
    {
        double currentValue = GetValue(row, column);

        rowSums[row] += value - currentValue;
        columnSums[column] += value - currentValue;
        super.SetValue(row,column,value);
    }

    public MatrixEx NormalizeColumns()
    {
        MatrixEx normalized = new MatrixEx(rowCount, columnCount);

        for(int r = 0; r < rowCount; r++)
            for(int c = 0; c < columnCount; c++)
                normalized.SetValue(r,c, GetValue(r,c) / columnSums[c]);

        return normalized;
    }

    public MatrixEx NormalizeRows()
    {
        MatrixEx normalized = new MatrixEx(rowCount, columnCount);

        for(int r = 0; r < rowCount; r++)
            for(int c = 0; c < columnCount; c++)
                normalized.SetValue(r,c, GetValue(r,c) / rowSums[r]);

        return normalized;
    }

    public MatrixEx clone()
    {
        MatrixEx clone = new MatrixEx(rowCount, columnCount);
        for(int r = 0; r < rowCount; r++)
            for(int c = 0; c < columnCount; c++)
                clone.SetValue(r,c, GetValue(r,c));

        return clone;
    }

    public double GetRowSum(int rowNumber)
    {
        if(rowNumber >= rowCount)
            throw new IndexOutOfBoundsException();

        return rowSums[rowNumber];
    }

    public double GetColumnSum(int columnNumber)
    {
        if(columnNumber >= columnCount)
            throw new IndexOutOfBoundsException();

        return columnSums[columnNumber];
    }

    // Not threadSafe
    public void Increment(int rowNumber, int columnNumber)
    {
        if(columnNumber >= columnCount || rowNumber >= rowCount)
            throw new IndexOutOfBoundsException();

        double val = GetValue(rowNumber,columnNumber);
        columnSums[columnNumber]++;
        rowSums[rowNumber]++;
        val++;
        super.SetValue(rowNumber, columnNumber, val);
    }

}
