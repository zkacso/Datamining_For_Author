package hu.university.utilities;

/**
 Created by Zoltán on 2016. 10. 31.. */
public class Stopper
{
    public static Stopper instance = new Stopper();

    private long currMilis;
    private long startMilis;

    public void Start()
    {
        this.startMilis = System.currentTimeMillis();
        this.currMilis = startMilis;
    }

    public void Tick()
    {
        currMilis = System.currentTimeMillis();
    }
    public void Tick(String Text)
    {
     long newCurrMilis = System.currentTimeMillis();
     System.out.println(Text + ": " + (newCurrMilis - currMilis));
     currMilis = newCurrMilis;
    }

    public void ElapsedMiliseconds()
    {
        System.out.println(System.currentTimeMillis() - startMilis);
    }
}
