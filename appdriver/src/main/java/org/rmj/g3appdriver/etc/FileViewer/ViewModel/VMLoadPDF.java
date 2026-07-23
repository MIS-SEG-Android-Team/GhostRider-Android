package org.rmj.g3appdriver.etc.FileViewer.ViewModel;

import android.app.Application;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VMLoadPDF extends AndroidViewModel {

    private String lsMessage;

    public interface OnStreamPDF {
        void OnLoading();
        void OnSuccess(File foFile);
        void OnFailed(String message);
    }

    public interface OnLoadPDFile {
        void OnLoading();
        void OnSuccess(File foFile);
        void OnFailed(String message);
    }

    public VMLoadPDF(@NonNull Application application) {
        super(application);
    }

    public void StreamPDF(String url, OnStreamPDF callback){

        TaskExecutor.Execute(url, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoading();
            }

            @Override
            public Object DoInBackground(Object args) {

                try {
                    // initialize target directory
                    File parentDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                            "GCircleExports/PDFViewer/"
                    );
                    if (!parentDir.exists() || !parentDir.isDirectory()) {
                        parentDir.mkdirs();
                    }

                    File[] laFiles = parentDir.listFiles();
                    int count_files = (laFiles == null ? 0 : laFiles.length) + 1;

                    // Correct filename logic
                    String lsFileNme = "PDF_" + count_files;
                    if (count_files < 10) {
                        lsFileNme = "PDF_000" + count_files;
                    } else if (count_files < 100) {
                        lsFileNme = "PDF_00" + count_files;
                    } else if (count_files < 1000) {
                        lsFileNme = "PDF_0" + count_files;
                    }

                    File pdfFile = new File(parentDir, lsFileNme + ".pdf");

                    URL url = new URL(args.toString());
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                    if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(httpConn.getInputStream());
                        FileOutputStream out = new FileOutputStream(pdfFile);

                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }

                        out.flush();
                        out.close();
                        in.close();

                        return pdfFile; // return the saved file
                    } else {
                        lsMessage = "HTTP error: " + httpConn.getResponseCode();
                        return false;
                    }
                } catch (Exception e) {
                    lsMessage = e.getMessage();
                    return false;
                }

            }

            @Override
            public void OnPostExecute(Object object) {

                if (object instanceof Boolean){
                    callback.OnFailed("Failed to load PDF: " + lsMessage);
                }else if (object instanceof File loPDF){

                    if (!loPDF.exists() || !loPDF.isFile() || !loPDF.canRead()){
                        callback.OnFailed("Failed to read PDF");
                        return;
                    }
                    callback.OnSuccess(loPDF);
                }else {
                    callback.OnFailed("Failed to load PDF");
                }
            }
        });
    }

    public void DisplayPDFile(File foFile, OnLoadPDFile callback){

        TaskExecutor.Execute(foFile, new OnTaskExecuteListener() {
            @Override
            public void OnPreExecute() {
                callback.OnLoading();
            }

            @Override
            public Object DoInBackground(Object args) {
                try{
                    File loFile = (File) args;
                    if (!(loFile.exists() || loFile.isFile() || loFile.canRead())){
                        return false;
                    }
                    return true;
                }catch (Exception e){
                    lsMessage = e.getMessage();
                    return false;
                }
            }

            @Override
            public void OnPostExecute(Object object) {
                if (object == null){
                    callback.OnFailed("Failed to load PDF: " + lsMessage);
                }else{
                    callback.OnSuccess(foFile);
                }
            }
        });
    }
}
