package fr.julienj.universalcontroller.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Utils {

    public static String getMessageByte(byte[] data)
    {
        String mess="";
        for(int i=0 ; i<data.length; i++){
            mess+= String.valueOf((char)data[i]);
        }

        return mess;
    }

    public String compressString(String stringToCompress)
    {
        //Log.i("jj", "Compressing String " + stringToCompress);
        byte[] input = stringToCompress.getBytes();
        // Create the compressor with highest level of compression
        Deflater compressor = new Deflater();
        //compressor.setLevel(Deflater.BEST_COMPRESSION);
        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();
        // Create an expandable byte array to hold the compressed data.
        // You cannot use an array that's the same size as the orginal because
        // there is no guarantee that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        // Compress the data
        byte[] buf = new byte[1024];
        while (!compressor.finished())
        {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }

        try {
            bos.close();
        } catch (IOException e)
        {

        }
        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        return new String(compressedData);
    }

    public static byte[] decompress(byte[] data)  {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = 0;
            try {
                count = inflater.inflate(buffer);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] output = outputStream.toByteArray();
        inflater.end();
        return output;
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a) {
            sb.append(String.format("%02x", b));
            //System.out.println("jj "+String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String decodeQRImage(String path) {
        File f= new File("mnt/sdcard/qrcode_test.png");
        Bitmap bMap = BitmapFactory.decodeFile("mnt/sdcard/qrcode_test.png");
        String decoded = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            decoded = result.getText();
            System.out.println("jj image "+byteArrayToHex(result.getRawBytes()));
        } catch (NotFoundException e) {
            e.printStackTrace();
            System.out.println("jj err1");
        } catch (ChecksumException e) {
            e.printStackTrace();
            System.out.println("jj err2");
        } catch (FormatException e) {
            e.printStackTrace();
            System.out.println("jj err3");
        }
        return decoded;
    }
}
