package edu.byu.cstaheli.cs478.toolkit.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by cstaheli on 3/9/2017.
 */
public class Utility
{
    public static double[] removeColumnFromRow(int column, double[] row)
    {
        List<Double> newRow = Arrays.stream(row)
                .boxed()
                .collect(Collectors.toList());
        newRow.remove(column);
        return newRow.stream()
                .mapToDouble(i -> i)
                .toArray();
    }
    
    public static double square(double value)
    {
        return value * value;
    }
}
