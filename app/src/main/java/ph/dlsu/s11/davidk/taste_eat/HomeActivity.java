package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ph.dlsu.s11.davidk.taste_eat.adapter.CuisineAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.CuisineList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rv_list, rv_list_admin;
    private CuisineAdapter adapter;

    private LinearLayout ll_add_cuisine;
    private ImageView img_add_cuisine;
    private TextView tv_add_cuisine;

    private String str_role;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor EDITOR;

    private final int NOTIFICATION_ID = 100; //notification identifier, each notification in android has ID
    private final String CHANNEL_ID = "ph.dlsu.s11.channels";
    private final String ACTION_SNOOZE = "ph.dlsu.s11.broadcasts";

    private Date date = new Date();
    private Locale philippineLocale = new Locale.Builder().setLanguage("en").setRegion("PH").build();


    // initialize cloud firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initNotification();
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

    private void initNotification(){

        sharedPreferences = getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        String str_role = sharedPreferences.getString("role", null);

        db.collection("notifications")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String str_name = document.getString("name");
                                String str_cuisine = document.getString("cuisine");
                                String str_notified = sharedPreferences.getString("notified", null);

                                if(document.getString("date").equalsIgnoreCase(getDate(date, philippineLocale))
                                        && str_role.equalsIgnoreCase("user")
//                                        && str_notified.equalsIgnoreCase("false")
                                ){

                                    EDITOR = sharedPreferences.edit();
                                    EDITOR.putString("notified", "true");
                                    EDITOR.apply();

                                    Intent snoozeIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                                    snoozeIntent.setAction(ACTION_SNOOZE);
                                    PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, snoozeIntent, 0);

                                    Intent notificationIntent = new Intent(HomeActivity.this, RecipesActivity.class);

                                    //Pending intent execute in future
                                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                                    createNotificationChannel();

                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                            .setSmallIcon(R.drawable.app_logo)
                                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo))
                                            .setContentTitle("New recipe posted!")
                                            .setContentText("Discover and learn a new recipe today")
                                            .setStyle(new NotificationCompat.BigPictureStyle()
                                                    .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.img_food))
                                                    .setBigContentTitle(str_name)
                                                    .setSummaryText(str_cuisine.toUpperCase()))

                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setAutoCancel(true)
                                            .addAction(R.drawable.app_logo, "Go check now",
                                                    contentIntent)
                                            .setContentIntent(contentIntent)
                                            .build();


                                    notificationManager.notify(NOTIFICATION_ID, notification);

                                }


                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });


    }

    private void createNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Channel";
            String description = "Channel Sample";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String getDate(Date date, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy", locale);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        return formatter.format(date);
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