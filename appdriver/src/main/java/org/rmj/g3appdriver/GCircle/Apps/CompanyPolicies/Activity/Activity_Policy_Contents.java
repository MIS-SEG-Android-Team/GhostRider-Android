package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.PolicyAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.itemAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Item;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.PolicySection;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleHead;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.ArrayList;
import java.util.List;

public class Activity_Policy_Contents extends AppCompatActivity {

    private MaterialTextView titles, description;
    private TextInputEditText searchView;
    private RecyclerView recyclerView;
    private PolicyAdapter loAdapter;
    private itemAdapter adapter;
    private List<Item> itemList;
    private List<PolicySection> laPolicyContents;
    private VMGuide mViewModel;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    private String sMenuTitle, sMenuIDxx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_policy_contents);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        sMenuTitle = getIntent().getStringExtra("sMenuTitle");
        sMenuIDxx = getIntent().getStringExtra("sMenuIDxx");

        mViewModel = new ViewModelProvider(this).get(VMGuide.class);
        poLoad = new LoadDialog(this);
        poMessage = new MessageBox(this);

        titles = findViewById(R.id.titles);
        description = findViewById(R.id.description);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        itemList = new ArrayList<>();

        poLoad.initDialog("Guanzon Circle", "Loading policy contents . . .", false);
        poLoad.show();

        poMessage.initDialog();
        poMessage.setTitle("Guanzon Circle");
        poMessage.setPositiveButton("Okay", new MessageBox.DialogButton() {
            @Override
            public void OnButtonClick(View view, AlertDialog dialog) {
                dialog.dismiss();
                finish();
            }
        });

        Spanned htmlTitle;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlTitle = Html.fromHtml(sMenuTitle, Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlTitle = Html.fromHtml(sMenuTitle);
        }

        titles.setText(htmlTitle);

        if (getIntent().hasExtra("sMenuDescription")){
            description.setVisibility(View.VISIBLE);
            description.setText(getIntent().getStringExtra("sMenuDescription"));
        }else {
            description.setVisibility(View.GONE);
        }

        switch (sMenuIDxx){
            case "001":
                initSummaryDefinitionContents();
                break;
            case "002":
                initSummaryDefinitionContents();
                break;
            case "003":
                initArticles();
                break;
            default:
                poMessage.setIcon(R.drawable.baseline_message_24);
                poMessage.setMessage("No policy contents found");
                poMessage.show();
                break;
        }

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (laPolicyContents != null){
                    loAdapter.setSearchQuery(searchView.getText().toString());
                    filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void initSummaryDefinitionContents(){

        mViewModel.GetPolicyContents(sMenuIDxx).observe(this, ePolicyContents -> {
            if (ePolicyContents != null) {

                try {

                    if (ePolicyContents.size() < 1){

                        poLoad.dismiss();

                        poMessage.setIcon(R.drawable.baseline_message_24);
                        poMessage.setMessage("No policy contents found");
                        poMessage.show();

                        return;
                    }

                    laPolicyContents = new ArrayList<>();
                    for (int i = 0; i < ePolicyContents.size(); i++) {
                        try {

                            JSONArray laItems = new JSONArray(ePolicyContents.get(i).sContentxx);
                            for (int ctr = 0; ctr < laItems.length(); ctr++){
                                JSONObject loItem = laItems.getJSONObject(ctr);

                                PolicySection policyContent = new PolicySection();
                                policyContent.title = loItem.getString("title");
                                policyContent.description = loItem.getString("description");

                                laPolicyContents.add(policyContent);
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                            break;
                        }
                        Thread.sleep(1000);
                    }

                    poLoad.dismiss();

                    loAdapter = new PolicyAdapter(this, laPolicyContents);
                    recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
                    recyclerView.setAdapter(loAdapter);

                }catch (Exception e){
                    poLoad.dismiss();

                    poMessage.setIcon(R.drawable.baseline_error_24);
                    poMessage.setMessage("Error loading policy contents");
                    poMessage.show();

                    e.printStackTrace();
                }

            }else {
                poLoad.dismiss();

                poMessage.setIcon(R.drawable.baseline_message_24);
                poMessage.setMessage("No policy contents found");
                poMessage.show();
            }
        });

    }
    private void initArticles(){

        mViewModel.GetArticles().observe(Activity_Policy_Contents.this, new Observer<List<EArticleHead>>() {
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

                    adapter = new itemAdapter(Activity_Policy_Contents.this, itemList, new itemAdapter.OnViewArticle() {
                        @Override
                        public void OnViewSubtitle(String subTitlexx) {
                            promptSubtitle(subTitlexx);
                        }

                        @Override
                        public void OmViewArticle(String sCodexx, String sTitlexx, int sImagexx, String sSubTitlexx) {
                            Intent loIntent = new Intent(Activity_Policy_Contents.this, Activity_Article_Details.class);
                            loIntent.putExtra("sCodexx", sCodexx);
                            loIntent.putExtra("sTitlexx", sTitlexx);
                            loIntent.putExtra("sSubTitlexx", sSubTitlexx);
                            loIntent.putExtra("sImage", sImagexx);
                            startActivity(loIntent);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(Activity_Policy_Contents.this, 2));

                    poLoad.dismiss();
                }else {
                    poLoad.dismiss();

                    poMessage.setIcon(R.drawable.baseline_message_24);
                    poMessage.setMessage("No Articles Found");
                    poMessage.show();
                }
            }
        });
    }
    private void filter(String text) {
        List<PolicySection> filteredList = new ArrayList<>();
        for (PolicySection item : laPolicyContents) {
            if (item.title.toLowerCase().contains(text.toLowerCase())||item.description.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        loAdapter.updateList(filteredList);
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