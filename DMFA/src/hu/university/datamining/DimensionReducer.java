package hu.university.datamining;

import java.util.List;

/**
 Created by Zoltán on 2016. 10. 24.. */
public abstract class DimensionReducer
{
    public abstract String[] filterStopWords(String[] words);
    public abstract List<Article> reduceWordDimension(List<Article> articles);

    public static DimensionReducer Default = DefaultDimensionReducer.Instance;
}
