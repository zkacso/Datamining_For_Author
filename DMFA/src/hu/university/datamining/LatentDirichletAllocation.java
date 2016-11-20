package hu.university.datamining;

import blogspot.software_and_algorithms.stern_library.optimization.HungarianAlgorithm;

import java.util.*;

/**
 Created by Zoltán on 2016. 09. 28.. */
public class LatentDirichletAllocation
{
    private Corpus corpus;
    private int numTopics;
    private int numDocs;
    private HashMap<String, int[]> wordTopicCount;
    private List<int[]> docWordToTopic;
    private int[][] docToTopicCount;
    private boolean initializing;
    private double alpha;
    private double lambda;


    public List<Article> getArticles()
    {
        return corpus.GetArticles();
    }

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

    //Latent dirichlet algorithm using gibbl sampling.
    public void Train(int numOfIterations)
    {
        //Initialize topicword allocation
        initializeDocWordToTopicMapping();
        int currIteration = 0;
        int currDocumentIndex = 0;
        while(currIteration < numOfIterations)
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
            currIteration += numWordsInDocument;
        }
    }

    private int generateNewTopic(int currDocumentIndex, int wordIndex)
    {
        double[] wordTopicProportions = calculateWordToTopicProportions(currDocumentIndex,wordIndex);
        double sumOfWordTopicProportions = 0;
        for(double p: wordTopicProportions)
            sumOfWordTopicProportions += p;
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

    private double[] calculateWordToTopicProportions(int documentIndex, int wordIndex)
    {
        double[] wordTopicProportions = new double[numTopics];
        String word = corpus.GetWordOfArticle(documentIndex, wordIndex);
        for(int topicIdx = 0; topicIdx < numTopics; topicIdx++)
        {
            double docTopic = documentToTopicProbability(documentIndex,topicIdx);

            double wordTopic = (wordTopicCount.get(word)[topicIdx] + lambda);
            double sum = 0;
            for(int k = 0;k < numTopics; k++)
            {
                sum+= wordTopicCount.get(word)[topicIdx];
            }
            sum += numTopics * lambda;
            wordTopic /= sum;

            wordTopicProportions[topicIdx] = docTopic * wordTopic;
        }
        return wordTopicProportions;
    }

    private double documentToTopicProbability(int documentIndex, int topicIdx)
    {
        return (docToTopicCount[documentIndex][topicIdx] + alpha)
            / (corpus.GetArticleLength(documentIndex) + alpha*numTopics);
    }

    public double[] documentToTopicProbabilities(int documentIndex)
    {
        double[] probabilities = new double[numTopics];
        for(int topicIdx = 0; topicIdx < numTopics; topicIdx++)
            probabilities[topicIdx] = documentToTopicProbability(documentIndex,topicIdx);
        return probabilities;
    }

    private void initializeDocWordToTopicMapping()
    {
        Random r = new Random();

        //Initialize corpus with random topic allocation
        initializing = true;
        for(int docIx = 0; docIx < numDocs; docIx++ )
        {
            int wordNum = docWordToTopic.get(docIx).length;
            for(int wordIx = 0; wordIx < wordNum; wordIx++)
            {
                int newTopicIdx = r.nextInt(numTopics);
                assignWordOfDocumentToTopic(docIx, wordIx, newTopicIdx);
            }
        }
        initializing = false;
    }

    private void assignWordOfDocumentToTopic(int documentIdx, int wordIdx, int newTopicIdx)
    {
        String docWord = corpus.GetWordOfArticle(documentIdx, wordIdx);
        int[] docWords = docWordToTopic.get(documentIdx);
        int oldTopicIdx = docWords[wordIdx];
        docWords[wordIdx] = newTopicIdx;
        int[] wordTopicCount = this.wordTopicCount.get(docWord);
        //Decrease old topic count, except when initializing.
        if(!initializing && oldTopicIdx != -1)
        {
            wordTopicCount[oldTopicIdx]--;
            docToTopicCount[documentIdx][oldTopicIdx]--;
            if(wordTopicCount[oldTopicIdx] < 0 || docToTopicCount[documentIdx][oldTopicIdx] < 0)
                throw new RuntimeException("Something wrong");
        }
        //Increase new topic count.
        wordTopicCount[newTopicIdx]++;
        docToTopicCount[documentIdx][newTopicIdx]++;
    }

    private void unassignWordOfDocument(int documentIdx, int wordIdx)
    {
        int[] docWords = docWordToTopic.get(documentIdx);
        int oldTopicIdx = docWords[wordIdx];
        docWords[wordIdx] = -1;
        //Decrease old topic count, except when initializing.
        if(oldTopicIdx == -1)
        {
            String docword = corpus.GetWordOfArticle(documentIdx, wordIdx);
            int[] wordTopicCount = this.wordTopicCount.get(docword);
            wordTopicCount[oldTopicIdx]--;
            docToTopicCount[documentIdx][oldTopicIdx]--;
            if(wordTopicCount[oldTopicIdx] < 0 || docToTopicCount[documentIdx][oldTopicIdx] < 0)
                throw new RuntimeException("Something wrong");
        }
    }

    public int numDocs()
    {
        return numDocs;
    }

    public int[] getDocumentToTopicIndexes()
    {
        int[] docToTopicIx = new int[numDocs];
        for(int docIdx = 0; docIdx < numDocs; docIdx++)
        {
            ArrayList<Integer> topicIndexes = new ArrayList<>();
            for(int t = 0; t < numTopics; t++)
                topicIndexes.add(t);
            final double[] docToTopic = documentToTopicProbabilities(docIdx);
            topicIndexes.sort((t1, t2) -> Double.compare(docToTopic[t2],docToTopic[t1]));
            docToTopicIx[docIdx] = topicIndexes.get(0);
        }
        return docToTopicIx;
    }

    public String[] getDocumentToAuthorMapping()
    {

        int[] topicToAuthorIx = getTopicToAuthorIxMapping();
        ArrayList<String> authors = new ArrayList<>(corpus.GetAuthors());
        int[] docToTopicIndexes = getDocumentToTopicIndexes();

        String[] documentToAuthorAllocation = new String[numDocs];
        for(int docIdx = 0; docIdx < numDocs; docIdx++)
        {
            documentToAuthorAllocation[docIdx] = authors.get(topicToAuthorIx[docToTopicIndexes[docIdx]]);
        }
        return documentToAuthorAllocation;
    }

    public int[] getTopicToAuthorIxMapping()
    {
        int[] docToTopicIndexes = getDocumentToTopicIndexes();
        double[][] costMatrix = new double[numTopics][numTopics];
        ArrayList<String> authors = new ArrayList<>(corpus.GetAuthors());
        double maxVal = 0;

        // Count the authors in each created topic
        for(int docIx = 0; docIx < numDocs; docIx++)
        {
            Article article = corpus.GetArticle(docIx);
            costMatrix[docToTopicIndexes[docIx] ][authors.indexOf(article.Author)]++;
            if(maxVal < costMatrix[docToTopicIndexes[docIx] ][authors.indexOf(article.Author)])
                maxVal = costMatrix[docToTopicIndexes[docIx] ][authors.indexOf(article.Author)];
        }

        // Hungarian algorithm calculates the minimal allocation, but we need the maximal so substract each value from max value
        for(int i = 0; i < numTopics; i++)
            for(int j = 0; j < numTopics; j++)
                costMatrix[i][j] = maxVal - costMatrix[i][j];

        HungarianAlgorithm alg = new HungarianAlgorithm(costMatrix);
        return alg.execute();
    }
}
