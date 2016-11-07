package hu.university.datamining;

import hu.university.utilities.Matrix;
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
    private Matrix termDocumentMatrix;
    private DimensionReducer dr;
    private Stemmer stemmer;
    private static final int authorIndexInDocument = 0;
    private static final int articleIndexInDocument = 1;

    //region Constructors and initialization

    public Corpus(String filePath, Stemmer st, DimensionReducer dr) throws FileNotFoundException
    {
        this.stemmer = st;
        this.dr = dr;
        initializeFromFile(filePath);
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
                String[] articleInfos = line.split("\"\"");
                String articleContent = articleInfos[articleIndexInDocument].replaceAll("\"", "").replaceAll("[ ]+", " ");

                String[] words = articleContent.split(" ");
                words = dr.filterStopWords(words);
                StringBuilder sb = new StringBuilder();
                for(String word : words)
                {
                    sb.append(stemmer.stem(word)).append(' ');
                }

                Article article = new Article(articleInfos[authorIndexInDocument].replace("\"", ""), sb.toString());
                articles.add(article);
            }
            this.articles = dr.reduceWordDimension(articles);
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
        termDocumentMatrix = new Matrix(wordMap.size(), articles.size());
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
    //endregion





    public int GetWordOccurrenceInDocument(int wordIndex, int articleIndex)
    {
        return (int)termDocumentMatrix.GetValue(wordIndex,articleIndex);
    }

    public Set<String> GetWordSet()
    {
        return wordMap.keySet();
    }

    public int GetWordIndex(String word)
    {
        return wordMap.get(word);
    }

    public int NumberOfArticles()
    {
        return articles.size();
    }

    public int NumberOfWords() { return wordMap.size(); }

    public List<Article> GetArticles()
    {
        return articles;
    }

    public Article GetArticle(int index)
    {
        return articles.get(index);
    }

    public Article GetArticleById(int ID)
    {
        for(Article a : articles)
        {
            if(a.ID == ID)
                return a;
        }
        throw new IndexOutOfBoundsException();
    }

    public int GetArticleIndex(Article a)
    {
        for(int i = 0; i < articles.size(); i++)
        {
            if(articles.get(i).ID == a.ID)
                return i;
        }
        throw new IndexOutOfBoundsException();
    }

    public String GetWordOfArticle(int articleIndex, int wordIndex)
    {
        return articles.get(articleIndex).TextAsWords[wordIndex];
    }

    public int GetArticleLength(int articleIndex)
    {
        return articles.get(articleIndex).TextAsWords.length;
    }


}
