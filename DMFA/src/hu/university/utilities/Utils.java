package hu.university.utilities;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 Created by Zoltán on 2016. 10. 23.. */
public class Utils
{

   public static < T > List<T> pickNRandom(List<T> list, int N)
   {
       List<T> copy = new LinkedList<>(list);
       Collections.shuffle(copy);
       return copy.subList(0, N);
   }

    public static double pow(double a, int n)
    {
        return pow(a,n,1.0);
    }

    private static double pow(double a, int n, double e)
    {
        if(n == 0)
            return e;
        double e1 = n % 2 == 1 ? e*a : e;
        return pow(a*a, n/2, e1);
    }
}
