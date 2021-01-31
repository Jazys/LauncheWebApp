package fr.julienj.myapplication;

class Constants {

    //port Web HTTP
    static final int PORT_HTTP=9000;

    //port WSS
    static final int PORT_WS=9091;

    //USB
    static final int DEVICE_ID_XIAO=2002;
    static final int BAUD_RATE_XIAO=19200;

    //BLUETOOTH 2.0
    static final String ADRESSE_MAC_PC_JJ="2C:33:7A:26:40:A6";
    static final String NAME_BLUETOOTH="JULIENJ-HP";

    // values have to be globally unique
    static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    static final String USB_ATTACHED="android.hardware.usb.action.USB_DEVICE_ATTACHED";
    static final String USB_DETTACHED="android.hardware.usb.action.USB_DEVICE_DETACHED";

    // values have to be unique within each app
    static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private Constants() {}
}
