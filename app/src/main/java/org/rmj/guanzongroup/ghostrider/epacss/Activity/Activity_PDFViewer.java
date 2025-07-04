package org.rmj.guanzongroup.ghostrider.epacss.Activity;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.github.barteksc.pdfviewer.PDFView;

import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;
import org.rmj.guanzongroup.ghostrider.epacss.R;
import org.rmj.guanzongroup.ghostrider.epacss.ViewModel.VMLoadPDF;

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

        if (!getIntent().hasExtra("pdf_url")){
            poMessage.setTitle("PDF Viewer");
            poMessage.setMessage("No PDF file found");
            poMessage.setIcon(R.drawable.baseline_error_24);
            poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
                @Override
                public void OnButtonClick(View view, AlertDialog dialog) {

                }
            });

            return;
        }

        String pdf_url = getIntent().getStringExtra("pdf_url");
        mviewmodel.DisplayPDF(pdf_url, new VMLoadPDF.OnLoadPDF() {
            @Override
            public void OnLoading() {
                poDialog.initDialog("PDF File", "Loading PDF file...", false);
                poDialog.show();
            }

            @Override
            public void OnSuccess(InputStream inputStream) {
                poDialog.dismiss();

                pdv_viewer.fromStream(inputStream)
                        .enableSwipe(true)
                        .enableDoubletap(true)
                        .swipeHorizontal(false)
                        .defaultPage(0)
                        .load();
            }

            @Override
            public void OnFailed(String message) {
                poDialog.dismiss();
            }
        });
    }
}