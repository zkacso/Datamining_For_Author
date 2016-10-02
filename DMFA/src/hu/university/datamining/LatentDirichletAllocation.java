package hu.university.datamining;

import org.apache.commons.math3.distribution.GammaDistribution;

/**
 Created by Zoltán on 2016. 09. 28.. */
public class LatentDirichletAllocation
{
    private Corpus corpus;
    private long numTopics;
    private int numDocs;


    public LatentDirichletAllocation(Corpus corpus)
    {
        this.numDocs = corpus.NumberOfArticles();
        this.numTopics = corpus.GetArticles().stream().map(a -> a.Author).distinct().count();
        this.corpus = corpus;

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
