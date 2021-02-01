package fr.julienj.universalcontroller;

public class SingletonApp {

    private static SingletonApp sSoleInstance;

    private SingletonApp(){}  //private constructor.

    public static boolean isActivityRecreate;

    public static SingletonApp getInstance(){
        if (sSoleInstance == null){ //if there is no instance available... create new one
            sSoleInstance = new SingletonApp();
            isActivityRecreate=false;
        }

        return sSoleInstance;
    }
}