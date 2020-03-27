package co.uk.depotnet.onsa.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.ItemListAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class KeywordListActivity extends AppCompatActivity  {

    private ItemListAdapter adapter;
    private ArrayList<ItemType> keywords;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_keyword);

        Intent intent = getIntent();
        ArrayList<ItemType> selectedKeywords = intent.getParcelableArrayListExtra("keywords");
        keywords = new ArrayList<>();

        keywords = DBHandler.getInstance().getItemTypes(DatasetResponse.DBTable.jobCategories);

        adapter = new ItemListAdapter(this , keywords , selectedKeywords , true);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SearchView searchView = findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("keywords", adapter.getSelectedKeywords());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        searchView.setIconified(true);



        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(true);

        EditText searchEditText = (EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        ImageView searchMagIcon = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.ic_search);
        ImageView closeMagIcon = (ImageView) searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeMagIcon.setImageResource(R.drawable.ic_close_white);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)){
                    adapter.update(keywords);
                    return true;
                }
                s = s.toLowerCase();
                ArrayList<ItemType> keys = new ArrayList<>();

                for (ItemType itemType : keywords){
                    String keyword = itemType.getDisplayItem().toLowerCase();
                    if(keyword.startsWith(s)){
                        keys.add(itemType);
                    }
                }

                if(keys.isEmpty()){
                    adapter.update(keywords);
                    return true;
                }


                adapter.update(keys);
                return true;
            }
        });


    }



}
