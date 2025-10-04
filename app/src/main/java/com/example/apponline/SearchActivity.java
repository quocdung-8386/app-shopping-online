package com.example.apponline;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apponline.Adapters.ProductAdapter;
import com.example.apponline.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearchInput;
    private ImageButton btnBack, btnClearSearch;
    private RecyclerView rvSearchResults;
    private TextView tvNoResults;

    private FirebaseFirestore db;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        db = FirebaseFirestore.getInstance();

        etSearchInput = findViewById(R.id.etSearchInput);
        btnBack = findViewById(R.id.btnBack);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        tvNoResults = findViewById(R.id.tvNoResults);
        productList = new ArrayList<>();
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, productList);
        rvSearchResults.setAdapter(productAdapter);
        btnBack.setOnClickListener(v -> finish());
        btnClearSearch.setOnClickListener(v -> etSearchInput.setText(""));
        etSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);

                if (s.length() >= 3) {
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    productList.clear();
                    productAdapter.notifyDataSetChanged();
                    tvNoResults.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearchInput.getText().toString());
                return true;
            }
            return false;
        });
    }
    private void performSearch(String queryText) {
        if (queryText.trim().isEmpty()) {
            return;
        }
        String searchKey = queryText.substring(0, 1).toUpperCase() + queryText.substring(1);

        db.collection("products")
                .whereGreaterThanOrEqualTo("name", searchKey)
                .whereLessThanOrEqualTo("name", searchKey + '\uf8ff')
                .limit(50)
                .get()
                .addOnCompleteListener(task -> {
                    productList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            product.setId(document.getId());

                            productList.add(product);
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                    if (productList.isEmpty()) {
                        tvNoResults.setVisibility(View.VISIBLE);
                        rvSearchResults.setVisibility(View.GONE);
                    } else {
                        tvNoResults.setVisibility(View.GONE);
                        rvSearchResults.setVisibility(View.VISIBLE);
                    }
                });
    }
}