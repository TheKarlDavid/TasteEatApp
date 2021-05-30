package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class AddCuisineActivity extends AppCompatActivity {

    private EditText et_cuisine_name;
    private TextInputLayout textInputName;
    private ImageButton img_btn_cuisine;
    private TextView tv_add_cuisine, tv_error_image;

    private String str_name;
    private Uri imageUri = null;
    private static final int GALLERY_REQUEST =1;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;

    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cuisine);

        init();
        navbarAdmin();
    }

    private void init(){
        et_cuisine_name = findViewById(R.id.et_cuisine_name);
        textInputName = findViewById(R.id.textInputName);
        img_btn_cuisine = findViewById(R.id.img_btn_cuisine);
        tv_add_cuisine = findViewById(R.id.tv_add_cuisine);
        tv_error_image = findViewById(R.id.tv_error_image);

        str_name = et_cuisine_name.getText().toString();

        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        img_btn_cuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        tv_add_cuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateName() && imageUri != null){
                    progressDialog.setMessage("Adding new cuisine");
                    progressDialog.show();

                    Log.d("TAG", "ADD CUISINE :" + str_name);

//                    //return name of image
//                    StorageReference filePath = storageReference.child("Cuisine Images").child(imageUri.getLastPathSegment());
                    StorageReference filePath = storageReference.child("Cuisine Images").child(str_name);

                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d("TAG", "ADD URI :" + uri);

                                    //create new map object to send data to the db
                                    Map<String, Object> cuisine = new HashMap<>();

                                    //add data to map
                                    cuisine.put("Name", str_name);
                                    cuisine.put("Image", uri.toString());


                                    //add new document with generated ID
                                    db.collection("cuisines")
                                            .add(cuisine)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d("tag", "successful insert");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //Execute when data is not successfully inserted to database
                                                    Log.d("tag", "error insert fail");
                                                }
                                            });
                                }
                            });

                            progressDialog.dismiss();

                            Toast.makeText(getApplicationContext(), str_name + " cuisine successfully addded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    });

                }

            }
        });


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            img_btn_cuisine.setImageURI(imageUri);
        }
    }

    private boolean validateName() {
        str_name = et_cuisine_name.getText().toString();

        if (str_name.isEmpty() || str_name.equals(null) || str_name.equals(" ")) {
            if(imageUri == null){
                tv_error_image.setVisibility(View.VISIBLE);
            }
            else{
                tv_error_image.setVisibility(View.GONE);
            }

            textInputName.setError("Please enter a cuisine name");
            return false;
        }
        else {
            if(imageUri == null){
                tv_error_image.setVisibility(View.VISIBLE);
            }
            else{
                tv_error_image.setVisibility(View.GONE);
            }

            textInputName.setError(null);
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