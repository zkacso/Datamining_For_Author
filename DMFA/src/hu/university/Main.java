package hu.university;

import hu.university.datamining.*;
import hu.university.io.ExpectationMaximizationIO;
import hu.university.utilities.Stopper;

import java.io.File;
import java.util.Arrays;

public class Main {
    //^(.*) \1$
    public static void main(String[] args) throws Exception
    {
        Corpus corpus = new Corpus("C:\\GitRepository\\Datamining_For_Author\\nytimes\\nytimesarticles.csv",
                new SnowballStemmer(),
                new MyDimensionReducer());

        Object[] asd = corpus.GetArticles().stream().map(a -> a.Author).distinct().toArray();
        String[] authors = Arrays.copyOf(asd,asd.length,String[].class);

        for(String author : authors)
        {
            long articleCount = corpus.GetArticles().stream().filter(a->a.Author.compareTo(author) == 0).count();
            System.out.println(author + " " + articleCount);
        }

        System.in.read();

        LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus,2.0,2.0);
        LDA.Train2(100000);

        ExpectationMaximization em = new ExpectationMaximization(corpus,1,10000);
        em.Train(10);

        File termtopic = new File("termtopic_5_10k.txt");
        ExpectationMaximizationIO.SaveTermTopicMatrix(em, termtopic);
        File allocations = new File("authorallocation_5_10k.txt");
        ExpectationMaximizationIO.SaveTopicAllocationWithText(em, allocations);
    }
}
