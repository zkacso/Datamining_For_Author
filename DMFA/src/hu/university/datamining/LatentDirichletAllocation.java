package hu.university.datamining;

import java.util.*;

/**
 Created by Zoltán on 2016. 09. 28.. */
public class LatentDirichletAllocation
{
    private Corpus corpus;
    private int numTopics;
    private int numDocs;
    private int numOfWords;
    private HashMap<String, int[]> wordTopicCount;
    private List<int[]> docWordToTopic;
    private int[][] docToTopicCount;
    private boolean initializing;
    private double alpha;
    private double lambda;


    //public LatentDirichletAllocation(Corpus corpus, double[] alphas, double[] lambdas)
    //{
        //init(corpus, alphas, lambdas);
    //}

    public LatentDirichletAllocation(Corpus corpus, double alpha, double lambda)
    {
        init(corpus, alpha, lambda);
    }

    private void init(Corpus corpus, double alphas, double lambdas)
    {
        //if(alphas.length != numDocs || lambdas.length != numTopics)
        //    throw new RuntimeException("Wrong amount of parameters in alpha or lambda");

        this.corpus = corpus;
        this.numDocs = corpus.NumberOfArticles();
        this.numTopics = (int)corpus.GetArticles().stream().map(a -> a.Author).distinct().count();

        this.wordTopicCount = new HashMap<>();
        this.docWordToTopic = new ArrayList<>();
        this.docToTopicCount = new int[numDocs][numTopics];
        this.alpha = alphas;
        this.lambda = lambdas;

        Set<String> words = corpus.GetWordSet();
        this.numOfWords = words.size();

        for(String word : words)
        {
            wordTopicCount.put(word, new int[numTopics]);
        }

        for(int i = 0; i < numDocs; i++)
        {
            int numberOfWords = corpus.GetArticle(i).TextAsWords.length;
            docWordToTopic.add(new int[numberOfWords]);
        }
    }

    //private void init(Corpus corpus, double alpha, double lambda)
    //{
    //    double[] alphas = new double[numDocs];
    //    double[] lambdas = new double[numTopics];
//
//        for(int i = 0; i < numDocs; i++)
//        {
//            alphas[i] = alpha;
//        }
//        for(int i = 0; i < numTopics; i++)
//        {
//            lambdas[i] = lambda;
//        }
//
//        init(corpus, alphas, lambdas);
//    }

    //Latent dirichlet algorithm using gibbl sampling.
    public void Train2(int numOfIterations)
    {
        //Initialize topicword allocation
        initializeDocWordToTopicMapping();
        int curriteration = 0;
        int currDocumentIndex = 0;
        while(curriteration < numOfIterations)
        {
            if(currDocumentIndex == numDocs)
                currDocumentIndex = 0;

            int numWordsInDocument = docWordToTopic.get(currDocumentIndex).length;
            for(int wordIndex = 0; wordIndex < numWordsInDocument; wordIndex++)
            {
                unassignWordOfDocument(currDocumentIndex, wordIndex);
                int newTopicIndex = generateNewTopic(currDocumentIndex, wordIndex);
                assignWordOfDocumentToTopic(currDocumentIndex, wordIndex, newTopicIndex);
            }
            currDocumentIndex++;
            curriteration += numWordsInDocument;
        }
    }

    private int generateNewTopic(int currDocumentIndex, int wordIndex)
    {
        double[] wordTopicProportions = new double[numTopics];
        double sumOfWordTopicProportions = 0;
        String word = corpus.GetWordOfArticle(currDocumentIndex, wordIndex);
        for(int topicIdx = 0; topicIdx < numTopics; topicIdx++)
        {
            double docTopic = (docToTopicCount[currDocumentIndex][topicIdx] + alpha)
                    / (corpus.GetArticleLength(currDocumentIndex) + alpha*numTopics);

            double wordTopic = (wordTopicCount.get(word)[topicIdx] + lambda);
            double sum = 0;
            for(int k = 0;k < numTopics; k++)
            {
                sum+= wordTopicCount.get(word)[topicIdx];
            }
            sum += numTopics * lambda;
            wordTopic /= sum;

            wordTopicProportions[topicIdx] = docTopic * wordTopic;
            sumOfWordTopicProportions += wordTopicProportions[topicIdx];
        }
        Random r = new Random();
        double random = r.nextDouble() * sumOfWordTopicProportions;
        for(int i = 0; i < numTopics; i++)
        {
            if(wordTopicProportions[i] > random)
                return i;
            else
                random -= wordTopicProportions[i];
        }
        return numTopics - 1;
    }

    private void initializeDocWordToTopicMapping()
    {
        Random r = new Random();

        //Initialize corpus with random topic allocation
        initializing = true;
        for(int docix = 0; docix < numDocs; docix++ )
        {
            int wordNum = docWordToTopic.get(docix).length;
            for(int wordix = 0; wordix < wordNum; wordix++)
            {
                int newTopicIdx = r.nextInt(numTopics);
                assignWordOfDocumentToTopic(docix, wordix, newTopicIdx);
            }
        }
        initializing = false;
    }

    private void assignWordOfDocumentToTopic(int documentIdx, int wordIdx, int newTopicIdx)
    {
        String docword = corpus.GetWordOfArticle(documentIdx, wordIdx);
        int[] docwords = docWordToTopic.get(documentIdx);
        int oldtopicIdx = docwords[wordIdx];
        docwords[wordIdx] = newTopicIdx;
        int[] wordTopicCount = this.wordTopicCount.get(docword);
        //Decrease old topic count, except when initializing.
        if(!initializing && oldtopicIdx != -1)
        {
            wordTopicCount[oldtopicIdx]--;
            docToTopicCount[documentIdx][oldtopicIdx]--;
            if(wordTopicCount[oldtopicIdx] < 0 || docToTopicCount[documentIdx][oldtopicIdx] < 0)
                throw new RuntimeException("Something wrong");
        }
        //Increase new topic count.
        wordTopicCount[newTopicIdx]++;
        docToTopicCount[documentIdx][newTopicIdx]++;
    }

    private void unassignWordOfDocument(int documentIdx, int wordIdx)
    {
        int[] docwords = docWordToTopic.get(documentIdx);
        int oldtopicIdx = docwords[wordIdx];
        docwords[wordIdx] = -1;
        //Decrease old topic count, except when initializing.
        if(oldtopicIdx == -1)
        {
            String docword = corpus.GetWordOfArticle(documentIdx, wordIdx);
            int[] wordTopicCount = this.wordTopicCount.get(docword);
            wordTopicCount[oldtopicIdx]--;
            docToTopicCount[documentIdx][oldtopicIdx]--;
            if(wordTopicCount[oldtopicIdx] < 0 || docToTopicCount[documentIdx][oldtopicIdx] < 0)
                throw new RuntimeException("Something wrong");
        }
    }


    public int numDocs()
    {
        return numDocs;
    }

    public long numTopics()
    {
        return numTopics;
    }
}
