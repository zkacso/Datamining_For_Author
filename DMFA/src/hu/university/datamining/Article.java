package hu.university.datamining;

public class Article
{
    public final String Author;
    public final String Text;

    public String GetAuthor()
    {
        return Author;
    }

    public Article(String author, String text)
    {
        this.Author = author;
        this.Text = text;
    }

}
