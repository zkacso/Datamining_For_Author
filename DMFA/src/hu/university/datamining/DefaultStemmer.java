package hu.university.datamining;

/**
 Created by Zoltán on 2016. 10. 24.. */
public class DefaultStemmer implements Stemmer
{
 private DefaultStemmer(){}
 public final static DefaultStemmer Instance = new DefaultStemmer();


 @Override
 public String stem(String word)
 {
  return word;
 }
}
