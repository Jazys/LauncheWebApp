package fr.julienj.universalcontroller.utils;

public class Utils {

    public static String getMessageByte(byte[] data)
    {
        String mess="";
        for(int i=0 ; i<data.length; i++){
            mess+= String.valueOf((char)data[i]);
        }

        return mess;
    }
}
