package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.PolicyAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.PolicySection;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.ArrayList;
import java.util.List;

public class Activity_Policy_Contents extends AppCompatActivity {

    private MaterialTextView titles;
    private TextInputEditText searchView;
    private RecyclerView recyclerView;
    private PolicyAdapter loAdapter;
    private List<PolicySection> laPolicyContents;
    private VMGuide mViewModel;
    private LoadDialog poLoad;
    private MessageBox poMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_policy_contents);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        String sMenuTitle = getIntent().getStringExtra("sMenuTitle");
        String sMenuIDxx = getIntent().getStringExtra("sMenuIDxx");

        mViewModel = new ViewModelProvider(this).get(VMGuide.class);
        poLoad = new LoadDialog(this);
        poMessage = new MessageBox(this);

        titles = findViewById(R.id.titles);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

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

        titles.setText(sMenuTitle);

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

    private void filter(String text) {
        List<PolicySection> filteredList = new ArrayList<>();
        for (PolicySection item : laPolicyContents) {
            if (item.title.toLowerCase().contains(text.toLowerCase())||item.description.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        loAdapter.updateList(filteredList);
    }

}