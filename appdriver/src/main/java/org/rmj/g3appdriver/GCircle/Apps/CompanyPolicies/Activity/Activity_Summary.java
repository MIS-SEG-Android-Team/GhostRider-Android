package org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Activity;

import android.os.Bundle;
import android.content.res.AssetManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.PolicyAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.PolicySection;
import org.rmj.g3appdriver.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Activity_Summary extends AppCompatActivity {
    RecyclerView recyclerView;
    PolicyAdapter adapter;
    List<PolicySection> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        recyclerView = findViewById(R.id.recyclerView);
        TextInputEditText searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        itemList = loadJsonData();
        adapter = new PolicyAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        // Optional: react to text changes
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setSearchQuery(searchView.getText().toString());

                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Initialize itemList and adapter here
    }
    private void filter(String text) {
        List<PolicySection> filteredList = new ArrayList<>();
        for (PolicySection item : itemList) {
            if (item.title.toLowerCase().contains(text.toLowerCase())||item.description.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private List<PolicySection> loadJsonData() {
        List<PolicySection> items = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("summary.json");
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.beginArray();
            while (reader.hasNext()) {
                PolicySection item = new PolicySection();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                     if (name.equals("title")) item.title = reader.nextString();
                    else if (name.equals("content")) item.description = reader.nextString();
                       else reader.skipValue();
                }
                reader.endObject();
                items.add(item);
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}