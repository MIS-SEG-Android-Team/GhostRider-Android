package org.rmj.g3appdriver.etc;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtility {

    private final Context context;

    public FileUtility(Context context){
        this.context = context;
    }

    public String GetDisplayName(Uri uri){

        try {

            String filePath = null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {

                Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        filePath = cursor.getString(index);
                    }

                    cursor.close();
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filePath = uri.getPath();
            }
            return filePath;

        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    public File GetFileFromUri(Uri uri) {

        String fileName = GetDisplayName(uri);
        File destinationFile = new File(context.getFilesDir(), fileName);

        try {

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }

        return destinationFile;
    }

    public byte[] ReadFileToBytes(File file) throws IOException {

        byte[] bytes = new byte[(int) file.length()];
        FileInputStream fis = new FileInputStream(file);

        try {
            int read = fis.read(bytes);

            if (read != bytes.length) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }finally {
            fis.close();
        }

        return bytes;
    }
}
