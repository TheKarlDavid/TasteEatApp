package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ph.dlsu.s11.davidk.taste_eat.adapter.RecipesLikedAdapter;
import ph.dlsu.s11.davidk.taste_eat.adapter.RecipesSavedAdapter;
import ph.dlsu.s11.davidk.taste_eat.helper.MyItemTouchHelperCallback;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class LikedActivity extends AppCompatActivity {

    private RecyclerView rv_list;
    private RecipesLikedAdapter adapter;
    private ArrayList<Recipes> likedRecipeList = new ArrayList<>();
    private TextView tv_no_saved;

    private ItemTouchHelper itemTouchHelper;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        navbar();
        init();
    }

    private void init(){
        tv_no_saved = findViewById(R.id.tv_no_saved);

        rv_list = findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        SharedPreferences sharedPreferences = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        String str_user_email = sharedPreferences.getString("user", "default");

        db.collection("likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                Log.d("TAG", document.getId() + " => " + document.getString("email"));
                                Log.d("TAG", document.getId() + "STR => " + str_user_email);

                                if(str_user_email.equals(document.getString("email"))){
                                    String temp[] = document.getString("recipes").split(",");

                                    //search db for recipes that were liked
                                    for(int i =0; i<temp.length; i++){
                                        db.collection("recipes").whereEqualTo("name", temp[i]).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String str_name = document.getString("name");
                                                                String str_image = document.getString("Image");
                                                                String str_ingredients = document.getString("ingredients");
                                                                String str_cuisine = document.getString("cuisine");
                                                                String str_instructions = document.getString("instructions");
                                                                String str_meal = document.getString("meal");
                                                                int likes = document.getLong("likes").intValue();

                                                                Recipes recipe = new Recipes(str_name, str_image, str_ingredients, str_cuisine, str_instructions, str_meal, likes);
                                                                likedRecipeList.add(recipe);

                                                                Log.d("TAG", "RECP BM: "+ recipe.getName() + " " + recipe.getImage() + " "+likedRecipeList);

//                                                                adapter = new RecipesLikedAdapter(getApplicationContext(), likedRecipeList);
                                                                adapter = new RecipesLikedAdapter(getApplicationContext(), likedRecipeList, viewHolder -> {
                                                                    itemTouchHelper.startDrag(viewHolder);
                                                                });
                                                                rv_list.setAdapter(adapter);

                                                                ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(adapter);
                                                                itemTouchHelper = new ItemTouchHelper(callback);
                                                                itemTouchHelper.attachToRecyclerView(rv_list);

                                                            }
                                                        } else {
                                                            Log.d("TAG", "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }

                                }



                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void navbar(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_favorites);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_book:
                        startActivity(new Intent(getApplicationContext(),
                                HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_bookmark:
                        startActivity(new Intent(getApplicationContext(),
                                SavedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_favorites:
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
}