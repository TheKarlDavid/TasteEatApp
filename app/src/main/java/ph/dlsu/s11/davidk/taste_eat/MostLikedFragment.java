package ph.dlsu.s11.davidk.taste_eat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import ph.dlsu.s11.davidk.taste_eat.adapter.CuisineAdapter;
import ph.dlsu.s11.davidk.taste_eat.adapter.RecipesAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class MostLikedFragment extends Fragment {

    private RecyclerView rv_list;
    private RecipesAdapter adapter;

    private String str_cuisine_name;
    private TextView tv_no_recipes;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MostLikedFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = this.getActivity().getSharedPreferences("CUISINE", Context.MODE_PRIVATE);
        str_cuisine_name = sp.getString("cuisine", "default");
        Log.d("TAGS ONCREATE", "FROM: "+str_cuisine_name);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_most_liked, container, false);

        tv_no_recipes = view.findViewById(R.id.tv_no_recipes);

        rv_list = view.findViewById(R.id.rv_list_recipes);
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Query query = db.collection("recipes").whereGreaterThan("likes", 0).orderBy("likes", Query.Direction.DESCENDING).limit(8);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    Log.d("TAGS ONCREATEVIEW: ", "task: "+ task.getResult().isEmpty());
                    tv_no_recipes.setVisibility(view.VISIBLE);
                }


            }
        });

        FirestoreRecyclerOptions<Recipes> recipes = new FirestoreRecyclerOptions.Builder<Recipes>()
                .setQuery(query, Recipes.class)
                .build();



        adapter = new RecipesAdapter(recipes);
        rv_list.setAdapter(adapter);


        return view;

//        Log.d("TAGS ONCREATEVIEW: ", "FROM: "+str_cuisine_name);
//        Log.d("TAGS ONCREATE", "QR: "+query.whereEqualTo("cuisines", str_cuisine_name));

    }


        @Override
        public void onStop() {
            super.onStop();
            adapter.stopListening();
        }

        @Override
        public void onStart() {
            super.onStart();
            adapter.startListening();
        }

}