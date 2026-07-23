package org.rmj.g3appdriver.etc.FileViewer;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.rmj.g3appdriver.GCircle.Apps.ApprovalCode.ApprovalCode;
import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DImageInfo;
import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.GCircle.room.Repositories.RImageInfo;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.FileUtility;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.OnSwipeListener;
import org.rmj.g3appdriver.utils.ConnectionUtil;
import org.rmj.g3appdriver.utils.GestureListener;
import org.rmj.g3appdriver.utils.Task.OnDoBackgroundTaskListener;
import org.rmj.g3appdriver.utils.Task.OnTaskExecuteListener;
import org.rmj.g3appdriver.utils.Task.TaskExecutor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class FileViewer extends AppCompatActivity{

    private VMFileViewer mViewModel;
    private LoadDialog poLoad;
    private Adapter_File_Viewer loAdapter;

    String lsDfrom, lsDto;

    private MaterialToolbar toolbar;
    private ImageButton btn_filter;
    private TextInputEditText tie_search;
    private RecyclerView rcv_list;
    private MaterialCardView mcv_image;
    private PhotoView siv_image;

    private ConstraintLayout layout_info;
    private MaterialTextView mtv_close, mtv_filenamne, mtv_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_viewer);

        mViewModel = new ViewModelProvider(this).get(VMFileViewer.class);
        poLoad = new LoadDialog(this);

        toolbar = findViewById(R.id.toolbar);
        btn_filter = findViewById(R.id.btn_filter);
        tie_search = findViewById(R.id.tie_search);
        rcv_list = findViewById(R.id.rcv_list);
        mcv_image = findViewById(R.id.mcv_image);
        siv_image = findViewById(R.id.siv_image);

        layout_info = findViewById(R.id.layout_info);
        mtv_close = findViewById(R.id.mtv_close);
        mtv_filenamne = findViewById(R.id.mtv_filenamne);
        mtv_path = findViewById(R.id.mtv_path);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InitListener();
        InitDataReceiver();

        DownloadAttachments();

    }

    private void DownloadAttachments(){

        if (!getIntent().hasExtra("sSourceCd")){
            Toast.makeText(this, "Source code not initialized", Toast.LENGTH_SHORT).show();
            return;
        }else if (!getIntent().hasExtra("sSourceNo")){
            Toast.makeText(this, "Source number not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.ImportAttachments(getIntent().getStringExtra("sSourceCd"), getIntent().getStringExtra("sSourceNo"), new VMFileViewer.OnTransaction() {
            @Override
            public void OnLoad(String fsTitle, String fsMessage) {
                poLoad.initDialog(fsTitle, fsMessage, false);
                poLoad.show();
            }

            @Override
            public void OnSuccess() {
                poLoad.dismiss();
                Toast.makeText(FileViewer.this, "Attachments successfully downloaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.dismiss();
                Toast.makeText(FileViewer.this, fsMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void DownloadFile(String fsTransNox){

        mViewModel.DownloadFile(fsTransNox, new VMFileViewer.OnTransaction() {
            @Override
            public void OnLoad(String fsTitle, String fsMessage) {
                poLoad.initDialog("Attachment", "Downloading attachment. Please wait. .", false);
                poLoad.show();
            }

            @Override
            public void OnSuccess() {
                poLoad.dismiss();
                Toast.makeText(FileViewer.this, "Attachment successfully downloaded", Toast.LENGTH_SHORT).show();

                InitDataReceiver();
            }

            @Override
            public void OnFailed(String fsMessage) {
                poLoad.dismiss();
                Toast.makeText(FileViewer.this, fsMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void DisplayImage(File foFile){

        // Use FileProvider to get a safe content URI
        Uri uri = FileProvider.getUriForFile(
                FileViewer.this,
                FileViewer.this.getPackageName() + ".provider",
                foFile
        );

        //display image
        Glide.with(FileViewer.this)
                .asBitmap() // force bitmap decoding
                .load((foFile.exists() && foFile.isFile() && foFile.canRead()) ? uri : R.drawable.baseline_error_24)  // use safe URI instead of raw path
                .apply(new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE))
                .error(R.drawable.baseline_error_24)
                .into(siv_image);

        if (mcv_image.getVisibility() == View.GONE) mcv_image.setVisibility(View.VISIBLE);
        mtv_filenamne.setText(foFile.getName());
        mtv_path.setText(foFile.getAbsolutePath());

    }

    private void ViewDocument(File foFile){

        try {

            // Use FileProvider to get a safe content URI
            Uri uri = FileProvider.getUriForFile(
                    FileViewer.this,
                    FileViewer.this.getPackageName() + ".provider",
                    foFile
            );

            Intent loIntent = new Intent(Intent.ACTION_VIEW);
            loIntent.setData(uri);
            loIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(loIntent, "Open with"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(FileViewer.this, "No application found to open this file", Toast.LENGTH_SHORT).show();
        }

    }

    private void InitDataReceiver(){

        if (!getIntent().hasExtra("sSourceCd")){
            Toast.makeText(this, "Source code not initialized", Toast.LENGTH_SHORT).show();
            return;
        }else if (!getIntent().hasExtra("sSourceNo")){
            Toast.makeText(this, "Source number not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        mViewModel.GetAttachments(getIntent().getStringExtra("sSourceCd"), getIntent().getStringExtra("sSourceNo")).observe(this, new Observer<List<EImageInfo>>() {
            @Override
            public void onChanged(List<EImageInfo> eImageInfos) {

                if (eImageInfos == null){
                    return;
                }

                loAdapter = new Adapter_File_Viewer(FileViewer.this, eImageInfos, new Adapter_File_Viewer.OnAttachment() {
                    @Override
                    public void OnImageView(EImageInfo loAttachment) {

                        File loFile = new File(loAttachment.getFileLoct() + loAttachment.getImageNme());
                        if ((loFile.exists() && loFile.isFile() && loFile.canRead())){
                            DisplayImage(loFile);
                        }else {
                            DownloadFile(loAttachment.getTransNox());
                        }
                    }

                    @Override
                    public void OnDocument(EImageInfo loAttachment) {

                        File loFile = new File(loAttachment.getFileLoct() + loAttachment.getImageNme());
                        if ((loFile.exists() && loFile.isFile() && loFile.canRead())){
                            ViewDocument(loFile);
                        }else {
                            DownloadFile(loAttachment.getTransNox());
                        }
                    }
                });

                rcv_list.setAdapter(loAdapter);
                rcv_list.setLayoutManager(new LinearLayoutManager(FileViewer.this,  LinearLayoutManager.VERTICAL, false));
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void InitListener(){

        tie_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (loAdapter == null) return;
                loAdapter.GetFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btn_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //initialize pop up object, menu object holder
                PopupMenu loMenu = new PopupMenu(FileViewer.this, view);
                loMenu.getMenuInflater().inflate(R.menu.menu_attachment_filter, loMenu.getMenu());
                loMenu.show();

                loMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if (menuItem.getItemId() == R.id.action_filter_date){

                            MaterialDatePicker.Builder<Pair<Long, Long>> loBuilder = MaterialDatePicker.Builder.dateRangePicker();
                            loBuilder.setTitleText("Select Date Range");

                            MaterialDatePicker<Pair<Long, Long>> loPicker = loBuilder.build();
                            loPicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                                @Override
                                public void onPositiveButtonClick(Pair<Long, Long> selection) {

                                    //set date range parameters for downloading history
                                    lsDfrom = mViewModel.FormatLongData(selection.first);
                                    lsDto = mViewModel.FormatLongData(selection.second);

                                    InitDataReceiver();

                                }
                            });
                            loPicker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");
                            return true;

                        }else if (menuItem.getItemId() == R.id.action_download){

                           DownloadAttachments();
                            return true;

                        }
                        return false;
                    }
                });

            }
        });

        siv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_info.setVisibility(layout_info.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });

        mtv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mcv_image.getVisibility() == View.VISIBLE) mcv_image.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class VMFileViewer extends AndroidViewModel{

        @SuppressLint("StaticFieldLeak")
        private final Context poInstance;
        private final ConnectionUtil poConnection;
        private final ApprovalCode poApproval;
        private final FileUtility loFile;
        private final RImageInfo loImageSys;
        private final DImageInfo loImage;

        private String lomessage;

        public interface  OnTransaction{
            void OnLoad(String fsTitle, String fsMessage);
            void OnSuccess();
            void OnFailed(String fsMessage);
        }

        public VMFileViewer(@NonNull Application application) {
            super(application);

            this.poInstance = application;
            this.poConnection = new ConnectionUtil(application);
            this.poApproval = new ApprovalCode(application);
            this.loImageSys = new RImageInfo(application);
            this.loFile = new FileUtility(application);

            this.loImage = GGC_GCircleDB.getInstance(application).ImageInfoDao();
        }

        public void ImportAttachments(String fsSourceCd, String fsSourceNo, OnTransaction callback){

            TaskExecutor.Execute(null, new OnTaskExecuteListener() {
                @Override
                public void OnPreExecute() {
                    callback.OnLoad("CAS Attachments", "Downloading attachments. Please wait . .");
                }

                @Override
                public Object DoInBackground(Object args) {

                    if (!poConnection.isDeviceConnected()){
                        lomessage = poConnection.getMessage();
                        return false;
                    }

                    String lsDir = poInstance.getExternalFilesDir(null).getAbsolutePath() + "/CASApproval/0032/" + fsSourceNo + "/";
                    if (!loFile.IsFileExist(lsDir)){

                        if (!loFile.CreateDirectory(lsDir)){
                            lomessage = "Unable to create directory";
                            return false;
                        }
                    }

                    if (poApproval.ImportCASAttachmentList(fsSourceCd, fsSourceNo)){
                        return true;
                    }
                    lomessage = poApproval.getMessage();
                    return false;
                }

                @Override
                public void OnPostExecute(Object object) {

                    if ((Boolean) object){
                        callback.OnSuccess();
                    }else {
                        callback.OnFailed(lomessage);
                    }
                }
            });
        }

        public void DownloadFile(String fsVal, OnTransaction foCallback){

            TaskExecutor.Execute(fsVal, new OnDoBackgroundTaskListener() {
                @Override
                public Object DoInBackground(Object args) {
                    return loImageSys.DownloadImageSource(fsVal);
                }

                @Override
                public void OnPostExecute(Object object) {
                    if (!(Boolean) object){
                        foCallback.OnFailed(loImageSys.getMessage());
                    }else {
                        foCallback.OnSuccess();
                    }
                }
            });
        }

        public LiveData<List<EImageInfo>> GetAttachments(String fsSourceCd, String fsSourceNo){
            return loImage.GetTransactionAttachments(fsSourceCd, fsSourceNo);
        }

        public String FormatLongData(Long fsParam){

            return new SimpleDateFormat("yyyy-MM-dd")
                    .format(fsParam);

        }

    }
}