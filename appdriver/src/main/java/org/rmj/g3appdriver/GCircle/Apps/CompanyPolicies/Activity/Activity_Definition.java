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

import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.DefinitionAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.DefinitionSection;
import org.rmj.g3appdriver.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Activity_Definition extends AppCompatActivity {
    RecyclerView recyclerView;
    DefinitionAdapter adapter;
    List<DefinitionSection> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);
        recyclerView = findViewById(R.id.recyclerView);
        TextInputEditText searchView = findViewById(R.id.searchView);
        searchView.setBackground(null);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        itemList = loadJsonData();
        adapter = new DefinitionAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.setSearchQuery(searchView.getText().toString());

                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void filter(String text) {
        List<DefinitionSection> filteredList = new ArrayList<>();
        for (DefinitionSection item : itemList) {
            if (item.title.toLowerCase().contains(text.toLowerCase())||item.description.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateList(filteredList);
    }

    private List<DefinitionSection> loadJsonData() {
        List<DefinitionSection> items = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("disciplinary.json");
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.beginArray();
            while (reader.hasNext()) {
                DefinitionSection item = new DefinitionSection();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("title")) item.title = reader.nextString();
                    else if (name.equals("description")) item.description = reader.nextString();
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