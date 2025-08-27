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
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Adapter.itemAdapter;
import org.rmj.g3appdriver.GCircle.Apps.CompanyPolicies.Model.Item;
import org.rmj.g3appdriver.R;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Activity_MainArticle extends AppCompatActivity {
    RecyclerView recyclerView;
    itemAdapter adapter;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainarticle);

        recyclerView = findViewById(R.id.recyclerViewArticles);
        TextInputEditText searchView = findViewById(R.id.searchView);

        itemList = loadJsonData();
        adapter = new itemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
    private List<Item> loadJsonData() {
        List<Item> items = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("menu.json");
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
            reader.beginArray();
            while (reader.hasNext()) {
                Item item = new Item();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("image")) item.image = reader.nextString();
                    else if (name.equals("title")) item.title = reader.nextString();
                    else if (name.equals("subtitle")) item.subtitle = reader.nextString();
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