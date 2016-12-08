package hu.university.datamining;

import hu.university.utilities.Matrix;

import java.io.*;
import java.util.*;

public class Corpus
{
    private List<Article> articles = new ArrayList<>();
    /**
     * Key = words in the corpus
     * Value = the index of the word in the term-document matrix.
     */
    private HashMap<String, Integer> wordMap = new HashMap<>();
    private Matrix termDocumentMatrix;
    private DimensionReducer dimensionReducer;
    private Stemmer stemmer;
    private static final int authorIndexInDocument = 0;
    private static final int articleIndexInDocument = 1;

    //region Constructors and initialization

    public Corpus(String filePath, Stemmer st, DimensionReducer dimensionReducer) throws FileNotFoundException
    {
        this.stemmer = st;
        this.dimensionReducer = dimensionReducer;
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

                Article article = createArticleFromFileLine(line);
                articles.add(article);
            }
            this.articles = dimensionReducer.reduceWordDimension(articles);
        }
        catch (FileNotFoundException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            System.err.println("Couldn't read from file.");
        }
        finally
        {
            if(br != null)
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    System.err.println("Couldn't close file handler");
                }
        }
    }

    private Article createArticleFromFileLine(String line)
    {
        line = line.toLowerCase().replaceAll("[^a-z\" ]", "");
        String[] articleInfos = line.split("\"\"");
        String articleContent = articleInfos[articleIndexInDocument].replaceAll("\"", "").replaceAll("[ ]+", " ");

        String[] words = articleContent.split(" ");
        words = dimensionReducer.filterStopWords(words);
        StringBuilder sb = new StringBuilder();
        for(String word : words)
        {
            sb.append(stemmer.stem(word)).append(' ');
        }

        Article article = new Article(articleInfos[authorIndexInDocument].replace("\"", ""), sb.toString());
        return article;
    }

    private void initializeWordMap(List<Article> articles)
    {
        for(Article article : articles)
        {
            String[] words = article.TextAsWords;
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
        for(int articleIdx = 0; articleIdx < articles.size(); articleIdx++)
        {
            Article article = articles.get(articleIdx);
            for(String word : article.TextAsWords)
            {
                int termIndex = wordMap.get(word);
                termDocumentMatrix.Increment(termIndex, articleIdx);
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

    public Set<String> GetAuthors()
    {
        HashSet<String> authors = new HashSet<>();
        for(Article a : articles)
        {
            authors.add(a.Author);
        }
        return authors;
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
