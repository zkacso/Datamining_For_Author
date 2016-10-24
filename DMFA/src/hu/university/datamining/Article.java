package hu.university.datamining;

public class Article
{
    public final String Author;
    public final String Text;
    public final String[] TextAsWords;
    private static int currentid = 0;
    public final int ID;

    public String GetAuthor()
    {
        return Author;
    }

    public Article(String author, String text)
    {
        this.ID = currentid;
        this.Author = author;
        this.Text = text;
        this.TextAsWords = text.split(" ");
        currentid++;
    }

}
