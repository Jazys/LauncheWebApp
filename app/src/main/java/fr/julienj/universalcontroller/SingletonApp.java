package fr.julienj.universalcontroller;

public class SingletonApp {

    private static SingletonApp sSoleInstance;

    private SingletonApp(){}  //private constructor.

    public static boolean isActivityRecreate;
    public static boolean serviceWebIsRunning;
    public static boolean serviceLSUSBIsRunning;
    public static boolean serviceWebSocketIsRunning;
    public static boolean serviceBluetoothIsRunning;
    public static boolean serviceBLEIsRunning;

    public static SingletonApp getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new SingletonApp();
            isActivityRecreate=false;
            serviceWebIsRunning=false;
            serviceLSUSBIsRunning=false;
            serviceWebSocketIsRunning=false;
            serviceBluetoothIsRunning=false;
            serviceBLEIsRunning=false;

        }

        return sSoleInstance;
    }
}