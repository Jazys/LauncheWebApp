package fr.julienj.universalcontroller;

public class Constants {

    //port Web HTTP
    public static final int PORT_HTTP=9000;

    //port WSS
    public static final int PORT_WS=9091;

    //USB
    public static final int DEVICE_ID_XIAO=2002;
    public static final int BAUD_RATE_XIAO=19200;

    //BLUETOOTH 2.0
    public static final String ADRESSE_MAC_PC_JJ="2C:33:7A:26:40:A6";
    public static final String NAME_BLUETOOTH="JULIENJ-HP";

    //BLE
    public static final String MAC_BLUNO="C4:BE:84:1A:C2:07";

    // values have to be globally unique
    public static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    public static final String USB_ATTACHED="android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String USB_DETTACHED="android.hardware.usb.action.USB_DEVICE_DETACHED";

    // values have to be unique within each app
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    public Constants() {}
}
