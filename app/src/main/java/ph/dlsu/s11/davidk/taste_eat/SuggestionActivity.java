package ph.dlsu.s11.davidk.taste_eat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ph.dlsu.s11.davidk.taste_eat.adapter.RecipesLikedAdapter;
import ph.dlsu.s11.davidk.taste_eat.adapter.SuggestionAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;
import ph.dlsu.s11.davidk.taste_eat.model.Suggestion;

public class SuggestionActivity extends AppCompatActivity {

    private RecyclerView rv_list;
    private ArrayList<Suggestion> suggestionArrayList = new ArrayList<>();
    private SuggestionAdapter suggestionAdapter;

    private TextView tv_instruction;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        init();
        navbarAdmin();
    }

    private void init(){

        tv_instruction = findViewById(R.id.tv_instruction);
        rv_list = findViewById(R.id.rv_list);

        rv_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        db.collection("suggestions").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());

                                String str_id = document.getId();
                                String str_suggestor = "Suggester: " + document.getString("email");
                                String str_date = "Date suggested: " + document.getString("date");
                                String str_suggestion = "Suggestion: " + document.getString("suggestion");

                                Suggestion suggestion = new Suggestion(str_id, str_suggestor, str_date, str_suggestion);
                                suggestionArrayList.add(suggestion);

                                suggestionAdapter = new SuggestionAdapter(getApplicationContext(), suggestionArrayList);

                                new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_list);
                                rv_list.setAdapter(suggestionAdapter);

                                tv_instruction.setVisibility(View.VISIBLE);
                                startAnimation();

                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //when item is swipe
            //remove item from array list

            String str_id = suggestionArrayList.get(viewHolder.getAdapterPosition()).getId();

//            Log.d("TAG", String.valueOf(suggestionArrayList.size()));

            if(suggestionArrayList.size() == 1){
                tv_instruction.setVisibility(View.GONE);
            }

            db.collection("suggestions").document(str_id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error deleting document", e);
                        }
                    });

            suggestionArrayList.remove(viewHolder.getAdapterPosition());
            suggestionAdapter.notifyDataSetChanged();
        }
    };

    private void startAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        tv_instruction.startAnimation(animation);
    }

    private void navbarAdmin(){
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation_admin);
        bottomNav.setSelectedItemId(R.id.nav_suggestion);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_book:
                        startActivity(new Intent(getApplicationContext(),
                                HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.nav_suggestion:
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