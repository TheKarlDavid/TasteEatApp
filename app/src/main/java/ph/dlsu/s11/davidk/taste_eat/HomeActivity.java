package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import ph.dlsu.s11.davidk.taste_eat.adapter.CuisineAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.CuisineList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rv_list;
    private CuisineAdapter adapter;

    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

        navbar();
    }

    private void init(){
        rv_list = findViewById(R.id.rv_list);

        //query
        Query query = db.collection("cuisines");

        //recycler options
        FirestoreRecyclerOptions<CuisineList> cuisines = new FirestoreRecyclerOptions.Builder<CuisineList>()
                .setQuery(query, CuisineList.class)
                .build();

        adapter = new CuisineAdapter(cuisines);

        rv_list.setHasFixedSize(true);
        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setAdapter(adapter);


    }

    private void navbar(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_menu_book);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_book:
                        return true;

                    case R.id.nav_bookmark:
                        startActivity(new Intent(getApplicationContext(),
                                SavedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_favorites:
                        startActivity(new Intent(getApplicationContext(),
                                LikedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}