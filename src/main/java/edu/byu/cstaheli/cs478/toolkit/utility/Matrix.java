package edu.byu.cstaheli.cs478.toolkit.utility;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import edu.byu.cstaheli.cs478.decision_tree.util.Utility;
import edu.byu.cstaheli.cs478.toolkit.exception.ARFFParseException;
import edu.byu.cstaheli.cs478.toolkit.exception.IncompatibleMatrixException;
import edu.byu.cstaheli.cs478.toolkit.exception.MatrixException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

public class Matrix
{
    private static double MISSING = Double.MAX_VALUE; // representation of missing values in the dataset
    // Data
    private ArrayList<double[]> m_data;
    // Meta-data
    private ArrayList<String> m_attr_name;
    private ArrayList<TreeMap<String, Integer>> m_str_to_enum;
    private ArrayList<TreeMap<Integer, String>> m_enum_to_str;
    private String datasetName;
    private boolean binRealValues;

    // Creates a 0x0 matrix. You should call loadARFF or setSize next.
    public Matrix()
    {
    }

    public Matrix(String fileName) throws FileNotFoundException, ARFFParseException
    {
        this();
        loadArff(fileName);
    }

    public Matrix(Matrix that)
    {
        this(that, 0, 0, that.rows(), that.cols());
    }

    // Copies the specified portion of that matrix into this matrix
    public Matrix(Matrix that, int rowStart, int colStart, int rowCount, int colCount)
    {
        m_data = new ArrayList<>();
        for (int j = 0; j < rowCount; j++)
        {
            double[] rowSrc = that.row(rowStart + j);
            double[] rowDest = new double[colCount];
            for (int i = 0; i < colCount; i++)
                rowDest[i] = rowSrc[colStart + i];
            m_data.add(rowDest);
        }
        m_attr_name = new ArrayList<>();
        m_str_to_enum = new ArrayList<>();
        m_enum_to_str = new ArrayList<>();
        for (int i = 0; i < colCount; i++)
        {
            m_attr_name.add(that.attrName(colStart + i));
            m_str_to_enum.add(that.m_str_to_enum.get(colStart + i));
            m_enum_to_str.add(that.m_enum_to_str.get(colStart + i));
        }
    }

    public boolean isBinRealValues()
    {
        return binRealValues;
    }

    public void setBinRealValues(boolean binRealValues)
    {
        this.binRealValues = binRealValues;
    }

    // Adds a copy of the specified portion of that matrix to this matrix
    public void add(Matrix that, int rowStart, int colStart, int rowCount) throws MatrixException
    {
        if (colStart + cols() > that.cols())
            throw new IndexOutOfBoundsException(String.format("Out of Range: %d + %d > %d", colStart, cols(), that.cols()));
        for (int i = 0; i < cols(); i++)
        {
            if (that.valueCount(colStart + i) != valueCount(i))
                throw new IncompatibleMatrixException(String.format("incompatible Relations: %d != %d", that.valueCount(colStart + i), valueCount(i)));
        }
        for (int j = 0; j < rowCount; j++)
        {
            double[] rowSrc = that.row(rowStart + j);
            double[] rowDest = new double[cols()];
            for (int i = 0; i < cols(); i++)
                rowDest[i] = rowSrc[colStart + i];
            m_data.add(rowDest);
        }
    }

    public void removeRow(int row)
    {
        m_data.remove(row);
    }

    public void removeColumn(int column)
    {
        m_attr_name.remove(column);
        m_str_to_enum.remove(column);
        m_enum_to_str.remove(column);
        for (int i = 0; i < m_data.size(); ++i)
        {
            m_data.set(i, Utility.removeColumnFromRow(column, m_data.get(i)));
        }
    }

    // Resizes this matrix (and sets all attributes to be continuous)
    public void setSize(int rows, int cols)
    {
        m_data = new ArrayList<>();
        for (int j = 0; j < rows; j++)
        {
            double[] row = new double[cols];
            m_data.add(row);
        }
        m_attr_name = new ArrayList<>();
        m_str_to_enum = new ArrayList<>();
        m_enum_to_str = new ArrayList<>();
        for (int i = 0; i < cols; i++)
        {
            m_attr_name.add("");
            m_str_to_enum.add(new TreeMap<>());
            m_enum_to_str.add(new TreeMap<>());
        }
    }

