/*
 * Created by Android Team MIS-SEG Year 2021
 * Copyright (c) 2021. Guanzon Central Office
 * Guanzon Bldg., Perez Blvd., Dagupan City, Pangasinan 2400
 * Project name : GhostRider_Android
 * Module : GhostRider_Android.imgCapture
 * Electronic Personnel Access Control Security System
 * project file created : 4/24/21 3:19 PM
 * project file last modified : 4/24/21 3:17 PM
 */

package org.rmj.g3appdriver.etc;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFileCreator {

    public static final String TAG = ImageFileCreator.class.getSimpleName();
    private final Context poContext;

    private String imgName;
    private String TransNox, FileCode;
    private int EntryNox;
    String currentPhotoPath;
    private String SUB_FOLDER;

    File image;

    private Intent loIntent;

    private String message = "Something went wrong please restart the app and try again.", sFileName, sFilePath;

    public String getFileName() {
        return sFileName;
    }

    public String getFilePath() {
        return sFilePath;
    }

    public String getMessage() {
        return message;
    }

    public Intent getCameraIntent() {
        return loIntent;
    }

    public void setTransNox(String transNox){
        this.TransNox = transNox;
    }

    public ImageFileCreator(Context context, String usage, String imgName) {
        this.poContext = context;
        this.SUB_FOLDER = usage;
        this.imgName = imgName;
    }

    public File createImageFile() throws IOException {

        image = new File(
                generateMainStorageDir(),
                generateImageFileName());

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        Log.e(TAG, currentPhotoPath + " createImageFile");
        return image;
    }

    @SuppressLint("SimpleDateFormat")
    public String generateTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public String generateImageFileName() {
        String lsResult = imgName + "_" + generateTimestamp() + ".png";
        return lsResult;
    }
    public String generateImageScanFileName() {
        String lsResult = TransNox + "_" + EntryNox + "_" + FileCode + ".png";
        return lsResult;
    }

    public File generateMainStorageDir() {
        String root = String.valueOf(poContext.getExternalFilesDir(null));
        File sd = new File(root + "/" + SUB_FOLDER + "/");
        if (!sd.exists()) {
            sd.mkdirs();
        }
        return sd;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public boolean IsFileCreated(boolean cSelfieLog) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(poContext.getPackageManager()) != null) {

                // Create the File where the photo should go
                File photoFile;

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    message = ex.getMessage();
                    return false;
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(poContext,
                            "org.rmj.guanzongroup.ghostrider.epacss" + ".provider",
                            photoFile);

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if(cSelfieLog) {
                        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    }
                    sFileName = generateImageFileName();
                    sFilePath = currentPhotoPath;
                    loIntent = takePictureIntent;
                    return true;
                }
            }

            return false;

        } catch (Exception e){

            e.printStackTrace();
            message = getLocalMessage(e);

            return false;
        }
    }
}
