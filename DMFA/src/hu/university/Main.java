package hu.university;

import hu.university.datamining.*;
import hu.university.utilities.Stopper;

public class Main {

    public static void main(String[] args) throws Exception
    {
        Corpus corpus = new Corpus("C:\\GitRepository\\onlab\\onlab_datamining\\Bytest213.csv",
                new SnowballStemmer(),
                new MyDimensionReducer());


        Stopper.instance.Start();
        LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus,2.0,2.0);
        LDA.Train2(100000);
        Stopper.instance.Tick("LDA finished");
        ExpectationMaximization em = new ExpectationMaximization(corpus,1,3000);

        System.out.println(System.currentTimeMillis());
        em.Train(1);
        System.out.println(System.currentTimeMillis());
    }
}
