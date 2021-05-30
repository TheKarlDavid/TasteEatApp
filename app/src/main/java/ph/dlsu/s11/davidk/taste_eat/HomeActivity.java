package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import ph.dlsu.s11.davidk.taste_eat.adapter.CuisineAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.CuisineList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rv_list, rv_list_admin;
    private CuisineAdapter adapter;

    private LinearLayout ll_add_cuisine;
    private ImageView img_add_cuisine;
    private TextView tv_add_cuisine;

    private String str_role;


    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();

    }

    private void init(){

        rv_list = findViewById(R.id.rv_list);
        rv_list_admin = findViewById(R.id.rv_list_admin);
        ll_add_cuisine = findViewById(R.id.ll_add_cuisine);
        img_add_cuisine = findViewById(R.id.img_add_cuisine);
        tv_add_cuisine = findViewById(R.id.tv_add_cuisine);

        SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        str_role = sp.getString("role", "default");

        //query
        Query query = db.collection("cuisines").orderBy("Name", Query.Direction.ASCENDING);

        //recycler options
        FirestoreRecyclerOptions<CuisineList> cuisines = new FirestoreRecyclerOptions.Builder<CuisineList>()
                .setQuery(query, CuisineList.class)
                .build();

        adapter = new CuisineAdapter(cuisines);

        if(str_role.equalsIgnoreCase("admin")){ //if user is admin
            ll_add_cuisine.setVisibility(View.VISIBLE);

            rv_list.setVisibility(View.GONE);
            rv_list_admin.setVisibility(View.VISIBLE);

            rv_list_admin.setHasFixedSize(true);
            rv_list_admin.setLayoutManager(new LinearLayoutManager(this));
            rv_list_admin.setAdapter(adapter);

            navbarAdmin();

            ll_add_cuisine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    img_add_cuisine.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
                    tv_add_cuisine.setTextColor(getResources().getColor(R.color.colorPrimary));

                    Intent intent = new Intent(getApplicationContext(), AddCuisineActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

        }
        else{ // if regular user
            rv_list.setVisibility(View.VISIBLE);
            rv_list_admin.setVisibility(View.GONE);

            rv_list.setHasFixedSize(true);
            rv_list.setLayoutManager(new LinearLayoutManager(this));
            rv_list.setAdapter(adapter);

            navbar();
        }


    }

    private void navbar(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNavAdmin = findViewById(R.id.bottom_navigation_admin);

        bottomNav.setVisibility(View.VISIBLE);
        bottomNavAdmin.setVisibility(View.GONE);

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
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(),
                                AccountActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    private void navbarAdmin(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNavAdmin = findViewById(R.id.bottom_navigation_admin);

        bottomNav.setVisibility(View.GONE);
        bottomNavAdmin.setVisibility(View.VISIBLE);

        bottomNavAdmin.setSelectedItemId(R.id.nav_menu_book);
        bottomNavAdmin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_book:
                        return true;

                    case R.id.nav_suggestion:
                        startActivity(new Intent(getApplicationContext(),
                                SuggestionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(),
                                AccountActivity.class));
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