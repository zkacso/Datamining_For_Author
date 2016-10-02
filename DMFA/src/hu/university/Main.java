package hu.university;

import hu.university.datamining.Article;
import hu.university.datamining.Corpus;
import hu.university.datamining.LatentDirichletAllocation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception
    {
        Corpus corpus = new Corpus("C:\\GitRepository\\Datamining_For_Author\\test.txt");
        LatentDirichletAllocation LDA = new LatentDirichletAllocation(corpus);
        System.out.println(LDA.numTopics());
    }
}
