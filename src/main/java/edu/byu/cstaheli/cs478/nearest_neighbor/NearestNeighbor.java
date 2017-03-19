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
        NearestNeighbors nearestNeighbors = new NearestNeighbors(numberOfNeighborsToCompareTo);
        for (int i = 0; i < trainingData.rows(); ++i)
        {
            double[] row = trainingData.row(i);
            double distance = distance(row, features);
            nearestNeighbors.addPotential(row, distance);
        }
        labels[0] = nearestNeighbors.predict();
    }
    
    private double distance(double[] first, double[] second) throws MatrixException
    {
        assert first.length == second.length;
        double distance = 0;
        for (int i = 0; i < first.length; ++i)
        {
            distance += distance(i, first[i], second[i]);
        }
        return distance;
    }
    
    private double distance(int column, double firstValue, double secondValue) throws MatrixException
    {
        if (new Double(firstValue).equals(Matrix.MISSING) || new Double(secondValue).equals(Matrix.MISSING))
        {
            return 1;
        }
        if (trainingData.valueCount(column) == 0)
        {
            return euclideanDistance(firstValue, secondValue);
        }
        else
        {
            return valueDistanceMetric(column, firstValue, secondValue);
        }
    }
    
    protected double euclideanDistance(double x, double y)
    {
        return Math.sqrt(squaredDistance(x, y));
    }
    
    private double squaredDistance(double first, double second)
    {
        return square(first - second);
    }
    
    protected double valueDistanceMetric(int column, double firstValue, double secondValue) throws MatrixException
    {
        double valueDistanceMetric = 0;
        Map<Double, Integer> outputOccurrences = trainingData.getColumnOccurrences(trainingData.cols() - 1);
        Matrix hasFirstValue = trainingData.getRowsWithColumnClass(column, firstValue);
        //# Times attribute a had value x
        double nax = hasFirstValue.rows();
        Matrix hasSecondValue = trainingData.getRowsWithColumnClass(column, secondValue);
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
            double paxc = naxc / nax;
            double payc = nayc / nay;
            valueDistanceMetric += squaredDistance(paxc, payc);
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
