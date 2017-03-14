package edu.byu.cstaheli.cs478.toolkit.strategy;

import edu.byu.cstaheli.cs478.toolkit.learner.LearnerData;
import edu.byu.cstaheli.cs478.toolkit.utility.Matrix;

/**
 * Created by cstaheli on 1/20/2017.
 */
public abstract class LearningStrategy
{
    private final static double TRAINING_PERCENT = .8;
    private LearnerData learnerData;

    public LearningStrategy(LearnerData learnerData) throws Exception
    {
        this.learnerData = learnerData;
    }

    public abstract Matrix getTrainingData();

    public Matrix getTrainingFeatures()
    {
        return getFeaturesFromMatrix(getTrainingData());
    }

    public Matrix getTrainingLabels()
    {
        return getLabelsFromMatrix(getTrainingData());
    }

    public abstract Matrix getTestingData();

    public Matrix getTestingFeatures()
    {
        return getFeaturesFromMatrix(getTestingData());
    }

    public Matrix getTestingLabels()
    {
        return getLabelsFromMatrix(getTestingData());
    }

    public abstract Matrix getValidationData();

    public Matrix getValidationFeatures()
    {
        return getFeaturesFromMatrix(getValidationData());
    }

    public Matrix getValidationLabels()
    {
        return getLabelsFromMatrix(getValidationData());
    }

    private Matrix getFeaturesFromMatrix(Matrix data)
    {
        return new Matrix(data, 0, 0, data.rows(), data.cols() - 1);
    }

    private Matrix getLabelsFromMatrix(Matrix data)
    {
        return new Matrix(data, 0, data.cols() - 1, data.rows(), 1);
    }

    Matrix getArffData()
    {
        return learnerData.getArffData();
    }

    protected double getTrainPercent()
    {
        return TRAINING_PERCENT;
    }

    protected int getTrainSize()
    {
        return getArffData().rows();
    }

    protected int getTrainingSetSize()
    {
        return (int) (getTrainSize() * getTrainPercent());
    }

    protected int getValidationSetSize()
    {
        return (int) (getTrainSize() * (1 - getTrainPercent()));
    }
}
