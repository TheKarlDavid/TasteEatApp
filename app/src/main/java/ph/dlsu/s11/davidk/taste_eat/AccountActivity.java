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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AccountActivity extends AppCompatActivity {

    private TextView tv_header, tv_name, tv_email, tv_suggestion, tv_logout;
    private EditText et_suggestion;
    private LinearLayout ll_suggestion;
    private ImageView img_user;
    private String str_user_email, str_user_name, str_role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        str_role = sp.getString("role", "default");

        Log.d("TAGS", "ADMIN : " + str_role);

        if(str_role.equalsIgnoreCase("admin")){
            navbarAdmin();
        }
        else{
            navbar();
        }

        init();

    }

    private void init(){
        tv_header = findViewById(R.id.tv_header);
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_suggestion = findViewById(R.id.tv_suggestion);
        tv_logout = findViewById(R.id.tv_logout);
        et_suggestion = findViewById(R.id.et_suggestion);
        ll_suggestion = findViewById(R.id.ll_suggestion);
        img_user = findViewById(R.id.img_user);

        SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        str_user_email = sp.getString("user", "default");
        str_user_name = sp.getString("name", "default");
        str_role = sp.getString("role", "default");


        if(str_role.equalsIgnoreCase("admin")){
//            Log.d("TAGS", "admin" + str_user_email);
            tv_header.setText("Admin account");
            img_user.setImageResource(R.drawable.admin_profile);
            ll_suggestion.setVisibility(View.GONE);
        }

        tv_name.setText(str_user_name);
        tv_email.setText(str_user_email);

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
                SharedPreferences.Editor EDITOR = sp.edit();
                EDITOR.clear();
                EDITOR.apply();

                Intent intent = new Intent(AccountActivity.this, MainActivity.class);
                startActivity(intent);

                Log.d("TAGS", "Logout");
            }
        });

    }

    private void navbar(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        BottomNavigationView bottomNavAdmin = findViewById(R.id.bottom_navigation_admin);

        bottomNav.setVisibility(View.VISIBLE);
        bottomNavAdmin.setVisibility(View.GONE);

        bottomNav.setSelectedItemId(R.id.nav_account);
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
                        startActivity(new Intent(getApplicationContext(),
                                LikedActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_account:
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

        bottomNavAdmin.setSelectedItemId(R.id.nav_account);
        bottomNavAdmin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_book:
                        startActivity(new Intent(getApplicationContext(),
                                HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_suggestion:
                        startActivity(new Intent(getApplicationContext(),
                                SuggestionActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_account:
                        return true;
                }
                return false;
            }
        });
    }
}