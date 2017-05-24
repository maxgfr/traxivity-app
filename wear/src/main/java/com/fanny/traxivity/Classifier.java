package com.fanny.traxivity;


import android.os.Environment;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by fanny on 01/07/2016.
 */

public class Classifier {

    /**
     * This mat will contain the training data
     * Not used in the current app
     */
    private Mat trainingMat;

    /**
     * This mat will contain the labels of the training data
     * Not used in the current app
     */
    private Mat labelMat;

    /**
     * This mat is needed in order to train the model
     * Not used in the current app
     */
    private Mat varIdx = new Mat();

    /**
     * This mat is needed in order to train the model
     * Not used in the current app
     */
    private Mat sampleIdx= new Mat();

    private String filesDir;

    /**
     * The svm used to predict
     */
    private CvSVM svm;


    public Classifier(String filesDir){
        this.filesDir = filesDir;
        svm = new CvSVM();
    }

    /**
     * Load the model and predicts the provided param "sampleData"
     * @param sampleData data that needs to be predicted
     * @return int the prediction
     */
    public int predict(Mat sampleData){
        //svm.load(Environment.getExternalStorageDirectory() + "/Traxivity/models/model.csv");
        svm.load(this.filesDir + "/Traxivity/models/model.csv");
        return (int)svm.predict(sampleData);
    }

    /**
     * This method can be used to train a new model
     * Not used in the actual app as the trained model is upload in the raw folder
     */
    public void trainNewModel(){
        System.out.println("Training new model.");
        setUpTrainingData();
        normalize(trainingMat);
        trainModel();

    }
    /**
     * This method can be used to set up training data from files to matrix before training
     * Not used in the current app as the trained model is upload in the raw folder
     */
    public void setUpTrainingData(){
        trainingMat = new Mat(1915, 192 , CvType.CV_32FC1);
        labelMat = new Mat(1915, 1, CvType.CV_32FC1);

        String trainingFile = Environment.getExternalStorageDirectory()  + "/Traxivity/train.csv";
        String labelFile = Environment.getExternalStorageDirectory() + "/Traxivity/labels_int.csv";
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
                    trainingMat.put(j, i, Float.parseFloat(splitLine[i]));
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

        try {

            br = new BufferedReader(new FileReader(labelFile));
            int j = 0;
            while ((line = br.readLine()) != null) {
                labelMat.put(j, 0, Float.parseFloat(line));
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

    /**
     * This method can be used to train a model
     * The setUpTrainingData method must be called before
     * Not used in the actual app as the trained model is upload in the raw folder
     */
    public void trainModel(){
        CvSVMParams params = new CvSVMParams();
        svm.train_auto(trainingMat, labelMat, varIdx, sampleIdx, params);
        svm.save(Environment.getExternalStorageDirectory() + "/Traxivity/model");
    }
    /**
     * This method can be used to normalize a feature
     * Not used in the actual app as the trained model is upload in the raw folder
     */
    public void normalize(Mat train_features){
        Mat meansigmas = new Mat(2, train_features.cols(), CvType.CV_32FC1) ; //matrice to save all the means and standard deviations

        for (int i = 0; i < train_features.cols(); i++){  //take each of the features in vector

            Statistics stat = new Statistics(train_features.col(i));

            float mean = (float) stat.getMean();

            float sigma = (float) stat.getStdDev();

            meansigmas.put(0, i, mean);
            meansigmas.put(1, i, sigma);


            for(int j = 0; j < train_features.col(i).rows();j++) {

                float normalized = (float)(train_features.get(j,i)[0] - mean) / sigma;

                //train_features.put(j, i, normalized);
                train_features.col(i).put(j, 0, normalized);

            }

        }
        //optional steps to save all the parameters
        saveMatToCsv(meansigmas, "meansigma.csv");  //custom function to save data to .csv file

    }


    /**
     * This method can be used to save the mean and the standard deviation in a csv file
     * Not used in the actual app as the trained model is upload in the raw folder
     */
    public void saveMatToCsv(Mat mat, String fileName){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0 ; i < mat.rows();i++ ){
            if (i != 0){
                stringBuffer.append("\n");
            }
            for (int j = 0 ; j<mat.cols(); j++ ){
                if (j != 0){
                    stringBuffer.append(",");
                }
                stringBuffer.append(mat.get(i,j)[0]);
            }
        }

        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Traxivity/"+fileName);
        //FileOutputStream fos;
        try {

            file.createNewFile();
            FileWriter filewriter = new FileWriter(file, true);
            filewriter.write(stringBuffer.toString());
            filewriter.close();
            stringBuffer.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
