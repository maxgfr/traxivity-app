package com.fanny.traxivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

import biz.source_code.dsp.filter.FilterPassType;
import biz.source_code.dsp.filter.IirFilter;
import biz.source_code.dsp.filter.IirFilterCoefficients;
import biz.source_code.dsp.filter.IirFilterDesignExstrom;

/**
 * Created by Sadiq on 15/12/2016.
 */

public class StepCounter {

    public static void countSteps(List<String> data){

        Mat mat = new Mat(1, data.size(), CvType.CV_32FC1);
        Mat dft_mat = new Mat(1, data.size(), CvType.CV_32FC1);

        for (int i=0; i<data.size(); i++){
            String[] vals = data.get(i).split(",");
            double x = Double.parseDouble(vals[1]);
            double y = Double.parseDouble(vals[2]);
            double z = Double.parseDouble(vals[3]);
            double m = Math.sqrt(x*x + y*y + z*z);
            mat.put(0,i, m);
        }

        Core.dft(mat, dft_mat);
        Core.MinMaxLocResult minmax = Core.minMaxLoc(dft_mat);
        //double max_val = minmax.maxLoc.;
        //double min = minmax.minVal;

        //Core.
        int max_i = -1;
        double base = Math.abs(dft_mat.get(0, 1)[0]);
        double max=0;
        for (int i=1; i<dft_mat.cols(); i++){
            double val = Math.abs(dft_mat.get(0, i)[0]);
            if (val > max){
                max = val;
                max_i = i;
            }
        }

        System.out.println("Base: "+base+"\t Max i: "+max_i+"\t Max: "+max);

    }

    public static int countSteps1(List<String> data){

        //ApproximationFunctionType functionType = ApproximationFunctionType.BUTTERWORTH;
        FilterPassType filterPassType = FilterPassType.lowpass;
        int filterOrder = 5;
        double fcf1 = 0.02;
        double fcf2 = 0.0;

        IirFilterCoefficients coeffs = IirFilterDesignExstrom.design(filterPassType, filterOrder, fcf1, fcf2);
        IirFilter filter = new IirFilter(coeffs);

        List<Double> mag = mag(data);
        List<Double> fSignal = new ArrayList();
        for (int i=0; i<mag.size();i++){
            //double val = Double.parseDouble(mag.get(i));
            double fVal = filter.step(mag.get(i));
            fSignal.add(fVal);
        }

        List<Integer> peaks = detectPeaks(fSignal);
        int steps = peaks.size();
        return steps;

    }

    public static int countSteps2(List<String> data){
        double alpha = 0.15;
        List<Double> x = mag(data);
        List<Double> y = new ArrayList();
        y.add(x.get(0));
        for (int i=1; i<x.size(); i++){
            double val = y.get(i-1) + alpha * (x.get(i) - y.get(i-1));
            y.add(val);

        }
        List<Integer> peaks = detectPeaks(y);
        return peaks.size();
    }


    public static List<Integer> detectPeaks(List<Double> v, double... mph){

        List<Double> dx = new ArrayList();
        for (int i=1; i<v.size();i++){
            double val = v.get(i) - v.get(i-1);
            dx.add(val);
        }

        // for each negative value in dx, find ones which are precede by a positive value
        // i.e. i < 0 & i-1 > 0. Indices that satisfy these conditions are the peaks
        // This method also checks if identified peaks are greater than a given mimimum peak height (mph)
        List<Integer> indices = new ArrayList();
        for (int i=1; i<dx.size()-1;i++){
            if(dx.get(i) < 0 && dx.get(i-1) > 0){
                if (mph.length > 0) {
                    if (v.get(i) > mph[0]) {
                        indices.add(i);
                    }
                }else{
                    indices.add(i);
                }
            }
        }

        return indices;

    }


    public static List<Double> mag(List<String> data){
        List<Double> mag = new ArrayList();
        for (int i=0; i<data.size(); i++){
            String[] vals = data.get(i).split(",");
            double x = Double.parseDouble(vals[1]);
            double y = Double.parseDouble(vals[2]);
            double z = Double.parseDouble(vals[3]);
            double m = Math.sqrt(x*x + y*y + z*z);
            mag.add(m);
        }
        return mag;
    }


}
