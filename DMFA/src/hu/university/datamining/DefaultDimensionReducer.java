package hu.university.datamining;

import java.util.List;

/**
 Created by Zolt�n on 2016. 10. 24.. */
public class DefaultDimensionReducer implements DimensionReducer
{
 private DefaultDimensionReducer(){}
 public final static DefaultDimensionReducer Instance = new DefaultDimensionReducer();

 @Override
 public String[] filterStopWords(String[] words)
 {
  return words;
 }

 @Override
 public List<String> filterStopWords(List<String> words)
 {
  return words;
 }
}
