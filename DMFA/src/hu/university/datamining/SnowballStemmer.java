package hu.university.datamining;

import org.tartarus.snowball.ext.englishStemmer;

/**
 Created by Zoltán on 2016. 10. 24.. */
public class SnowballStemmer extends Stemmer
{
    private org.tartarus.snowball.SnowballStemmer stemmer;
    public SnowballStemmer() throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Class stemmerClass = Class.forName("org.tartarus.snowball.ext.englishStemmer");
        stemmer = (org.tartarus.snowball.SnowballStemmer)stemmerClass.newInstance();
    }
    @Override
    public String stem(String word)
    {
        stemmer.setCurrent(word);
        stemmer.stem();
        stemmer.stem();
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
