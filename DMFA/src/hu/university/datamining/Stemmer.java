package hu.university.datamining;

/**
 Created by Zoltán on 2016. 10. 24.. */
public abstract class Stemmer
{
    public abstract String stem(String word);
    public static Stemmer Default = DefaultStemmer.Instance;
}
