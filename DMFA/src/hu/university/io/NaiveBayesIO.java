package hu.university.io;

import hu.university.datamining.Article;
import hu.university.datamining.ExpectationMaximization;
import hu.university.datamining.NaiveBayes;
import hu.university.utilities.Matrix;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 Created by Zoltán on 2016. 12. 01.. */
public class NaiveBayesIO
{
 public static void SaveTopicAllocationWithText(NaiveBayes nb,File output)
 {
  try
  {
   BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
   int[] docToAuthor = nb.GetDocumentToAuthorAllocation();
   Map<String,Integer> authorIds = nb.GetAuthorMapping();
   List<Article> articles = nb.GetArticles();
   for(int i = 0; i < articles.size(); i++)
   {
    Article a = articles.get(i);
    String selectedAuthor = getAuthor(authorIds, docToAuthor[i]);
    br.write("\""+a.Author + "\";\"" + selectedAuthor + "\"\n");
   }
   br.close();
  }
  catch (IOException e)
  {
   e.printStackTrace();
  }
 }
 private static String getAuthor(Map<String, Integer> authorIds, int selectedAuthorId)
 {
  return authorIds.keySet().stream().filter(author -> authorIds.get(author) == selectedAuthorId).findFirst().get();
 }
 public static void SaveTermTopicMatrix(NaiveBayes naiveBayes,File output)
 {
  Matrix m = naiveBayes.GetTermTopicMatrix();
  try
  {
   BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
   for(int i = 0; i < m.GetRowSize(); i++)
   {
    br.write("\"");
    for (int j = 0; j < m.GetColumnSize()-1; j++)
    {
     br.write(m.GetValue(i,j) + "\";\"");
    }
    br.write(m.GetValue(i,m.GetColumnSize()-1) + "\"");
    br.write('\n');
   }
   br.close();
  }
  catch (IOException e)
  {
   e.printStackTrace();
  }

 }
}
