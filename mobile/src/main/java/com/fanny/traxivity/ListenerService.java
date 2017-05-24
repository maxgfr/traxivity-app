package com.fanny.traxivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemAsset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import io.realm.Realm;

/**
 * Listen to the changes in the Data Layer Event, used to send the collected data from the wear to the mobile
 */
public class ListenerService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    /**
     * String to define the path for retrieving the DataMapRequest.
     *
     * @see ListenerService#onDataChanged(DataEventBuffer)
     */
    private static final String WEARABLE_DATA_PATH = "/wearable_data";

    /**
     * The app data folder
     */
    private static final String DATA_FOLDER= "/Traxivity/data";

    private GoogleApiClient googleClient;

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleClient.connect();
    }

    /**
     * When there is a change in the Data Layer Event, writes the new data in a file and call sendBroadcast to update the visualization in the main activity
     * @see ListenerService#sendBroadcast()
     * @param dataEvents
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        int dur = Toast.LENGTH_SHORT;
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Data Changed", dur);
        //toast.show();

        System.out.println("Data Changed!");

        for (DataEvent event : dataEvents) {

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {

                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    byte[] realmAsset = dataMapItem.getDataMap().getByteArray("realmDatabase");
                    if(realmAsset != null) {
                        toFile(realmAsset);
                    }

                    DataMap dataMap = dataMapItem.getDataMap();

                    String fileName = dataMap.getString("fileName");
                    String data = dataMap.getString("data");

                    writeToFile(fileName, data);
                    sendBroadcast();

                    /*
                    Map<String,DataItemAsset> assets = event.getDataItem().getAssets();

                    System.out.println("Assets: "+assets.size());

                    for (String key :assets.keySet()){
                        DataItemAsset asset = assets.get(key);
                        System.out.println("ID: "+asset.getId());
                        System.out.println("Data Item Key"+asset.getId());
                    }

                    System.out.println("Data Map Keys: ");
                   for (String key: dataMap.keySet()){
                       System.out.println(key);
                   }


                    //Asset asset = dataMap.get("data");
                    //System.out.println("Asset: ");
                    //System.out.println(asset.getUri().getPath());
                    //System.out.println(new String(asset.getData()));

                    ConnectionResult result = googleClient.blockingConnect();
                    if (result.isSuccess()){
                        System.out.println("Connection Succeeded!");
                        toast.setText("Connection Succeeded");
                        //toast.show();
                        Asset asset = dataMap.getAsset("data");
                        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(googleClient, asset).await().getInputStream();
                        googleClient.disconnect();
                        if (assetInputStream != null){
                            try {
                                BufferedReader br = new BufferedReader(new InputStreamReader(assetInputStream));
                                String data = "";
                                String line;
                                //System.out.println("Asset Content: ");
                                while ((line = br.readLine()) != null) {
                                    //System.out.println(line);
                                    data += line+"\n";
                                }


                                writeToFile(fileName, data);

                                sendBroadcast();

                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }else{
                            System.out.println("Asset Input stream is null");
                            toast.setText("Asset Input stream is null");
                            //toast.show();
                        }
                    }else{
                        System.out.println("Connection Failed!");
                        toast.setText("Connection Failed!");
                        //toast.show();
                    }

                    */

                }


            }
        }


    }

    private void toFile(byte [] byteArray){
        File writableFolder = ListenerService.this.getFilesDir();
        File realmFile = new File(writableFolder, Realm.DEFAULT_REALM_NAME);
        if (realmFile.exists()) {
            realmFile.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(realmFile.getPath());
            fos.write(byteArray);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.d("ListenerService", "toFile exception: " + e.getLocalizedMessage());
        }
    }


    public void writeToFile(String fileName, String data){

        /* if (isExternalStorageWritable()) { */

            File file = new File(getFilesDir() + DATA_FOLDER, fileName+".csv");

            FileWriter filewriter = null;

            try {
                file.createNewFile();
                filewriter = new FileWriter(file, true);
                filewriter.write(data);
                filewriter.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if (filewriter != null){
                    try{
                        filewriter.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }

        /*} else {
            CharSequence text = "The external storage is not writable";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        } */
    }

    /**
     * Send the intent to the BroadcastReceiver dataReceiver in the main activity
     * @see MainActivity#dataReceiver
     */
    public void sendBroadcast() {
        Intent intent = new Intent("newData");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * Check if the external storage is writable
     * @return boolean true if the external storage is writable.
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return(Environment.MEDIA_MOUNTED.equals(state));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
