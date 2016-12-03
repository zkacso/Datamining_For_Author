package hu.university;

import hu.university.datamining.*;
import hu.university.io.ExpectationMaximizationIO;
import hu.university.io.LatentDirichletAllocationIO;
import hu.university.io.NaiveBayesIO;

import java.io.File;

public class Main {
    //^(.*) \1$
    static Boolean useSemanticFields = false;
    static double leastFrequentWordCutPercentage = 0.4;
    static double mostFrequentWordCutPercentage = 0.01;
    static int MaxNumberOfWords = 6000;
    static int index = -1;
    static int sizeOfTrainingSet = 10;
    static final String outputfilePathBase = "C:\\GitRepository\\Datamining_For_Author\\results\\";
    static final String normalFilePath = "C:\\GitRepository\\Datamining_For_Author\\nytimes\\nytimesarticles.csv";
    static final String semanticFilePath = "C:\\GitRepository\\Datamining_For_Author\\semanticfields\\semanticArticles.csv";

    public static void main(String[] args) throws Exception
    {
        ProcessCommandLineArguments(args);
        if(index == -1)
        {
            System.out.println("Index number must be given. Use -ix");
            return;
        }

        String dataFilePath = useSemanticFields ? semanticFilePath : normalFilePath;

        Corpus corpus = new Corpus(dataFilePath,
                new SnowballStemmer(),
                new MyDimensionReducer(leastFrequentWordCutPercentage,
                                       mostFrequentWordCutPercentage,
                                       MaxNumberOfWords));

        /**
        Object[] asd = corpus.GetArticles().stream().map(a -> a.Author).distinct().toArray();
        String[] authors = Arrays.copyOf(asd,asd.length,String[].class);

        for(String author : authors)
        {
            long articleCount = corpus.GetArticles().stream().filter(a->a.Author.compareTo(author) == 0).count();
            System.out.println(author + " " + articleCount);
        }

        System.in.read();
        */

        LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus,2.0,2.0);
        LDA.Train(50000);

        ExpectationMaximization em = new ExpectationMaximization(corpus,10000);
        em.Train(sizeOfTrainingSet);

        NaiveBayes nb = new NaiveBayes(corpus);
        nb.Train(sizeOfTrainingSet);


        File termtopic = new File(getFilePath("em_termtopic"));
        ExpectationMaximizationIO.SaveTermTopicMatrix(em, termtopic);
        File allocations = new File(getFilePath("em_authorallocation"));
        ExpectationMaximizationIO.SaveTopicAllocationWithText(em, allocations);

        File nb_termtopic = new File(getFilePath("nb_termtopic"));
        NaiveBayesIO.SaveTermTopicMatrix(nb, nb_termtopic);
        File nb_allocations = new File(getFilePath("nb_authorallocation"));
        NaiveBayesIO.SaveTopicAllocationWithText(nb, nb_allocations);

        File ldaFile = new File(getFilePath("lda_authorallocation"));
        LatentDirichletAllocationIO.SaveToFile(LDA,ldaFile);
    }

    private static void ProcessCommandLineArguments(String[] args)
    {
        for(int i =0; i < args.length; i++)
        {
            String arg = args[i];
            if(arg.equals("-semanticFields"))
                useSemanticFields = true;
            else if(arg.equals("-lowCut"))
            {
                leastFrequentWordCutPercentage = Double.parseDouble(args[i + 1]);
                i++;
            }
            else if(arg.equals("-highCut"))
            {
                mostFrequentWordCutPercentage = Double.parseDouble(args[i + 1]);
                i++;
            }
            else if(arg.equals("-maxWords"))
            {
                MaxNumberOfWords = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if(arg.equals("-ix"))
            {
                index = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if(arg.equals("-trainingSetSize"))
            {
                sizeOfTrainingSet = Integer.parseInt(args[i+1]);
                i++;
            }
        }
    }

    private static String getFilePath(String theme)
    {

        return outputfilePathBase +
            theme + "_" +
            (useSemanticFields ? "sem" : "norm") + "_" +
            sizeOfTrainingSet + "_" +
            (int)(leastFrequentWordCutPercentage*100) + "_" +
            (int)(mostFrequentWordCutPercentage*100) + "_" +
            MaxNumberOfWords+ "_"+
            index + ".csv";
    }
}
