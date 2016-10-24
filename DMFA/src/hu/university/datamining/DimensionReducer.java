package hu.university.datamining;

import java.util.List;

/**
 Created by Zoltán on 2016. 10. 24.. */
public interface DimensionReducer
{
    String[] filterStopWords(String[] words);
    List<String> filterStopWords(List<String> words);
}
