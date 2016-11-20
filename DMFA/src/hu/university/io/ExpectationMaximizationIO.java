package hu.university.io;

import hu.university.datamining.Article;
import hu.university.datamining.ExpectationMaximization;
import hu.university.utilities.Matrix;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 Created by Zoltán on 2016. 10. 31.. */
public class ExpectationMaximizationIO
{
    public static void SaveTermTopicMatrix(ExpectationMaximization em,File output)
    {
        Matrix m = em.GetTermTopicMatrix();
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

    public static void SaveTopicAllocationWithText(ExpectationMaximization em,File output)
    {
        try
        {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            int[] docToAuthor = em.GetDocumentToAuthorAllocation();
            Map<String,Integer> authorIds = em.GetAuthorMapping();
            List<Article> articles = em.GetArticles();
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

    public static void SaveTopicAllocationWithIds(ExpectationMaximization em,File output)
    {
        try
        {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            int[] docToAuthor = em.GetDocumentToAuthorAllocation();
            Map<String,Integer> authorId = em.GetAuthorMapping();
            List<Article> articles = em.GetArticles();
            for(int i = 0; i < articles.size(); i++)
            {
                Article a = articles.get(i);
                int selectedAuthorId = docToAuthor[i];
                br.write("\"" + authorId.get(a.Author) + "\";\"" + selectedAuthorId + "\"\n");
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
 }
}
