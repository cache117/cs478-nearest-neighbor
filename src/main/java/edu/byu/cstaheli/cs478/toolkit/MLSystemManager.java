package edu.byu.cstaheli.cs478.toolkit;
// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------


import edu.byu.cstaheli.cs478.backpropogation.BackPropagation;
import edu.byu.cstaheli.cs478.baseline.BaselineLearner;
import edu.byu.cstaheli.cs478.decision_tree.DecisionTree;
import edu.byu.cstaheli.cs478.nearest_neighbor.NearestNeighbor;
import edu.byu.cstaheli.cs478.perceptron.Perceptron;
import edu.byu.cstaheli.cs478.toolkit.learner.EpochLearner;
import edu.byu.cstaheli.cs478.toolkit.learner.LearnerData;
import edu.byu.cstaheli.cs478.toolkit.learner.SupervisedLearner;
import edu.byu.cstaheli.cs478.toolkit.strategy.*;
import edu.byu.cstaheli.cs478.toolkit.utility.ArgParser;
import edu.byu.cstaheli.cs478.toolkit.utility.Matrix;

import java.util.Random;


public class MLSystemManager
{
    private Random random;
    private SupervisedLearner learner;
    private boolean binRealData;

    public MLSystemManager()
    {
        setRandom(new Random());
    }

    public static void main(String[] args) throws Exception
    {
        MLSystemManager ml = new MLSystemManager();
        ml.run(args);
    }

    public void run(String[] args) throws Exception
    {
        //Parse the command line arguments
        ArgParser parser = new ArgParser(args);
        determineEvalMethod(parser);
    }

    private void determineEvalMethod(ArgParser parser) throws Exception
    {
        // Load the model
        if (learner == null)
        {
            learner = getLearner(parser.getLearner(), getRandom());
        }

        // Load the ARFF file
        Matrix arffData = new Matrix();
        if (binRealData)
        {
            arffData.doBinRealValues();
        }
        arffData.loadArff(parser.getARFF());
        if (parser.isNormalized())
        {
            System.out.println("Using normalized data\n");
            arffData.normalize();
        }

        printStats(parser.getARFF(), parser.getLearner(), parser.getEvaluation(), arffData);

        LearnerData learnerData = new LearnerData(getRandom(), parser, arffData);
        switch (parser.getEvaluation())
        {
            case "training":
                calcTraining(learner, learnerData);
                break;
            case "static":
                calcStatic(learner, learnerData);
                break;
            case "random":
                calcRandom(learner, learnerData);
                break;
            case "cross":
                calcCrossValidation(learner, learnerData);
                break;
        }
    }
    
    /**
     * When you make a new learning algorithm, you should add a line for it to this method.
     */
    public SupervisedLearner getLearner(String model, Random rand) throws Exception
    {
        switch (model)
        {
            case "baseline":
                return new BaselineLearner();
            case "perceptron":
                return new Perceptron(rand);
            case "backpropagation":
                return new BackPropagation(rand);
            case "decisiontree":
                return new DecisionTree();
            case "knn":
                return new NearestNeighbor();
            default:
                throw new Exception("Unrecognized model: " + model);
        }
    }

    private void calcTraining(SupervisedLearner learner, LearnerData learnerData) throws Exception
    {
        System.out.println("Calculating accuracy on training set...");
        LearningStrategy strategy = new TrainingStrategy(learnerData);
        Matrix confusion = new Matrix();
        double startTime = System.currentTimeMillis();
        learner.train(strategy);
        double elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
        double accuracy = learner.measureAccuracy(strategy.getTrainingFeatures(), strategy.getTrainingLabels(), confusion);
        System.out.println("Training set accuracy: " + accuracy);
        if (learnerData.isVerbose())
        {
            System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
            confusion.print();
            System.out.println("\n");
        }
        if (learner instanceof EpochLearner)
        {
            System.out.println("Total number of epochs: " + ((EpochLearner) learner).getTotalEpochs());
        }
    }

    private void calcStatic(SupervisedLearner learner, LearnerData learnerData) throws Exception
    {
        LearningStrategy strategy = new StaticStrategy(learnerData);
        double startTime = System.currentTimeMillis();
        learner.train(strategy);
        double elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
        double trainAccuracy = learner.measureAccuracy(strategy.getTrainingFeatures(), strategy.getTrainingLabels(), null);
        System.out.println("Training set accuracy: " + trainAccuracy);
        Matrix testFeatures = strategy.getTestingFeatures();
        Matrix testLabels = strategy.getTestingLabels();
        Matrix confusion = new Matrix();
        double testAccuracy = learner.measureAccuracy(testFeatures, testLabels, confusion);
        System.out.println("Test set accuracy: " + testAccuracy);
        if (learnerData.isVerbose())
        {
            System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
            confusion.print();
            System.out.println("\n");
        }
        if (learner instanceof Perceptron)
        {
            ((Perceptron) learner).writeAccuraciesAndFinalWeights(trainAccuracy, testAccuracy);
        }
        if (learner instanceof EpochLearner)
        {
            System.out.println("Total number of epochs: " + ((EpochLearner) learner).getTotalEpochs());
        }
    }

