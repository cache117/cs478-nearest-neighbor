package edu.byu.cstaheli.cs478.nearest_neighbor;

import edu.byu.cstaheli.cs478.toolkit.MLSystemManager;
import org.junit.jupiter.api.Test;

import static edu.byu.cstaheli.cs478.other.Other.assertNumbersEqualWithEpsilon;

/**
 * Created by cstaheli on 3/14/2017.
 */
class NearestNeighborTest
{
    private static String datasetsLocation = "src/test/resources/datasets/nearest_neighbor/";
    
    @Test
    void distance()
    {
    
    }
    
    @Test
    void valueDistanceMetric()
    {
        
    }
    
    @Test
    void predict() throws Exception
    {
        
    }
    
    @Test
    void euclideanDistance() throws Exception
    {
        
    }
    
    @Test
    void square() throws Exception
    {
        double first = 0;
        double second = 1;
        double result = NearestNeighbor.squaredDistance(first, second);
        assertNumbersEqualWithEpsilon(1, result, .00001);
        
    }
    
    @Test
    void runManager() throws Exception
    {
        String[] args;
        MLSystemManager manager = new MLSystemManager();
        args = ("-L knn -A " + datasetsLocation + "homework.arff -E training -V").split(" ");
        manager.run(args);
    }
}