    // Loads from an ARFF file
    public void loadArff(String filename) throws ARFFParseException, FileNotFoundException
    {
        m_data = new ArrayList<>();
        m_attr_name = new ArrayList<>();
        m_str_to_enum = new ArrayList<>();
        m_enum_to_str = new ArrayList<>();
        boolean READDATA = false;
        Scanner s = new Scanner(new File(filename));
        while (s.hasNext())
        {
            String line = s.nextLine().trim();
            if (line.length() > 0 && line.charAt(0) != '%')
            {
                if (!READDATA)
                {
                    Scanner t = new Scanner(line);
                    String firstToken = t.next().toUpperCase();

                    if (firstToken.equals("@RELATION"))
                    {
                        datasetName = t.nextLine();
                    }

                    if (firstToken.equals("@ATTRIBUTE"))
                    {
                        TreeMap<String, Integer> ste = new TreeMap<>();
                        m_str_to_enum.add(ste);
                        TreeMap<Integer, String> ets = new TreeMap<>();
                        m_enum_to_str.add(ets);

                        Scanner u = new Scanner(line);
                        if (line.contains("'")) u.useDelimiter("'");
                        u.next();
                        String attributeName = u.next();
                        if (line.contains("'")) attributeName = "'" + attributeName + "'";
                        m_attr_name.add(attributeName);

                        int vals = 0;
                        String type = u.next().trim().toUpperCase();
                        if (!type.equals("REAL") && !type.equals("CONTINUOUS") && !type.equals("INTEGER"))
                        {
                            try
                            {
                                String values = line.substring(line.indexOf("{") + 1, line.indexOf("}"));
                                Scanner v = new Scanner(values);
                                v.useDelimiter(",");
                                while (v.hasNext())
                                {
                                    String value = v.next().trim();
                                    if (value.length() > 0)
                                    {
                                        ste.put(value, vals);
                                        ets.put(vals, value);
                                        vals++;
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                throw new ARFFParseException("Error parsing line: " + line + "\n" + e.toString());
                            }
                        }
                    }
                    if (firstToken.equals("@DATA"))
                    {
                        READDATA = true;
                    }
                }
                else
                {
                    double[] newrow = new double[cols()];
                    int curPos = 0;

                    try
                    {
                        Scanner t = new Scanner(line);
                        t.useDelimiter(",");
                        while (t.hasNext())
                        {
                            String textValue = t.next().trim();
                            //System.out.println(textValue);

                            if (textValue.length() > 0)
                            {
                                double doubleValue;
                                int vals = m_enum_to_str.get(curPos).size();

                                //Missing instances appear in the dataset as a double defined as MISSING
                                if (textValue.equals("?"))
                                {
                                    doubleValue = MISSING;
                                }
                                // Continuous values appear in the instance vector as they are
                                else if (vals == 0)
                                {
                                    doubleValue = Double.parseDouble(textValue);
                                }
                                // Discrete values appear as an index to the "name"
                                // of that value in the "attributeValue" structure
                                else
                                {
                                    doubleValue = m_str_to_enum.get(curPos).get(textValue);
                                    if (doubleValue == -1)
                                    {
                                        throw new ARFFParseException("Error parsing the value '" + textValue + "' on line: " + line);
                                    }
                                }

                                newrow[curPos] = doubleValue;
                                curPos++;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        throw new ARFFParseException("Error parsing line: " + line + "\n" + e.toString());
                    }
                    m_data.add(newrow);
                }
            }
        }
        if (binRealValues)
        {
            for (int i = 0; i < m_data.size(); ++i)
            {
                double[] row = m_data.get(i);
                for (int j = 0; j < row.length; ++j)
                {
                    if (valueCount(j) == 0)
                    {
                        double value = row[j];
                        double mean = columnMean(j);
                        double standardDeviation = columnStandardDeviation(j);
                        double zScore = zScore(value, mean, standardDeviation);
                        int numberOfStandardDeviations = (int) zScore;
                        set(i, j, numberOfStandardDeviations);
                    }
                }
            }
            for (int i = 0; i < cols(); ++i)
            {
                if (valueCount(i) == 0)
                {
                    TreeMap<String, Integer> ste = m_str_to_enum.get(i);
                    TreeMap<Integer, String> ets = m_enum_to_str.get(i);
                    Map<Double, Integer> columnOccurrences = getColumnOccurrences(i);
                    int counter = 0;
                    for (Map.Entry<Double, Integer> entry : columnOccurrences.entrySet())
                    {
                        ste.put(String.valueOf(entry.getKey()), counter);
                        ets.put(counter, String.valueOf(entry.getKey()));
                        ++counter;
                    }
                }
            }
        }
    }

    // Returns the number of rows in the matrix
    public int rows()
    {
        return m_data.size();
    }

    // Returns the number of columns (or attributes) in the matrix
    public int cols()
    {
        return m_attr_name.size();
    }

    // Returns the specified row
    public double[] row(int r)
    {
        return m_data.get(r);
    }

    // Returns the element at the specified row and column
    public double get(int r, int c)
    {
        return m_data.get(r)[c];
    }

    // Sets the value at the specified row and column
    public void set(int r, int c, double v)
    {
        row(r)[c] = v;
    }

    // Returns the name of the specified attribute
    public String attrName(int col)
    {
        return m_attr_name.get(col);
    }

    // Set the name of the specified attribute
    public void setAttrName(int col, String name)
    {
        m_attr_name.set(col, name);
    }

    // Returns the name of the specified value
    public String attrValue(int attr, int val)
    {
        return m_enum_to_str.get(attr).get(val);
    }

    // Returns the number of values associated with the specified attribute (or column)
    // 0=continuous, 2=binary, 3=trinary, etc.
    public int valueCount(int col)
    {
        return m_enum_to_str.get(col).size();
    }

    // Shuffles the row order
    public void shuffle(Random rand)
    {
        for (int n = rows(); n > 0; n--)
        {
            int i = rand.nextInt(n);
            double[] tmp = row(n - 1);
            m_data.set(n - 1, row(i));
            m_data.set(i, tmp);
        }
    }

    // Shuffles the row order with a buddy matrix
    public void shuffle(Random rand, Matrix buddy)
    {
        for (int n = rows(); n > 0; n--)
        {
            int i = rand.nextInt(n);
            double[] tmp = row(n - 1);
            m_data.set(n - 1, row(i));
            m_data.set(i, tmp);


            double[] tmp1 = buddy.row(n - 1);
            buddy.m_data.set(n - 1, buddy.row(i));
            buddy.m_data.set(i, tmp1);
        }
    }

    // Returns the mean of the specified column
    public double columnMean(int col)
    {
        double sum = 0;
        int count = 0;
        for (int i = 0; i < rows(); i++)
        {
            double v = get(i, col);
            if (v != MISSING)
            {
                sum += v;
                count++;
            }
        }
        return sum / count;
    }

    public double columnStandardDeviation(int col)
    {
        double sum = 0;
        double average = columnMean(col);
        for (int i = 0; i < rows(); ++i)
        {
            double value = get(i, col);
            if (value != MISSING)
            {
                sum += square(value - average) / rows();
            }
        }
        return Math.sqrt(sum);
    }

    private double zScore(double value, double mean, double standardDeviation)
    {
        return (value - mean) / standardDeviation;
    }

    public void convertAllToNominal()
    {

    }

    private double square(double value)
    {
        return value * value;
    }

    // Returns the min value in the specified column
    public double columnMin(int col)
    {
        double m = MISSING;
        for (int i = 0; i < rows(); i++)
        {
            double v = get(i, col);
            if (v != MISSING)
            {
                if (m == MISSING || v < m)
                    m = v;
            }
        }
        return m;
    }

    // Returns the max value in the specified column
    public double columnMax(int col)
    {
        double m = MISSING;
        for (int i = 0; i < rows(); i++)
        {
            double v = get(i, col);
            if (v != MISSING)
            {
                if (m == MISSING || v > m)
                    m = v;
            }
        }
        return m;
    }

    // Returns the most common value in the specified column
    public double mostCommonValue(int col)
    {
        Map<Double, Integer> tm = getColumnOccurrences(col);
        int maxCount = 0;
        double val = MISSING;
        for (Entry<Double, Integer> e : tm.entrySet())
        {
            if (e.getValue() > maxCount)
            {
                maxCount = e.getValue();
                val = e.getKey();
            }
        }
        return val;
    }

    public Map<Double, Integer> getColumnOccurrences(int col)
    {
        Map<Double, Integer> tm = new TreeMap<>();
        for (int i = 0; i < rows(); i++)
        {
            double v = get(i, col);
//            if (v != MISSING)
//            {
            Integer count = tm.get(v);
            if (count == null)
                tm.put(v, 1);
            else
                tm.put(v, count + 1);
//            }
        }
        return tm;
    }

    public Matrix getRowsWithColumnClass(int columnClass, double value) throws MatrixException
    {
        Matrix newMatrix = new Matrix(this);
        Double doubleValue = value;

        newMatrix.m_data.removeIf(row -> !doubleValue.equals(row[columnClass]));
        newMatrix.removeColumn(columnClass);
        return newMatrix;
    }

    public void normalize()
    {
        for (int i = 0; i < cols(); i++)
        {
            if (valueCount(i) == 0)
            {
                double min = columnMin(i);
                double max = columnMax(i);
                for (int j = 0; j < rows(); j++)
                {
                    double v = get(j, i);
                    if (v != MISSING)
                        set(j, i, (v - min) / (max - min));
                }
            }
        }
    }

    public void print()
    {
        System.out.println("@RELATION " + datasetName);
        for (int i = 0; i < m_attr_name.size(); i++)
        {
            System.out.print("@ATTRIBUTE " + m_attr_name.get(i));
            int vals = valueCount(i);
            if (vals == 0)
                System.out.println(" CONTINUOUS");
            else
            {
                System.out.print(" {");
                for (int j = 0; j < vals; j++)
                {
                    if (j > 0)
                        System.out.print(", ");
                    System.out.print(m_enum_to_str.get(i).get(j));
                }
                System.out.println("}");
            }
        }
        System.out.println("@DATA");
        for (int i = 0; i < rows(); i++)
        {
            double[] r = row(i);
            for (int j = 0; j < r.length; j++)
            {
                if (j > 0)
                    System.out.print(", ");
                if (valueCount(j) == 0)
                    System.out.print(r[j]);
                else
                    System.out.print(m_enum_to_str.get(j).get((int) r[j]));
            }
            System.out.println("");
        }
    }
}
