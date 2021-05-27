package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private TextView tv_login, tv_register;
    private TextInputLayout textInputEmail, textInputPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor EDITOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        tv_login = findViewById(R.id.tv_login);
        tv_register = findViewById(R.id.tv_register);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);

        // initialize cloud firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_email = et_email.getText().toString();
                String str_password = et_password.getText().toString();

                if (!validateEmail() | !validatePassword()) {
                    return;
                }

                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d("TAG", document.getId() + " => " + document.getData());
                                        if(str_email.equals(document.getString("email")) && str_password.equals(document.getString("password"))){

                                            Toast.makeText(getApplicationContext(),
                                                    "email match: " + document.getString("email") + "and" + document.getString("password"), Toast.LENGTH_SHORT).show();

                                            //for shared preferences
                                            sharedPreferences = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
                                            EDITOR = sharedPreferences.edit();
                                            EDITOR.putString("user", document.getString("email"));
//                                            EDITOR.clear();
                                            EDITOR.apply();


                                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                            startActivity(intent);

                                            return;
                                        }
                                        else{

                                            textInputEmail.setError("Incorrect Email or Password");
                                            textInputPassword.setError("Incorrect Email or Password");
                                            Toast.makeText(getApplicationContext(),
                                                    "email do not match", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                } else {
                                    Log.w("TAG", "Error getting documents.", task.getException());
                                }
                            }
                        });
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),
//                        "Register", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    private boolean validateEmail() {
        String str_email = et_email.getText().toString();

        if (str_email.isEmpty()) {
            textInputEmail.setError("Email field can't be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            return false;
        }
        else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String str_password = et_password.getText().toString();

        if (str_password.isEmpty()) {
            textInputPassword.setError("Password field can't be empty");
            return false;
        }
        else {
            textInputPassword.setError(null);
            return true;
        }

    }

}