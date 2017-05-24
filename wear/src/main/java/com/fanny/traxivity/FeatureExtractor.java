package com.fanny.traxivity;

import android.app.Activity;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by Fanny on 28/06/2016.
 */
public class FeatureExtractor{
    /**
     * The app models folder
     */
    private static final String MODELS_FOLDER= "/Traxivity/models";

    private int i;

    /**
     * value of the x axis
     */
    private double x;

    /**
     * value of the y axis
     */
    private double y;

    /**
     * value of the z axis
     */
    private double z;

    /**
     * value of the magnitude m
     */
    private double m;

    /**
     * Mat taken as an input by the dct method
     */
    private Mat input;

    /**
     * Mat used as an output for the dct method
     */
    private Mat output;

    /**
     * Mat used to store the final feature
     */
    private Mat finalFeature;

    /**
     * Mat used to store the mean and the standard deviation
     */
    private Mat meansigma;

    private String filesDir;

    public int windownb;
    public int catchnb;

    /**
     * Create the meansigma Mat and call the csvToMat method to fill it
     * @see FeatureExtractor#csvToMat(String, Mat)
     */
    public FeatureExtractor(String filesDir){
        this.filesDir = filesDir;
        meansigma = new Mat(2, 192 , CvType.CV_32FC1);
        String file = "meansigma.csv";
        csvToMat(file, meansigma);
        System.out.println("Recieved files dir: "+filesDir);

        windownb=0;
        catchnb=0;


    }

    /**
     * Take a window and return the final feature
     * put the window into a Mat, add the magnitude
     * apply the DCT
     * create the final feature by concatenating the absolute values of the first 48 coefficients of x', y', z' and m'
     * normalize the feature
     * @see FeatureExtractor#normalize(Mat)
     * @param window ArrayList of string containing all the sensor changes done during the window's length
     * @return Mat final feature
     */
    public Mat extract(ArrayList<String> window){

        try {

            if (window.size() % 2 != 0) { // Currently dct doesn't supports odd-size Mat
                window.remove(window.size() - 1);
            }


            input = new Mat(4, window.size(), CvType.CV_32FC1); // CvType.CV_32FC1 = float


            output = new Mat(4, window.size(), CvType.CV_32FC1);

            for (i = 0; i < window.size(); i++) {
                String[] line = window.get(i).split(",");
                x = Double.parseDouble(line[1]);
                y = Double.parseDouble(line[2]);
                z = Double.parseDouble(line[3]);
                m = Math.sqrt(x * x + y * y + z * z);

                input.put(0, i, x);
                input.put(1, i, y);
                input.put(2, i, z);
                input.put(3, i, m);
            }

            Core.dct(input, output, Core.DCT_ROWS); //If (flags & DCT_ROWS) != 0 , the function performs a 1D transform of each row.


            finalFeature = new Mat(1, 192, CvType.CV_32FC1);

            for (i = 0; i < 48; i++) {
                finalFeature.put(0, i, Math.abs(output.get(0, i)[0]));
                finalFeature.put(0, i + 48, Math.abs(output.get(1, i)[0]));
                finalFeature.put(0, i + 48 * 2, Math.abs(output.get(2, i)[0]));
                finalFeature.put(0, i + 48 * 3, Math.abs(output.get(3, i)[0]));
            }

            normalize(finalFeature);

            windownb++;


        }catch(Exception e){
            Log.e("ERROR", e.getMessage());
            catchnb++;
        }

        return finalFeature;
    }

    /**
     *  Normalize the feature by using the mean and the standart deviation contained in the meansigma file
     * @param testData matrix of 1 rows and 192 columns containing the feature
     */
    public void normalize(Mat testData){

        for (int i = 0; i < testData.rows(); i++){  //take each of the features in vector

            for(int j = 0; j < testData.row(i).cols();j++) {

                float normalized = (float)(testData.get(i,j)[0] - (float)meansigma.get(0, j)[0]) / (float)meansigma.get(1,j)[0];

                testData.row(i).put(i, j, normalized);

            }
        }
    }

    /**
     * Returns the matrix in which each row corresponds to a line of the csv file
     * @param file name of the file that needs to be transformed
     * @param mat matrix where the csv will be put
     */
    public void csvToMat(String file, Mat mat){

       //String trainingFile = Environment.getExternalStorageDirectory() + MODELS_FOLDER + "/"+ file;
        String trainingFile = this.filesDir + MODELS_FOLDER + "/"+ file;
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(trainingFile));
            int j = 0;
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] splitLine = line.split(cvsSplitBy);
                for(int i =0 ; i < splitLine.length ; i++){
                    mat.put(j, i, Float.parseFloat(splitLine[i]));
                }
                j++;
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
