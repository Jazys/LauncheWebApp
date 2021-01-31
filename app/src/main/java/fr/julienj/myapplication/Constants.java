package fr.julienj.myapplication;

class Constants {

    //port Web HTTP
    static final int PORT_HTTP=9000;

    //port WSS
    static final int PORT_WS=9091;

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
