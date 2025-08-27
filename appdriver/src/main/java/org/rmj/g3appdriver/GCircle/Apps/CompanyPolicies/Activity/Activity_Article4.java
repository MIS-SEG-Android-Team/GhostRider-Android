package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.ViolationAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Violation;
import org.rmj.g3appdriver.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Activity_Article4 extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private TextInputEditText searchView;
    private ViolationAdapter adapter;
    private List<Violation> fullViolationList;
    private boolean isExpanded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article4);

        searchView = findViewById(R.id.searchView);
        expandableListView = findViewById(R.id.expandableListView);


        fullViolationList = loadViolations(this);
        adapter = new ViolationAdapter(this, fullViolationList);
        expandableListView.setAdapter(adapter);

        MaterialTextView readMore = findViewById(R.id.readMore);
        MaterialTextView description = findViewById(R.id.description);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                adapter.setSearchQuery(query);
                filter(query);

            }
        });
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

    public List<Violation> loadViolations(Context context) {
        List<Violation> violations = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("article4.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            JSONArray jsonArray = new JSONArray(builder.toString());
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

                violations.add(new Violation(name, actions));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return violations;
    }
}
