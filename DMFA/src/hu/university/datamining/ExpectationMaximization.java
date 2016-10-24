package hu.university.datamining;

import hu.university.utilities.Matrix;
import hu.university.utilities.Utils;

import java.util.*;

/**
 Created by Zoltán on 2016. 10. 16.. */
public class ExpectationMaximization
{
    private final Corpus corpus;
    private final int numDocs;
    private final int numTopics;
    private final int numWords;
    private HashMap<String, Integer> authorIds;
    private int[] Y;
    private Matrix termTopicMatrix;
    private double[] topicModel;
    private final double alpha;

    public ExpectationMaximization(Corpus corpus, double alpha)
    {
        this.authorIds = new HashMap<String, Integer>();
        this.corpus = corpus;
        this.numDocs = corpus.NumberOfArticles();
        this.Y = new int[numDocs];
        Object[] temp = corpus.GetArticles().stream()
                .map(a -> a.Author)
                .distinct()
                .toArray();
        String[] Authors = Arrays.copyOf(temp, temp.length, String[].class);
        this.numTopics = Authors.length;
        this.numWords = corpus.NumberOfWords();
        for(int i = 0; i < numTopics; i++)
        {
            authorIds.put(Authors[i],i);
        }
        this.termTopicMatrix = new Matrix(numWords, numTopics);
        this.topicModel = new double[numTopics];
        this.alpha = alpha;
    }

    public void Train(int numberOfTrainingArticlesPerTopic)
    {
        List<Article> trainingSet =
                new ArrayList<>();

        System.out.println("Training set creation: " + System.currentTimeMillis());
        for(int i = 0; i < numTopics; i++)
        {
            final int j = i; // fuck java
            Object[] temp = corpus.GetArticles().stream()
                    .filter(a -> authorIds.get(a.Author) == j)
                    .toArray();
            Article[] articles = Arrays.copyOf(temp,temp.length,Article[].class);
            if(articles.length <= numberOfTrainingArticlesPerTopic)
                trainingSet.addAll(Arrays.asList(articles));
            else
            {
                trainingSet.addAll(Utils.pickNRandom(Arrays.asList(articles), numberOfTrainingArticlesPerTopic));
            }
        }
        System.out.println("Training set created: " + System.currentTimeMillis());

        //Initialize model from training set in a semi-supervised fashion.
        maximization(trainingSet);
        expectationMaximization(trainingSet);

    }

    private void expectationMaximization(List<Article> trainingSet)
    {
        double logProbability = calculateLogProbability(trainingSet);
        System.out.println(logProbability);
        while (true)
        {
            expectation(trainingSet);
            maximization(corpus.GetArticles());
            double newLogProbability = calculateLogProbability(trainingSet);
            System.out.println(newLogProbability);
            double diff = newLogProbability - logProbability;
            System.out.println("Log prob difference: " + diff);
            if(Math.abs(diff) < 0.00001)
            {
                break;
            }
            logProbability = newLogProbability;
        }
    }

    private double calculateLogProbability(List<Article> trainingSet)
    {

        System.out.println("Log calculation started: " + System.currentTimeMillis());
        double modelProbability = 1.0;
        for(int j = 0; j < numTopics; j++)
        {
            for(int t = 0; t < numWords; t++)
            {
                modelProbability *= Math.pow(termTopicMatrix.GetValue(t,j),alpha-1);
            }
        }
        System.out.println("Model probability: " + modelProbability);
        System.out.println("Log model probability: " + Math.log(modelProbability));

        double unlabeledLogProbability = 0.0;
        double labeledLogProbability = 0.0;
        for(int i = 0; i < numDocs; i++)
        {
            Article a = corpus.GetArticle(i);
            if(trainingSet.contains(a))
            {
                double prob = 1.0;
                int topicId = authorIds.get(a.Author);
                prob *= topicModel[topicId];
                for(int t = 0; t < numWords; t++)
                {
                    prob *= Utils.pow(termTopicMatrix.GetValue(t, topicId),corpus.GetWordOccurrenceInDocument(t,i));
                }
                System.out.println("Labeled probability: " + prob);
                labeledLogProbability += Math.log(prob);
            }
            else
            {
                double prob = 0.0;
                for(int j = 0; j < numTopics; j++)
                {
                    double prob2 = 1.0;
                    prob2 *= topicModel[j];
                    for(int t = 0; t < numWords; t++)
                    {
                        System.out.println(prob2);
                        prob2 *= Utils.pow(termTopicMatrix.GetValue(t, j),corpus.GetWordOccurrenceInDocument(t,i));
                    }
                    prob += prob2;
                }
                System.out.println("Unlabeled probability: " + prob);
                unlabeledLogProbability += Math.log(prob);
            }
        }
        System.out.println("Log calculation ended: " + System.currentTimeMillis());
        return Math.log(modelProbability) + labeledLogProbability + unlabeledLogProbability;
    }

    private void expectation(List<Article> trainingSet)
    {
        System.out.println("Expectation started: " + System.currentTimeMillis());
        for(int i = 0; i < numDocs; i++)
        {
            Article a = corpus.GetArticle(i);
            //Labeled document
            if(trainingSet.contains(a))
            {
                Y[i] = authorIds.get(a.Author);
                continue;
            }

            //A not labeled document
            double[] posteriorProbability = new double[numTopics];
            for(int j = 0; j < numTopics; j++)
            {
                posteriorProbability[j] = topicModel[j];
                for(int n = 0; n < numWords; n++)
                    posteriorProbability[j] *= Utils.pow(termTopicMatrix.GetValue(n,j),corpus.GetWordOccurrenceInDocument(n, i));
            }

            //Label unlabeled documents with the topic with the highest probability
            Y[i] = 0;
            for(int j = 1; j < numTopics; j++)
            {
                if(posteriorProbability[Y[i]] < posteriorProbability[j])
                    Y[i] = j;
            }
        }
        System.out.println("Expectation ended: " + System.currentTimeMillis());
    }

    private void maximization(List<Article> articles)
    {
        System.out.println("Maximization start: " + System.currentTimeMillis());
        for(int j = 0; j < numTopics; j++)
        {
            final int authorId = j;
            //articles in the jth topic
            Object[] temp = articles.stream()
                    .filter(a -> authorIds.get(a.Author) == authorId)
                    .toArray();
            Article[] currentArticles = Arrays.copyOf(temp,temp.length,Article[].class);

            // termTopic[i,j] == (1 + sum(wordOccurence in documents that are in the jth topic))/(numWords + sum(word occurence for each word in the documents that are in the jth topic)
            double termTopicDenominator = numWords;
            double[] termTopicReckoner = new double[numWords];
            for(int s = 0; s < numWords; s++)
            {
                termTopicReckoner[s] = 1;
                for(Article a : currentArticles)
                {
                    int articleIndex = corpus.GetArticleIndex(a);
                    int wordOccurrence = corpus.GetWordOccurrenceInDocument(s, articleIndex);
                    termTopicDenominator+= wordOccurrence;
                    termTopicReckoner[s] += wordOccurrence;
                }
            }
            for(int t = 0; t < numWords; t++)
            {
                termTopicMatrix.SetValue(t,j,termTopicReckoner[t]/termTopicDenominator);
            }
            topicModel[j] = (1.0 + currentArticles.length)/(numTopics + articles.size());
        }
        System.out.println("Maximization ended: " + System.currentTimeMillis());
    }
}
