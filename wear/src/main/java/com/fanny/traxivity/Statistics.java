package com.fanny.traxivity;

import org.opencv.core.Mat;

/**
 * Created by fanny on 14/07/2016.
 *
 * This method can be used to get the mean, the variance and the standard deviation of a matrix
 * Not used in the actual app as the trained model is upload in the raw folder
 */
public class Statistics {

    Mat data;
    int size;

    public Statistics(Mat data)
    {
        this.data=data;
        size = data.rows();
    }

    double getMean()
    {
        double sum = 0.0;
        for( int i = 0 ; i < size ; i++)
            sum += data.get(i,0)[0];
        return sum/size;
    }

    double getVariance()
    {
        double mean = getMean();
        double temp = 0;
        for( int i = 0 ; i < size ; i++)
            temp += (mean-data.get(i,0)[0])*(mean-data.get(i,0)[0]);
        return temp/size;
    }

    double getStdDev()
    {
        return Math.sqrt(getVariance());
    }

}