    private void calcRandom(SupervisedLearner learner, LearnerData learnerData) throws Exception
    {
        LearningStrategy strategy = new RandomStrategy(learnerData);
        Matrix testFeatures = strategy.getTestingFeatures();
        Matrix testLabels = strategy.getTestingLabels();
        double startTime = System.currentTimeMillis();
        learner.train(strategy);
        double elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Time to train (in seconds): " + elapsedTime / 1000.0);
        double trainAccuracy = learner.measureAccuracy(strategy.getTrainingFeatures(), strategy.getTrainingLabels(), null);
        System.out.println("Training set accuracy: " + trainAccuracy);
        Matrix confusion = new Matrix();
        double testAccuracy = learner.measureAccuracy(testFeatures, testLabels, confusion);
        System.out.println("Test set accuracy: " + testAccuracy);
        if (learnerData.isVerbose())
        {
            System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
            confusion.print();
            System.out.println("\n");
        }
        if (learner instanceof Perceptron)
        {
            ((Perceptron) learner).writeAccuraciesAndFinalWeights(trainAccuracy, testAccuracy);
        }
        if (learner instanceof EpochLearner)
        {
            System.out.println("Total number of epochs: " + ((EpochLearner) learner).getTotalEpochs());
        }
    }

    private void calcCrossValidation(SupervisedLearner learner, LearnerData learnerData) throws Exception
    {
        LearningStrategy strategy;
        System.out.println("Calculating accuracy using cross-validation...");
        int folds = Integer.parseInt(learnerData.getEvalParameter());
        if (folds <= 0)
            throw new Exception("Number of folds must be greater than 0");
        System.out.println("Number of folds: " + folds);
        int reps = 1;
        double sumAccuracy = 0.0;
        double elapsedTime = 0.0;
        for (int j = 0; j < reps; j++)
        {
            learnerData.getArffData().shuffle(learnerData.getRandom());
            for (int i = 0; i < folds; i++)
            {
                int begin = i * learnerData.getArffData().rows() / folds;
                int end = (i + 1) * learnerData.getArffData().rows() / folds;
                strategy = new CrossValidationStrategy(learnerData, begin, end);
                Matrix testFeatures = strategy.getTestingFeatures();
                Matrix testLabels = strategy.getTestingLabels();
                double startTime = System.currentTimeMillis();
                learner.train(strategy);
                elapsedTime += System.currentTimeMillis() - startTime;
                Matrix confusion = new Matrix();
                double accuracy = learner.measureAccuracy(testFeatures, testLabels, confusion);
                sumAccuracy += accuracy;
                System.out.println("Rep=" + j + ", Fold=" + i + ", Accuracy=" + accuracy);
                if (learnerData.isVerbose())
                {
                    System.out.println("\nConfusion matrix: (Row=target value, Col=predicted value)");
                    confusion.print();
                    System.out.println("\n");
                }
            }
        }
        elapsedTime /= (reps * folds);
        System.out.println("Average time to train (in seconds): " + elapsedTime / 1000.0);
        System.out.println("Mean accuracy=" + (sumAccuracy / (reps * folds)));
        if (learner instanceof EpochLearner)
        {
            System.out.println("Total number of epochs: " + ((EpochLearner) learner).getTotalEpochs());
        }
    }

    private void printStats(String fileName, String learnerName, String evalMethod, Matrix data)
    {
        // Print some stats
        System.out.println();
        System.out.println("Dataset name: " + fileName);
        System.out.println("Number of instances: " + data.rows());
        System.out.println("Number of attributes: " + data.cols());
        System.out.println("Learning algorithm: " + learnerName);
        System.out.println("Evaluation method: " + evalMethod);
        System.out.println();
    }

    public Random getRandom()
    {
        return random;
    }

    private void setRandom(Random random)
    {
        this.random = random;
    }
    
    public SupervisedLearner getLearner()
    {
        return learner;
    }
    
    public void setLearner(SupervisedLearner learner)
    {
        this.learner = learner;
    }
    
    public void setRandomSeed(long seed)
    {
        setRandom(new Random(seed));
    }

    private String getArrayString(double[] array)
    {
        StringBuilder builder = new StringBuilder();
        for (double anArray : array)
        {
            builder.append(",").append(anArray);
        }
        return builder.toString();
    }

    public void binRealData(boolean value)
    {
        binRealData = value;
    }
}
