package hu.university;

import hu.university.datamining.*;
import hu.university.io.ExpectationMaximizationIO;
import hu.university.utilities.Stopper;

import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception
    {
        Corpus corpus = new Corpus("C:\\GitRepository\\onlab\\onlab_datamining\\Bytest213.csv",
                new SnowballStemmer(),
                new MyDimensionReducer());

        LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus,2.0,2.0);
        LDA.Train2(100000);

        ExpectationMaximization em = new ExpectationMaximization(corpus,1,10000);
        em.Train(2);

        File termtopic = new File("termtopic1000.txt");
        ExpectationMaximizationIO.SaveTermTopicMatrix(em, termtopic);
        File allocations = new File("authorallocation1000.txt");
        ExpectationMaximizationIO.SaveTopicAllocationWithText(em, allocations);
    }
}
