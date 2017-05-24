package com.fanny.traxivity;

import android.hardware.Sensor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Olivier on 19/04/2016.
 */

//This class is made to implement the sensors in the application

/**
 * This class describe a sensor.
 *
 * A Csensor is characterized by:
 *
 * A name, which is attributed definitely.
 * A state, if the sensor is enable or not, it could be change.
 * A sensor, this sensor cannot change.
 *
 * The Csensor is Parcelable, a Csensor can be pass through an intent.
 *
 */
public class Csensor implements Parcelable {

    /**
     * The name of the Csensor. This name can't change.
     *
     * @see Csensor#Csensor(String, boolean, Sensor)
     * @see Csensor#getName()
     */
    private String Name;

    /**
     * The state of the Csensor, if the Csensor is enable or not.
     * It is possible to change the state of the Csensor.
     *
     * @see Csensor#Csensor(String, boolean, Sensor)
     * @see Csensor#isEnabled()
     * @see Csensor#setEnabled(boolean)
     */
    private boolean Enabled;

    /**
     * The sensor associate to the Csensor. This sensor cannot change.
     *
     * @see Csensor#Csensor(String, boolean, Sensor)
     * @see Csensor#getSensorused()
     */
    private Sensor Sensorused;

    /**
     * Constructor of the Csensor.
     *
     * A Csensor is created with the different parameters defined by the user.
     *
     * @param name
     *          The name of the Csensor
     * @param enabled
     *          The state of the Csensor
     * @param sensor
     *          The Sensor of the Csensor
     */
    public Csensor(String name, boolean enabled, Sensor sensor){

        this.Name = name;
        this.Enabled = enabled;
        this.Sensorused = sensor;


    }

    /**
     * Return the name of the Csensor
     *
     * @return The name of the Csensor
     */
    public String getName() {
        return Name;
    }

    /**
     * Return the state of the Csensor
     *
     * @return The state of the Csensor, if it is enable or not
     */
    public boolean isEnabled() {
        return Enabled;
    }

    /**
     * Update the state of the Csensor
     *
     * @param enabled
     *          The new state of the Csensor
     */
    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    /**
     * Return the Sensor of the Csensor
     *
     * @return The Sensor of the Csensor
     */
    public Sensor getSensorused() {
        return Sensorused;
    }

    //Partial implementation of Parcelable, it allows us to pass an ArrayList in parameter in our intent in the main activity

    /**
     * Implementation of the Parcelable interface.
     * It is not possible to parce a Sensor. The SharedPreferences are used to complete that.
     *
     * @param in
     *          The Parcel in which the object should be written.
     */
    private Csensor(Parcel in){

        Name = in.readString();
        Enabled = in.readByte() != 0;

    }

    /**
     * Interface which must be implemented that generate an instance to parce of Csensor
     */
    public static final Creator<Csensor> CREATOR = new Creator<Csensor>() {
        @Override
        public Csensor createFromParcel(Parcel in) {
            return new Csensor(in);
        }

        @Override
        public Csensor[] newArray(int size) {
            return new Csensor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(Name);
        dest.writeByte((byte) (Enabled ? 1 : 0));
    }
}
