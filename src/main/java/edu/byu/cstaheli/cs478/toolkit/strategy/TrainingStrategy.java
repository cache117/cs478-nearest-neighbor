package edu.byu.cstaheli.cs478.toolkit.strategy;

import edu.byu.cstaheli.cs478.toolkit.learner.LearnerData;
import edu.byu.cstaheli.cs478.toolkit.utility.Matrix;

/**
 * Created by cstaheli on 1/20/2017.
 */
public class TrainingStrategy extends LearningStrategy
{
    public TrainingStrategy(LearnerData learnerData) throws Exception
    {
        super(learnerData);
    }

    @Override
    public Matrix getTrainingData()
    {
        return new Matrix(getArffData(), 0, 0, getTrainingSetSize(), getArffData().cols());
    }

    @Override
    public Matrix getTestingData()
    {
        return new Matrix(getArffData(), 0, 0, getTrainSize(), getArffData().cols());
    }

    @Override
    public Matrix getTestingFeatures()
    {
        return getTrainingFeatures();
    }

    @Override
    public Matrix getTestingLabels()
    {
        return getTrainingLabels();
    }

    @Override
    public Matrix getValidationData()
    {
        return new Matrix(getArffData(), getTrainingSetSize(), 0, getValidationSetSize(), getArffData().cols());
    }
}
