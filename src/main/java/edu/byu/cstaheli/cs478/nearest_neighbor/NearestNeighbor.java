package edu.byu.cstaheli.cs478.nearest_neighbor;

import edu.byu.cstaheli.cs478.toolkit.learner.SupervisedLearner;
import edu.byu.cstaheli.cs478.toolkit.strategy.LearningStrategy;
import edu.byu.cstaheli.cs478.toolkit.utility.Matrix;

import static edu.byu.cstaheli.cs478.toolkit.utility.Utility.square;

/**
 * Created by cstaheli on 3/14/2017.
 */
public class NearestNeighbor extends SupervisedLearner
{
    private int numberOfNeighborsToCompareTo;
    private Matrix trainingFeatures;
    private Matrix trainingLabels;
    
    public NearestNeighbor()
    {
        this.numberOfNeighborsToCompareTo = 0;
    }
    
    @Override
    public void train(LearningStrategy strategy) throws Exception
    {
        trainingFeatures = strategy.getTrainingFeatures();
        trainingLabels = strategy.getTrainingLabels();
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
            distance += square(x[i] - y[i]);
        }
        return Math.sqrt(distance);
    }
    
    public int getNumberOfNeighborsToCompareTo()
    {
        return numberOfNeighborsToCompareTo;
    }
    
    public void setNumberOfNeighborsToCompareTo(int numberOfNeighborsToCompareTo)
    {
        this.numberOfNeighborsToCompareTo = numberOfNeighborsToCompareTo;
    }
    
    public Matrix getTrainingFeatures()
    {
        return trainingFeatures;
    }
    
    public void setTrainingFeatures(Matrix trainingFeatures)
    {
        this.trainingFeatures = trainingFeatures;
    }
    
    public Matrix getTrainingLabels()
    {
        return trainingLabels;
    }
    
    public void setTrainingLabels(Matrix trainingLabels)
    {
        this.trainingLabels = trainingLabels;
    }
}
