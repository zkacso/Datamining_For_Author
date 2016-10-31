package hu.university.datamining;

import java.util.List;

/**
 Created by Zoltán on 2016. 10. 24.. */
public class DefaultDimensionReducer extends DimensionReducer
{
 private DefaultDimensionReducer(){}
 public final static DefaultDimensionReducer Instance = new DefaultDimensionReducer();

 @Override
 public String[] filterStopWords(String[] words)
 {
  return words;
 }

 @Override
 public List<Article> reduceWordDimension(List<Article> articles)
 {
  return articles;
 }

}
