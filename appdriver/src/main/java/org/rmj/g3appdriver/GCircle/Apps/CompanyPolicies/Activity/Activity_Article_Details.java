package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.ViolationAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Violation;
import org.rmj.g3appdriver.GCircle.Apps.User_Guide.ViewModel.VMGuide;
import org.rmj.g3appdriver.GCircle.room.Entities.EArticleDetails;
import org.rmj.g3appdriver.R;
import org.rmj.g3appdriver.etc.LoadDialog;
import org.rmj.g3appdriver.etc.MessageBox;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Activity_Article_Details extends AppCompatActivity {
    private VMGuide mViewModel;
    private MaterialTextView readMore, titles, description;
    private ImageView cardImage;
    private ExpandableListView expandableListView;
    private TextInputEditText searchView;
    private ViolationAdapter adapter;
    private List<Violation> fullViolationList;
    private LoadDialog poDialog;
    private MessageBox poMessage;

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_details);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
        );

        mViewModel = new ViewModelProvider(this).get(VMGuide.class);
        poDialog = new LoadDialog(this);
        poMessage = new MessageBox(this);

        searchView = findViewById(R.id.searchView);
        expandableListView = findViewById(R.id.expandableListView);
        readMore = findViewById(R.id.readMore);
        titles = findViewById(R.id.titles);
        description = findViewById(R.id.description);
        cardImage = findViewById(R.id.cardImage);

        String sCodexx = getIntent().getStringExtra("sCodexx");
        String sTitle = getIntent().getStringExtra("sTitlexx");
        String sSubTitlexx = getIntent().getStringExtra("sSubTitlexx");

        int sImage = getIntent().getIntExtra("sImage", R.drawable.kayscope);

        poDialog.initDialog("Guanzon Circle", "Loading policy contents . . .", false);
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

        titles.setText(sTitle);
        cardImage.setImageResource(sImage);
        description.setText(sSubTitlexx);

        mViewModel.GetArticleDetails(sCodexx).observe(Activity_Article_Details.this, new Observer<List<EArticleDetails>>() {
            @Override
            public void onChanged(List<EArticleDetails> eArticleDetails) {

                if (eArticleDetails != null){

                    try {

                        fullViolationList = new ArrayList<>();
                        for (EArticleDetails article : eArticleDetails){

                            try {
                                JSONArray jsonArray = new JSONArray(article.getsContentxx());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    String name = obj.getString("name");
                                    JSONObject actionsObj = obj.getJSONObject("actions");

                                    Map<String, String> actions = new LinkedHashMap<>();
                                    Iterator<String> keys = actionsObj.keys();
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        actions.put(key, actionsObj.getString(key));
                                    }
                                    fullViolationList.add(new Violation(name, actions));
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                break;
                            }
                            Thread.sleep(1000);
                        }

                        poDialog.dismiss();

                        adapter = new ViolationAdapter(Activity_Article_Details.this, fullViolationList);
                        expandableListView.setAdapter(adapter);

                    } catch (Exception e) {
                        poDialog.dismiss();

                        poMessage.setIcon(R.drawable.baseline_error_24);
                        poMessage.setMessage("Error loading policy contents");
                        poMessage.show();

                        e.printStackTrace();
                    }
                }else {
                    poDialog.dismiss();

                    poMessage.setIcon(R.drawable.baseline_message_24);
                    poMessage.setMessage("No policy contents found");
                    poMessage.show();
                }
            }
        });

        readMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            if (isExpanded) {
                description.setMaxLines(Integer.MAX_VALUE);
                readMore.setText("Read Less");
            } else {
                description.setMaxLines(3);
                readMore.setText("Read More");
            }});

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                adapter.setSearchQuery(query);
                filter(query);
            }});
    }

    private void filter(String text) {
        List<Violation> filteredList = new ArrayList<>();
        for (Violation violation : fullViolationList) {
            if (violation.getName().toLowerCase().contains(text.toLowerCase())||violation.getActions().toString().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(violation);
            }
        }
        adapter.updateList(filteredList);
    }
}
