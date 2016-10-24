package hu.university;

import hu.university.datamining.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception
    {
        Corpus corpus = new Corpus("C:\\GitRepository\\onlab\\onlab_datamining\\Bytest213.csv",
                new SnowballStemmer(),
                DefaultDimensionReducer.Instance);

        //LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus,2.0,2.0);
        //LDA.Train2(30000);
        //System.out.println(LDA.numTopics());
        System.out.println(Double.MIN_VALUE);
        ExpectationMaximization em = new ExpectationMaximization(corpus,2);

        System.out.println(System.currentTimeMillis());
        em.Train(1);
        System.out.println(System.currentTimeMillis());
    }
}
