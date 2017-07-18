package com.example.csilla.camerasample;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.joda.time.LocalDateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {


    String mCurrentPhotoPath;
    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;

    private FrameLayout cameraPreviewLayout;
    private FrameLayout capturedImageHolder;
    private boolean isImageCaputered;
    private Button xButton;
    private Button captureButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraPreviewLayout = (FrameLayout)findViewById(R.id.camera_preview);

        xButton = (Button) findViewById(R.id.delete);
        xButton.setVisibility(View.INVISIBLE);
        camera = checkDeviceCamera();
        mImageSurfaceView = new ImageSurfaceView(MainActivity.this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView);

        captureButton = (Button)findViewById(R.id.button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, mPicture);
                captureButton.setEnabled(false);
                xButton.setVisibility(View.VISIBLE);
            }
        });

        xButton.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v)
                                       {
                                           captureButton.setEnabled(true);
                                           camera.startPreview();
                                           xButton.setVisibility(View.INVISIBLE);
                                       }
                                   });

        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                try {
                    createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

   private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new LocalDateTime().toString();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

       File image = File.createTempFile(imageFileName,  /* prefix */".jpg", /* suffix */storageDir      /* directory */
        );

       // Save a file: path for use with ACTION_VIEW intents
       mCurrentPhotoPath = image.getAbsolutePath();
        return image;
   }
    private Camera checkDeviceCamera(){
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }

//    PictureCallback pictureCallback = new PictureCallback() {
//        @Override
//        public void onPictureTaken(byte[] data, Camera camera) {
//          /*  Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            if(bitmap == null){
//                Toast.makeText(MainActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
//                return;*/
//            }
//
//           // capturedImageHolder.setImageBitmap(scaleDownBitmapImage(bitmap, 300, 200));
//
//
//    };
PictureCallback mPicture = new PictureCallback() {
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
};

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new LocalDateTime().toString();
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    private Bitmap scaleDownBitmapImage(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        return resizedBitmap;
    }




}