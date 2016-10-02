package hu.university.utilities;

/**
 Created by Zolt�n on 2016. 10. 02.. */
public class DistributionUtilities
{
    public static int getPoisson(double lambda) {
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        }
        while (p > L);

        return k - 1;
    }


}
