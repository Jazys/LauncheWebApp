package fr.julienj.universalcontroller;

import java.util.HashMap;
import java.util.Map;

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

    public static final String LOG_TAG = "lWS";
    public static final String ACTION_STOP = "net.basov.lws.stop_service";
    public static final int NOTIFICATION_ID = 1001;
    public static final int DIRECTORY_REQUEST = 170;
    public static final int MAIN_SCREEN_REQUEST = 171;
    public static final int STOP_SERVICE_REQUEST = 172;
    public static final int GRANT_WRITE_EXTERNAL_STORAGE = 173;
    public static final MimeType MIME_OCTAL = new MimeType("application/octet-stream", "file");
    public static final Map<String, MimeType> MIME = new HashMap<String, MimeType>(30, 1.0F) {
        {
            put("html", new MimeType("text/html; charset=utf-8", "web"));
            put("css", new MimeType("text/css; charset=utf-8", "code"));
            put("js", new MimeType("text/javascript; charset=utf-8", "code"));
            put("txt", new MimeType("text/plain; charset=utf-8", "file-text"));
            put("md", new MimeType("text/markdown; charset=utf-8", "file-text"));
            put("gif", new MimeType("image/gif", "image"));
            put("png", new MimeType("image/png", "image"));
            put("jpg", new MimeType("image/jpeg", "image"));
            put("bmp", new MimeType("image/bmp", "image"));
            put("svg", new MimeType("image/svg+xml", "image"));
            put("ico", new MimeType("image/x-icon", "image"));
            put("zip", new MimeType("application/zip", "package"));
            put("gz", new MimeType("application/gzip", "package"));
            put("tgz", new MimeType("application/gzip", "package"));
            put("pdf", new MimeType("application/pdf", "file-text"));
            put("mp4", new MimeType("video/mp4", "video"));
            put("avi", new MimeType("video/x-msvideo", "video"));
            put("3gp", new MimeType("video/3gpp", "video"));
            put("mp3", new MimeType("audio/mpeg", "music"));
            put("ogg", new MimeType("audio/ogg", "music"));
            put("wav", new MimeType("audio/wav", "music"));
            put("flac", new MimeType("audio/flac", "music"));
            put("java", new MimeType("text/plain", "code"));
            put(".c", new MimeType("text/plain", "code"));
            put(".cpp", new MimeType("text/plain", "code"));
            put(".sh", new MimeType("text/plain", "code"));
            put(".py", new MimeType("text/plain", "code"));
        }
    };

    static class MimeType {
        final String contentType;
        final String kind;

        public MimeType(String contentType, String kind) {
            this.contentType = contentType;
            this.kind = kind;
        }
    }

    public Constants() {}
}
