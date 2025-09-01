package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.itemAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Item;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.ArrayList;
import java.util.List;

public class Activity_MainArticle extends AppCompatActivity {
    private TextInputEditText searchView;
    private RecyclerView recyclerView;
    private itemAdapter adapter;
    private List<Item> itemList;
    private VMGuide mViewModel;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainarticle);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        mViewModel = new ViewModelProvider(this).get(VMGuide.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);

        recyclerView = findViewById(R.id.recyclerViewArticles);
        searchView = findViewById(R.id.searchView);
        itemList = new ArrayList<>();

        poDialog.initDialog("Guanzon Circle", "Loading Articles...", false);
        poDialog.show();

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Circle");
        poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
                finish();
            }
        });

        mViewModel.GetArticles().observe(Activity_MainArticle.this, new Observer<List<EArticleHead>>() {
            @Override
            public void onChanged(List<EArticleHead> eArticleHeads) {
                if (eArticleHeads != null){

                    if (eArticleHeads.size() < 1){
                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setMessage("No Articles Found");
                        poMessage.show();
                    }

                    for (EArticleHead eArticleHead : eArticleHeads) {

                        Item item = new Item();
                        item.sCodexx = eArticleHead.getsCodexx();
                        item.image = eArticleHead.getsImage();
                        item.title = eArticleHead.getsTitle();
                        item.description = eArticleHead.getsDescription();
                        item.subtitle = eArticleHead.getsSubtitle();

                        itemList.add(item);
                    }

                    adapter = new itemAdapter(Activity_MainArticle.this, itemList, new itemAdapter.OnViewArticle() {
                        @Override
                        public void OnViewSubtitle(String subTitlexx) {
                            promptSubtitle(subTitlexx);
                        }

                        @Override
                        public void OmViewArticle(String sCodexx, String sTitlexx, int sImagexx, String sSubTitlexx) {
                            Intent loIntent = new Intent(Activity_MainArticle.this, Activity_Article_Details.class);
                            loIntent.putExtra("sCodexx", sCodexx);
                            loIntent.putExtra("sTitlexx", sTitlexx);
                            loIntent.putExtra("sSubTitlexx", sSubTitlexx);
                            loIntent.putExtra("sImage", sImagexx);
                            startActivity(loIntent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(Activity_MainArticle.this, 2));

                    poDialog.dismiss();
                }else {
                    poDialog.dismiss();

                    poMessage.setIcon(R.drawable.baseline_message_24);
                    poMessage.setMessage("No Articles Found");
                    poMessage.show();
                }
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
    private void filter(String text) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.title.toLowerCase().contains(text.toLowerCase())||item.description.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private void promptSubtitle(String subtitle){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.custom_layout, null);

        builder.setView(dialogView);
        MaterialTextView dialogText = dialogView.findViewById(R.id.dialog_text);
        dialogText.setText(subtitle);
        AlertDialog alertDialog = builder.create();

        // Optional: Make it non-cancelable
        alertDialog.setCancelable(false);

        // Set button click listener
        MaterialButton button = dialogView.findViewById(R.id.dialog_button);
        button.setOnClickListener(view -> alertDialog.dismiss());
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Remove default corners
        alertDialog.show();
    }
}