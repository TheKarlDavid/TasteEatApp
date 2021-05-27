package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class RecipeItemActivity extends AppCompatActivity {

    private TextView tv_name, tv_likes, tv_ingredients, tv_instructions;
    private ImageView img_recipe, img_like, img_save, img_ingredients, img_instructions;

    private Intent intent = getIntent();
    private String str_name, str_ingredients, str_instructions, str_img_recipe, strId, temp;
    private int likes, instructionsVisible = 0, ingredientsVisible = 0, isSaved = 0;

    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_item);

        init();

        navbar();
    }

    private void init() {

        tv_name = findViewById(R.id.tv_name);
        tv_likes = findViewById(R.id.tv_likes);
        tv_ingredients = findViewById(R.id.tv_ingredients);
        tv_instructions = findViewById(R.id.tv_instructions);

        img_recipe = findViewById(R.id.img_recipe);
        img_like = findViewById(R.id.img_like);
        img_save = findViewById(R.id.img_save);
        img_ingredients = findViewById(R.id.img_ingredients);
        img_instructions = findViewById(R.id.img_instructions);

        str_name = getIntent().getStringExtra("name");
        str_ingredients = getIntent().getStringExtra("ingredients");
        str_instructions = getIntent().getStringExtra("instructions");
        str_img_recipe = getIntent().getStringExtra("image");
        likes = getIntent().getIntExtra("likes", 0);

        SharedPreferences sharedPreferences = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        String str_user_email = sharedPreferences.getString("user", null);

        tv_name.setText(str_name);
        Picasso.get().load(str_img_recipe).into(img_recipe);

        if(likes == 1){
            tv_likes.setText(String.valueOf(likes) + " like");
        }
        else if(likes > 1){
            tv_likes.setText(String.valueOf(likes) + " likes");
        }
        else{
            tv_likes.setVisibility(View.GONE);
        }

        db.collection("bookmarks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                //if user bookmarked
                                if(str_user_email.equals(document.getString("email"))){

                                    // if many bookmark
                                    String recipe[] = document.getString("recipes").split(",");
                                    for(int i=0; i<recipe.length; i++){
                                        if(str_name.equals(recipe[i])){
                                            img_save.setImageResource(R.drawable.ic_baseline_bookmark_24);
                                            isSaved = 1;
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

//        SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
//        String user = sp.getString("user", "default");
//
//        Log.d("TAG_USER", "User is:" +user);

        img_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_like.setImageResource(R.drawable.ic_baseline_favorite_24);

                Toast.makeText(getApplicationContext(), "LIKE CLICK" , Toast.LENGTH_SHORT).show();

            }
        });

        img_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSaved == 0){ //not yet saved, create new
                    img_save.setImageResource(R.drawable.ic_baseline_bookmark_24);

                    db.collection("bookmarks")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                            //if user bookmarked
                                            if(str_user_email.equals(document.getString("email"))){
                                                strId = document.getId();
                                                // if existing bookmark for user
                                                temp = "";
                                                if(document.getString("recipes").equals("")){
                                                    temp = temp.concat(str_name);
                                                }
                                                else{
                                                    temp = temp.concat(document.getString("recipes"));
                                                    temp = temp.concat(","+str_name);
                                                }

                                                //create new map object to send data to the db
                                                Map<String, Object> bookmark = new HashMap<>();

                                                //add data to map
                                                bookmark.put("email", str_user_email);
                                                bookmark.put("recipes", temp);

                                                //update
                                                db.collection("bookmarks")
                                                        .document(strId).set(bookmark);
                                                isSaved=1;
                                            }
                                        }

                                        if(isSaved == 0){
                                            //create new map object to send data to the db
                                            Map<String, Object> bookmark = new HashMap<>();

                                            //add data to map
                                            bookmark.put("email", str_user_email);
                                            bookmark.put("recipes", str_name);

                                            //add new document with generated ID
                                            db.collection("bookmarks")
                                                    .add(bookmark)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            //Execute when data is successfully inserted to database
                                                            Log.d("TAG", "inserted success");
                                                            isSaved=1;
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            //Execute when data is not successfully inserted to database
                                                            Log.d("TAG", "error insert fail");
                                                        }
                                                    });
                                        }

                                    } else {
                                        Log.w("TAG", "Error getting documents.", task.getException());
                                    }
                                }
                            });


                }
                else{ //unsave
                    img_save.setImageResource(R.drawable.ic_baseline_bookmark_border_24);

                    db.collection("bookmarks")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("TAG", document.getId() + " => " + document.getData());
                                            //if user bookmarked
                                            if(str_user_email.equals(document.getString("email"))){
                                                strId = document.getId();
                                                // if many bookmark
                                                String recipe[] = document.getString("recipes").split(",");
                                                temp = "";
                                                for(int i=0; i<recipe.length; i++){
                                                    if(!str_name.equals(recipe[i])){
                                                        if(temp.equals("")){
                                                            temp = temp.concat(recipe[i]);
                                                        }
                                                        else{
                                                            temp = temp.concat("," + recipe[i]);
                                                        }
                                                        Log.d("TAGS", "recipes: " +temp);
                                                    }
                                                }
                                            }
                                        }

                                        //create new map object to send data to the db
                                        Map<String, Object> bookmark = new HashMap<>();

                                        //add data to map
                                        bookmark.put("email", str_user_email);
                                        bookmark.put("recipes", temp);

                                        //update
                                        db.collection("bookmarks")
                                                .document(strId).set(bookmark);
                                        isSaved=0;
                                    } else {
                                        Log.w("TAG", "Error getting documents.", task.getException());
                                    }
                                }
                            });

                }



                Toast.makeText(getApplicationContext(), "SAVE CLICK" , Toast.LENGTH_SHORT).show();

            }
        });


        img_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ingredientsVisible == 0){
                    img_ingredients.setImageResource(R.drawable.ic_baseline_remove_24);

                    String ingredient[] = str_ingredients.split(",");
                    String ingredient_list = "";

                    for(int i=0; i<ingredient.length; i++){
                        if(i == 0){
                            ingredient_list = ingredient_list.concat(" "+ ingredient[i] + "\n");
                        }
                        else if(i+1 == ingredient.length){
                            ingredient_list = ingredient_list.concat(ingredient[i]);
                        }
                        else {
                            ingredient_list = ingredient_list.concat(ingredient[i] + "\n");
                        }

                    }

                    tv_ingredients.setText(ingredient_list);
//                    tv_ingredients.setText(str_ingredients);
                    tv_ingredients.setVisibility(View.VISIBLE);
                    ingredientsVisible = 1;
                }
                else{
                    img_ingredients.setImageResource(R.drawable.ic_baseline_add_24);
                    tv_ingredients.setVisibility(View.GONE);
                    ingredientsVisible = 0;
                }

            }
        });

        img_instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(instructionsVisible == 0){
                    img_instructions.setImageResource(R.drawable.ic_baseline_remove_24);

                    tv_instructions.setText(str_instructions);
                    tv_instructions.setVisibility(View.VISIBLE);
                    instructionsVisible = 1;
                }
                else{
                    img_instructions.setImageResource(R.drawable.ic_baseline_add_24);
                    tv_instructions.setVisibility(View.GONE);
                    instructionsVisible = 0;
                }

            }
        });




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