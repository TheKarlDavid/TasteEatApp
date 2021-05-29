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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import io.grpc.internal.FailingClientStream;
import kotlin.collections.IntIterator;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_first_name, et_last_name, et_email, et_password, et_password_confirm;
    private TextView tv_signup;
    private TextInputLayout textInputFirstName, textInputLastName, textInputEmail, textInputPassword, textInputPasswordConfirm;
    private int isExistingEmail;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor EDITOR;

    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    private void init(){
        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        tv_signup = findViewById(R.id.tv_sign_up);

        textInputFirstName = findViewById(R.id.textInputFirstName);
        textInputLastName = findViewById(R.id.textInputLastName);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
        textInputPasswordConfirm = findViewById(R.id.textInputPasswordConfirm);



        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str_email = et_email.getText().toString();

                if (!validateFirstName() || !validateLastName() || !validateEmail() || !validatePassword() || !validatePasswordConfirm()) {
                    return;
                }
                else{
                    checkEmail(str_email);
                }


//                String input = "Email: " + textInputEmail.getEditText().getText().toString();
//                input += "\n";
//                input += "First Name: " + textInputFirstName.getEditText().getText().toString();
//                input += "\n";
//                input += "Last Name: " + textInputFirstName.getEditText().getText().toString();
//                input += "\n";
//                input += "Password: " + textInputPassword.getEditText().getText().toString();
//
//                Toast.makeText(getApplicationContext(), "SUCCESS" + input, Toast.LENGTH_SHORT).show();
            }




//            }

        });

    }

    private void checkEmail(String email){
        isExistingEmail = 0;
        final String[] inputEmail = new String[1];
        final String[] DBemail = new String[1];

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                inputEmail[0] = et_email.getText().toString();
                                DBemail[0] = document.getString("email");
                                if(inputEmail[0].equals(DBemail[0])){
                                    //   System.out.println("I am the same email!!!!");
                                    isExistingEmail = 1;
//                                    Toast.makeText(getApplicationContext(), "EXIST E " , Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            if(isExistingEmail == 1){
                                //  System.out.println("Change email!!!!");
                                textInputEmail.setError("Email has already been taken");
                            }
                            if(isExistingEmail == 0){
                                //create new map object to send data to the db
                                Map<String, Object> user = new HashMap<>();

                                //add data to map
                                user.put("first_name", et_first_name.getText().toString());
                                user.put("last_name", et_last_name.getText().toString());
                                user.put("email", et_email.getText().toString());
                                user.put("password", et_password.getText().toString());
                                user.put("role", "user");

                                //add new document with generated ID
                                //remember model of firestore: Data -> Document -> Collection
                                db.collection("users")
                                        .add(user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                //Execute when data is successfully inserted to database
                                                Log.d("tag", "inserted success");
//                                                Toast.makeText(getApplicationContext(), "Account successfully created", Toast.LENGTH_SHORT).show();

                                                Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content) ,
                                                        "ACCOUNT SUCCESSFULLY CREATED", Snackbar.LENGTH_LONG).show();
                                                //for shared preferences
                                                sharedPreferences = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
                                                EDITOR = sharedPreferences.edit();
                                                EDITOR.putString("user", et_email.getText().toString());
                                                EDITOR.putString("name", et_first_name.getText().toString() + " " + et_last_name.getText().toString());
                                                EDITOR.putString("role", "user");
                                                EDITOR.apply();

                                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                                startActivity(intent);
                                                finish();
//                                                System.exit(0);
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
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private boolean validateFirstName() {
        String str_first_name = et_first_name.getText().toString();

        if (str_first_name.isEmpty()) {
            textInputFirstName.setError("Please enter your first name");
            return false;
        }
        else {
            textInputFirstName.setError(null);
            return true;
        }
    }

    private boolean validateLastName() {
        String str_last_name = et_last_name.getText().toString();

        if (str_last_name.isEmpty()) {
            textInputLastName.setError("Please enter your last name");
            return false;
        }
        else {
            textInputLastName.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
//        Toast.makeText(getApplicationContext(), "validateEmail", Toast.LENGTH_SHORT).show();
        String str_email = et_email.getText().toString();

        if (str_email.isEmpty()) {
            textInputEmail.setError("Email field can't be empty");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
            textInputEmail.setError("Please enter a valid email address");
            return false;
        }
//        else if (existingEmail[0] == 1){
//            textInputEmail.setError("Email address already exists");
//            return false;
//        }
        else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String str_password = et_password.getText().toString();
        String str_password_confirm = et_password_confirm.getText().toString();

        if (str_password.isEmpty()) {
            textInputPassword.setError("Password field can't be empty");
            return false;
        }
        else if(str_password_confirm.isEmpty()){
            textInputPasswordConfirm.setError("Password confirm field can't be empty");
            return false;
        }

        else {

            if(str_password.length() >= 8){
                if(str_password.equals(str_password_confirm)){
//                    Intent intent = new Intent(RegistrationActivity.this, WelcomeActivity.class);
//                    intent.putExtra ( "text_username", username);
//                    startActivity(intent);
                    textInputPassword.setError(null);
                    return true;
                }
                else{
                    textInputPassword.setError("Password and confirm password does not match");
                    textInputPasswordConfirm.setError("Password and confirm password does not match");
                    return false;
                }
            }
            else{
                textInputPassword.setError("Password must be at least 8 characters");
                textInputPasswordConfirm.setError("Password must be at least 8 characters");
                return false;
            }

        }
    }

    private boolean validatePasswordConfirm() {
        String str_password_confirm = et_password_confirm.getText().toString();

        if(str_password_confirm.isEmpty()){
            textInputPasswordConfirm.setError("Password confirm field can't be empty");
            return false;
        }

        else {
            textInputPasswordConfirm.setError(null);
            return true;

        }
    }



}