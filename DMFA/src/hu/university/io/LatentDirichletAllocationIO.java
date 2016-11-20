package hu.university.io;

import hu.university.datamining.Article;
import hu.university.datamining.LatentDirichletAllocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 Created by Zoltán on 2016. 11. 18.. */
public class LatentDirichletAllocationIO
{
    public static void SaveToFile(LatentDirichletAllocation lda, File output)
    {
        try
        {
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            List<Article> articles = lda.getArticles();
            String[] docToAuthor = lda.getDocumentToAuthorMapping();
            for(int docIdx = 0; docIdx<lda.numDocs(); docIdx++)
            {
                br.write("\"" + articles.get(docIdx).Author + "\";\"" + docToAuthor[docIdx]  + "\"\n");
            }
            br.close();
        }
        catch (Exception ex)
        {
            System.err.println("Couldn't write to file. LDA\n" + ex.getMessage());
        }
    }
}
