package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText et_recipe_name, et_ingredients, et_instructions;
    private TextInputLayout textInputName, textInputIngredients, textInputInstructions, textInputCuisine, textInputMeal;
    private AutoCompleteTextView actv_cuisine, actv_meal;
    private TextView tv_error_image, tv_add_recipe;
    private ImageButton img_btn_recipe;

    private String str_name, str_ingredients, str_instructions;
    private Uri imageUri = null;
    private static final int GALLERY_REQUEST =1;

    private static final String[] MEALS = new String[]{"breakfast", "lunch", "dinner"};
    private ArrayList<String> cuisineList = new ArrayList<>();;
    private String[] cuisines;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

//        setArrayCuisine();
        init();
        setArrayCuisine();
        navbarAdmin();
    }

    private void init(){
        et_recipe_name = findViewById(R.id.et_recipe_name);
        et_ingredients = findViewById(R.id.et_ingredients);
        et_instructions = findViewById(R.id.et_instructions);
        actv_cuisine = findViewById(R.id.actv_cuisine);
        actv_meal = findViewById(R.id.actv_meal);

        textInputName = findViewById(R.id.textInputName);
        textInputIngredients = findViewById(R.id.textInputIngredients);
        textInputInstructions = findViewById(R.id.textInputInstructions);
        textInputCuisine = findViewById(R.id.textInputCuisine);
        textInputMeal = findViewById(R.id.textInputMeal);

        tv_error_image = findViewById(R.id.tv_error_image);
        tv_add_recipe = findViewById(R.id.tv_add_recipe);

        img_btn_recipe = findViewById(R.id.img_btn_recipe);

        str_name = et_recipe_name.getText().toString();
        str_ingredients = et_ingredients.getText().toString();
        str_instructions = et_instructions.getText().toString();

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        ArrayAdapter<String> adapterMeals = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MEALS);
        actv_meal.setAdapter(adapterMeals);

        img_btn_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        tv_add_recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageUri == null){
                    tv_error_image.setVisibility(View.VISIBLE);
                }
                else{
                    tv_error_image.setVisibility(View.GONE);
                }

                if(validateName() && validateIngredients() && validateInstructions() && validateCuisine() && validateMeal() && imageUri != null){
                    progressDialog.setMessage("Adding new recipe");
                    progressDialog.show();

                    Log.d("TAG", "ADD RECIPE :" + str_name);

//                    //return name of image
                    StorageReference filePath = storageReference.child("Recipes Images").child(str_name);

                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //create new map object to send data to the db
                                    Map<String, Object> recipe = new HashMap<>();

                                    //add data to map
                                    recipe.put("name", str_name);
                                    recipe.put("Image", uri.toString());
                                    recipe.put("ingredients", str_ingredients);
                                    recipe.put("instructions", str_instructions);
                                    recipe.put("cuisine", actv_cuisine.getText().toString());
                                    recipe.put("meal", actv_meal.getText().toString());
                                    recipe.put("likes", 0);


                                    //add new document with generated ID
                                    db.collection("recipes")
                                            .add(recipe)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("TAG", "successful insert");
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
                            });

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), str_name + " cuisine successfully addded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), RecipesActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                }
            }
        });

    }

    private void setArrayCuisine(){

        db.collection("cuisines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cuisineList.add(document.getString("Name"));
//                                Log.d("TAG", cuisineList.size() + " => " + document.getString("Name"));
                            }
                            cuisines = cuisineList.toArray(new String[0]);

                            ArrayAdapter<String> adapterCuisines = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, cuisines);
                            actv_cuisine.setAdapter(adapterCuisines);

//                            for (String s : cuisines) {
//                                Log.d("TAG", " FUNCTION => " + s);
//                            }

                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            img_btn_recipe.setImageURI(imageUri);
        }
    }

    private boolean validateName() {
        str_name = et_recipe_name.getText().toString();

        if (str_name.isEmpty() || str_name.equals(null) || str_name.equals(" ")) {
            textInputName.setError("Please enter a recipe name");
            return false;
        }
        else {
            textInputName.setError(null);
            return true;
        }
    }

    private boolean validateIngredients() {
        str_ingredients = et_ingredients.getText().toString();

        if (str_ingredients.isEmpty() || str_ingredients.equals(null) || str_ingredients.equals(" ")) {
            textInputIngredients.setError("Please enter ingredients for dish");
            return false;
        }
        else {
            textInputIngredients.setError(null);
            return true;
        }
    }

    private boolean validateInstructions() {
        str_instructions = et_instructions.getText().toString();

        if (str_instructions.isEmpty() || str_instructions.equals(null) || str_instructions.equals(" ")) {
            textInputInstructions.setError("Please enter instructions for dish");
            return false;
        }
        else {
            textInputInstructions.setError(null);
            return true;
        }
    }

    private boolean validateCuisine() {
        String str_cuisine = actv_cuisine.getText().toString();

        if (str_cuisine.isEmpty() || str_cuisine.equals(null) || str_cuisine.equals(" ")) {
            textInputCuisine.setError("Please select a cuisine");
            return false;
        }
        else {
            textInputCuisine.setError(null);
            return true;
        }
    }

    private boolean validateMeal() {
        String str_meal = actv_meal.getText().toString();

        if (str_meal.isEmpty() || str_meal.equals(null) || str_meal.equals(" ")) {
            textInputMeal.setError("Please select a meal");
            return false;
        }
        else {
            textInputMeal.setError(null);
            return true;
        }
    }

    private void navbarAdmin(){
        BottomNavigationView bottomNavAdmin = findViewById(R.id.bottom_navigation_admin);
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
}