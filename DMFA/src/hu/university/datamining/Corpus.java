package hu.university.datamining;

import hu.university.utilities.MatrixEx;

import java.io.*;
import java.util.*;

public class Corpus
{
    private List<Article> articles = new ArrayList<Article>();
    /**
     * Key = words in the corpus
     * Value = the index of the word in the term-document matrix.
     */
    private HashMap<String, Integer> wordMap = new HashMap<String, Integer>();
    private MatrixEx termDocumentMatrix;

    public Corpus(List<Article> articles)
    {
        initializeFromArticles(articles);
    }

    public Corpus(String filePath) throws FileNotFoundException
    {
        initializeFromFile(filePath);
    }

    public Corpus(File file) throws FileNotFoundException
    {
        initializeFromFile(file);
    }

    public int GetWordOccurence(String word)
    {
        if(!wordMap.containsKey(word))
            return 0;

        return (int)termDocumentMatrix.GetRowSum(wordMap.get(word));
    }

    public int GetWordOccurenceInDocument(String word, int articleIndex)
    {
        if(!wordMap.containsKey(word))
            return 0;

        return (int)termDocumentMatrix.GetValue(wordMap.get(word), articleIndex);
    }

    public double GetWordOccurenceProbability(String word, int articleIndex)
    {
        if(!wordMap.containsKey(word))
            return 0.0;
        int wordIdx = wordMap.get(word);
        return termDocumentMatrix.GetValue(wordIdx, articleIndex) / termDocumentMatrix.GetColumnSum(articleIndex);
    }

    public Set<String> GetWordSet()
    {
        return wordMap.keySet();
    }

    public int NumberOfArticles()
    {
        return articles.size();
    }

    public List<Article> GetArticles()
    {
        return articles;
    }



    private void initializeFromArticles(List<Article> articles)
    {
        this.articles = articles;
        initializeWordMap(articles);
        initializeTermDocumentMatrix(articles, wordMap);
    }

    private void initializeFromFile(String filePath) throws FileNotFoundException
    {
        File file = new File(filePath);
        initializeFromFile(file);
    }

    private void initializeFromFile(File file) throws FileNotFoundException
    {
        loadArticles(file);
        initializeFromArticles(articles);
    }

    private void loadArticles(File file) throws FileNotFoundException
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
            while(true)
            {
                String line = br.readLine();
                if(line == null)
                    break;

                line = line.toLowerCase().replaceAll("[^a-z\" ]", "");
                String[] temp = line.split("\"\"");
                temp[1] = temp[1].replaceAll("\"","").replaceAll("[ ]+"," ");
                Article article = new Article(temp[0], temp[1]);
                articles.add(article);
            }
        }
        catch (FileNotFoundException e)
        {
            throw e;
        }
        catch (IOException e)
        {}
        finally
        {
            if(br != null)
                try
                {
                    br.close();
                }
                catch (IOException e)
                {}
        }
    }

    private void initializeWordMap(List<Article> articles)
    {
        for(Article article : articles)
        {
            String[] words = article.Text.split(" ");
            for(String word : words)
            {
                if(wordMap.containsKey(word))
                    continue;

                wordMap.put(word, wordMap.size());
            }
        }
    }

    private  void initializeTermDocumentMatrix(List<Article> articles, Map<String, Integer> wordMap)
    {
        termDocumentMatrix = new MatrixEx(wordMap.size(), articles.size());
        for(int i = 0; i < articles.size(); i++)
        {
            Article article = articles.get(i);
            for(String word : article.Text.split(" "))
            {
                int termIndex = wordMap.get(word);
                termDocumentMatrix.Increment(termIndex, i);
            }
        }
    }

}
