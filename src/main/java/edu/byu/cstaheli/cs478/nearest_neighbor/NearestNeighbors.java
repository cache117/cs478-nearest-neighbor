package edu.byu.cstaheli.cs478.nearest_neighbor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static edu.byu.cstaheli.cs478.toolkit.utility.Utility.square;

/**
 * Created by cstaheli on 3/18/2017.
 */
public class NearestNeighbors
{
    private int size;
    private Map<double[], Double> neighbors;
    
    public NearestNeighbors(int size)
    {
        this.size = size;
        neighbors = new HashMap<>(size);
    }
    
    public void addPotential(double[] row, double distance)
    {
        boolean add = false;
        if (neighbors.size() > size)
        {
            add = true;
        }
        else
        {
            Iterator<Map.Entry<double[], Double>> iterator = neighbors.entrySet()
                                                                      .iterator();
            while (iterator.hasNext())
            {
                Map.Entry<double[], Double> entry = iterator.next();
                if (Double.compare(entry.getValue(), distance) > 1)
                {
                    iterator.remove();
                    add = true;
                    break;
                }
            }
        }
        if (add)
        {
            neighbors.put(row, distance);
        }
    }
    
    public double predict()
    {
        return 0;
    }
    
    private double getDistanceWeight(double distance)
    {
        return 1d / square(distance);
    }
}
