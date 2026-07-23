package org.rmj.g3appdriver.etc.FileViewer.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.room.Entities.EImageInfo;
import org.rmj.g3appdriver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Adapter_File_Viewer extends RecyclerView.Adapter<Adapter_File_Viewer.VH_File_Viewer>{

    private final Context loInstance;
    private final List<EImageInfo> llaImage;
    private final OnAttachment loCallback;
    private final Adapter_Attachment_Filter loFilter;

    private List<EImageInfo> llaImageFiltered;

    public Adapter_Attachment_Filter GetFilter(){
        return loFilter;
    }

    public interface OnAttachment{
        void OnImageView(EImageInfo loAttachment);
        void OnDocument(EImageInfo loAttachment);
    }

    public Adapter_File_Viewer(Context foInstance, List<EImageInfo> faAttachment, OnAttachment foCallback){

        loInstance = foInstance;
        llaImage = faAttachment;
        loCallback = foCallback;

        llaImageFiltered = llaImage;
        loFilter = new Adapter_Attachment_Filter(this);
    }

    @NonNull
    @Override
    public VH_File_Viewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VH_File_Viewer(
                LayoutInflater.from(loInstance).inflate(
                        R.layout.list_adapter_attachments,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH_File_Viewer holder, int position) {

        try {

            File loFile = new File(llaImageFiltered.get(position).getFileLoct() + llaImageFiltered.get(position).getImageNme());

            holder.mtv_filename.setText(loFile.getName());
            holder.mtv_filepath.setText(loFile.getAbsolutePath());

            // Use FileProvider to get a safe content URI
            Uri uri = FileProvider.getUriForFile(
                    holder.itemView.getContext(),
                    holder.itemView.getContext().getPackageName() + ".provider",
                    loFile
            );

            //display image
            Glide.with(holder.itemView.getContext())
                    .asBitmap() // force bitmap decoding
                    .load(!(loFile.getName().contains(".png") || loFile.getName().contains(".jpg")) ? R.drawable.baseline_insert_drive_file_24 : uri)  // use safe URI instead of raw path
                    .apply(new RequestOptions()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE))
                    .placeholder(R.drawable.baseline_error_24)
                    .error(R.drawable.baseline_error_24)
                    .into(holder.siv_icon);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if ((loFile.getName().contains(".png") || loFile.getName().contains(".jpg"))){
                        loCallback.OnImageView(llaImage.get(position));
                    }else {
                        loCallback.OnDocument(llaImage.get(position));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return llaImageFiltered.size();
    }

    public class Adapter_Attachment_Filter extends Filter {

        private final Adapter_File_Viewer loAdapter;

        public Adapter_Attachment_Filter(Adapter_File_Viewer foAdapter){
            loAdapter = foAdapter;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            if (charSequence.length() < 1){
                llaImageFiltered = llaImage;
            }else {

                List<EImageInfo> filterSearch = new ArrayList<>();

                //first filter, via search text
                for (EImageInfo loAttachment : llaImage){

                    if (loAttachment.getImageNme().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterSearch.add(loAttachment);
                    }
                }

                llaImageFiltered = filterSearch;
            }

            FilterResults loResults = new FilterResults();
            loResults.values = llaImageFiltered;
            loResults.count = llaImageFiltered.size();

            return loResults;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            llaImageFiltered = (List<EImageInfo>) filterResults.values;
            loAdapter.notifyDataSetChanged();
        }
    }

    public static class VH_File_Viewer extends RecyclerView.ViewHolder{

        private View view;
        private ShapeableImageView siv_icon;
        private MaterialTextView mtv_filename, mtv_filepath;

        public VH_File_Viewer(@NonNull View itemView) {
            super(itemView);

            view = itemView;
            siv_icon = itemView.findViewById(R.id.siv_icon);
            mtv_filename = itemView.findViewById(R.id.mtv_filename);
            mtv_filepath = itemView.findViewById(R.id.mtv_filepath);

        }
    }
}
