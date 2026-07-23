package org.rmj.g3appdriver.etc.FileViewer.Activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.alamin5g.pdf.PDFView;

import org.rmj.g3appdriver.etc.FileViewer.ViewModel.VMLoadPDF;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.io.File;
import java.io.InputStream;

public class Activity_PDFViewer extends AppCompatActivity {

    private VMLoadPDF mviewmodel;
    private LoadDialog poDialog;
    private PDFView pdv_viewer;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pdfviewer);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        poMessage = new MessageBox(this);
        mviewmodel = new ViewModelProvider(this).get(VMLoadPDF.class);
        poDialog = new LoadDialog(this);
        pdv_viewer = findViewById(R.id.pdv_viewer);

        poMessage.initDialog();

        InitData();
    }

    private void InitData(){

        if (!getIntent().hasExtra("pdf_type")){

            poMessage.setTitle("PDF Viewer");
            poMessage.setMessage("Could not define PDF type");
            poMessage.setIcon(R.drawable.baseline_error_24);
            poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                @Override
                public void OnButtonClick(View view, AlertDialog dialog) {
                    dialog.dismiss();
                }
            });
            return;
        }else if (!getIntent().hasExtra("pdf_url")){

            poMessage.setTitle("PDF Viewer");
            poMessage.setMessage("No PDF file found");
            poMessage.setIcon(R.drawable.baseline_error_24);
            poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                @Override
                public void OnButtonClick(View view, AlertDialog dialog) {
                    dialog.dismiss();
                }
            });
            return;
        }

        String pdf_type = getIntent().getStringExtra("pdf_type");
        String pdf_url = getIntent().getStringExtra("pdf_url");

        if (pdf_type.equalsIgnoreCase("stream")){
            StreamPDF(pdf_url);
        }else if (pdf_type.equalsIgnoreCase("file")){
            DisplayPDFile(pdf_url);
        }

    }

    private void StreamPDF(String fsURL){

        mviewmodel.StreamPDF(fsURL, new VMLoadPDF.OnStreamPDF() {
            @Override
            public void OnLoading() {
                poDialog.initDialog("PDF File", "Loading PDF file...", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess(File foFile) {
                poDialog.dismiss();

                int pdf_index = 1;
                if (getIntent().hasExtra("pdf_index")){
                    pdf_index = Integer.parseInt(getIntent().getStringExtra("pdf_index"));
                }

                Uri uri = FileProvider.getUriForFile(Activity_PDFViewer.this, getPackageName() + ".provider", foFile);
                pdv_viewer.fromUri(uri)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .swipeHorizontal(false)
                        .defaultPage(pdf_index)
                        .load();
            }

            @Override
            public void OnFailed(String message) {
                poDialog.dismiss();
            }
        });

    }

    private void DisplayPDFile(String fsPath){

        mviewmodel.DisplayPDFile(new File(fsPath), new VMLoadPDF.OnLoadPDFile() {
            @Override
            public void OnLoading() {
                poDialog.initDialog("PDF File", "Loading PDF file...", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess(File foFile) {
                poDialog.dismiss();

                int pdf_index = 1;
                if (getIntent().hasExtra("pdf_index")) {
                    pdf_index = Integer.parseInt(getIntent().getStringExtra("pdf_index"));
                }

                pdv_viewer.fromFile(foFile)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .swipeHorizontal(false)
                        .defaultPage(pdf_index)
                        .load();
            }

            @Override
            public void OnFailed(String message) {
                poDialog.dismiss();
            }
        });
    }
}