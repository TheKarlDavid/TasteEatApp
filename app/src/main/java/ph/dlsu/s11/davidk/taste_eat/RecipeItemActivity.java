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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class RecipeItemActivity extends AppCompatActivity {

    private TextView tv_name, tv_likes, tv_ingredients, tv_instructions;
    private ImageView img_recipe, img_like, img_save, img_ingredients, img_instructions;

    private Intent intent = getIntent();
    private String str_name, str_ingredients, str_instructions, str_img_recipe;
    private int likes, instructionsVisible = 0, ingredientsVisible = 0;


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

//        SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
//        String user = sp.getString("user", "default");
//
//        Log.d("TAG_USER", "User is:" +user);

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