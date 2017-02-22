package hu.university.datamining;


import hu.university.utilities.Matrix;
import hu.university.utilities.Utils;

import java.util.*;

public class NaiveBayes
{
    private final Corpus corpus;
    private final int numDocs;
    private final int numTopics;
    private final int numWords;
    private HashMap<String, Integer> authorIds;
    private int[] Y;
    private Matrix termTopicMatrix;
    private double[] topicModel;
    public NaiveBayes(Corpus corpus)
    {
        this.authorIds = new HashMap<>();
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
        for (int i = 0; i < numTopics; i++)
        {
            authorIds.put(Authors[i], i);
        }
        this.termTopicMatrix = new Matrix(numWords, numTopics);
        this.topicModel = new double[numTopics];
    }

    public void Train(int numberOfTrainingArticlesPerTopic)
    {
        List<Article> trainingSet = createTrainingSet(numberOfTrainingArticlesPerTopic);

        //Initialize model from training set in a semi-supervised fashion.
        buildModel(trainingSet);
        labelDocuments(trainingSet);
    }

    private List<Article> createTrainingSet(int numberOfArticlePerTopic)
    {
        List<Article> trainingSet = new ArrayList<>();
        for(int i = 0; i < numTopics; i++)
        {
            final int j = i;
            Object[] temp = corpus.GetArticles().stream()
                .filter(a -> authorIds.get(a.Author) == j)
                .toArray();
            Article[] articles = Arrays.copyOf(temp,temp.length,Article[].class);
            if(articles.length <= numberOfArticlePerTopic)
                trainingSet.addAll(Arrays.asList(articles));
            else
            {
                trainingSet.addAll(Utils.pickNRandom(Arrays.asList(articles), numberOfArticlePerTopic));
            }
        }
        return trainingSet;
    }

    public Matrix GetTermTopicMatrix()
 {
  return termTopicMatrix;
 }

    public Map<String,Integer> GetAuthorMapping()
 {
  return authorIds;
 }

    public int[] GetDocumentToAuthorAllocation()
 {
  return Y;
 }

    public List<Article> GetArticles()
 {
  return corpus.GetArticles();
 }

    private void labelDocuments(List<Article> trainingSet)
    {
        for(int i = 0; i < numDocs; i++)
        {
            //If labeled document use its label
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

                for(String word : a.TextAsWords)
                {
                    int n = corpus.GetWordIndex(word);
                    posteriorProbability[j] *= termTopicMatrix.GetValue(n, j);
                }
            }

            //Label unlabeled documents with the topic with the highest probability
            Y[i] = 0;
            for(int j = 1; j < numTopics; j++)
            {
                if(posteriorProbability[Y[i]] < posteriorProbability[j])
                    Y[i] = j;
            }
        }
    }

    private void buildModel(List<Article> trainingSet)
    {
        for(int j = 0; j < numTopics; j++)
        {
            final int authorId = j;
            //articles in the jth topic
            Object[] temp = trainingSet.stream()
                .filter(a -> authorIds.get(a.Author) == authorId)
                .toArray();
            Article[] currentArticles = Arrays.copyOf(temp,temp.length,Article[].class);

            // termTopic[i,j] == (1 + sum(wordOccurrence in documents that are in the jth topic))/(numWords + sum(word occurrence for each word in the documents that are in the jth topic)
            double termTopicDenominator = numWords;
            double[] termTopicReckoner = new double[numWords];

            for(Article a : currentArticles)
            {
                for(String word : a.TextAsWords)
                {
                    int wordIndex = corpus.GetWordIndex(word);
                    termTopicDenominator++;
                    termTopicReckoner[wordIndex]++;
                }
            }

            for(int t = 0; t < numWords; t++)
            {
                termTopicMatrix.SetValue(t,j,(termTopicReckoner[t] + 1)/termTopicDenominator);
            }
            topicModel[j] = (1.0 + currentArticles.length)/(numTopics + trainingSet.size());
        }
    }
}
