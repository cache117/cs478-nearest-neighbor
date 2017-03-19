package edu.byu.cstaheli.cs478.nearest_neighbor;

import edu.byu.cstaheli.cs478.toolkit.exception.MatrixException;
import edu.byu.cstaheli.cs478.toolkit.learner.SupervisedLearner;
import edu.byu.cstaheli.cs478.toolkit.strategy.LearningStrategy;
import edu.byu.cstaheli.cs478.toolkit.utility.Matrix;

import java.util.Map;

import static edu.byu.cstaheli.cs478.toolkit.utility.Utility.square;

/**
 * Created by cstaheli on 3/14/2017.
 */
public class NearestNeighbor extends SupervisedLearner
{
    private int numberOfNeighborsToCompareTo;
    private Matrix trainingData;
    
    public NearestNeighbor()
    {
        this.numberOfNeighborsToCompareTo = 0;
    }
    
    @Override
    public void train(LearningStrategy strategy) throws Exception
    {
        trainingData = strategy.getTrainingData();
    }
    
    @Override
    public void predict(double[] features, double[] labels) throws Exception
    {
    
    }
    
    protected double euclideanDistance(double[] x, double[] y)
    {
        assert x.length == y.length;
        double distance = 0;
        for (int i = 0; i < x.length; ++i)
        {
            distance += distance(x[i], y[i]);
        }
        return Math.sqrt(distance);
    }
    
    private double distance(double first, double second)
    {
        return square(first - second);
    }
    
    protected double getValueDistanceMetric(int firstColumn, double firstValue, int secondColumn, double secondValue) throws MatrixException
    {
        double valueDistanceMetric = 0;
        Map<Double, Integer> outputOccurrences = trainingData.getColumnOccurrences(trainingData.cols() - 1);
        Matrix hasFirstValue = trainingData.getRowsWithColumnClass(firstColumn, firstValue);
        //# Times attribute a had value x
        double nax = hasFirstValue.rows();
        Matrix hasSecondValue = trainingData.getRowsWithColumnClass(secondColumn, secondValue);
        //# Times attribute a had value y
        double nay = hasSecondValue.rows();
        for (Map.Entry<Double, Integer> entry : outputOccurrences.entrySet())
        {
            Matrix hasFirstAndOutput = hasFirstValue.getRowsWithColumnClass(hasFirstValue.cols() - 1, entry.getKey());
            //# times attribute a=x and class was c
            double naxc = hasFirstAndOutput.rows();
            Matrix hasSecondAndOutput = hasSecondValue.getRowsWithColumnClass(hasSecondValue.cols() - 1, entry.getKey());
            //# times attribute a=y and class was c
            double nayc = hasSecondAndOutput.rows();
            valueDistanceMetric += distance(naxc / nax, nayc / nay);
        }
        return valueDistanceMetric;
    }
    
    public int getNumberOfNeighborsToCompareTo()
    {
        return numberOfNeighborsToCompareTo;
    }
    
    public void setNumberOfNeighborsToCompareTo(int numberOfNeighborsToCompareTo)
    {
        this.numberOfNeighborsToCompareTo = numberOfNeighborsToCompareTo;
    }
    
    public Matrix getTrainingData()
    {
        return trainingData;
    }
    
    public void setTrainingData(Matrix trainingData)
    {
        this.trainingData = trainingData;
    }
}
