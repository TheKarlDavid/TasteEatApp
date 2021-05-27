package ph.dlsu.s11.davidk.taste_eat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import ph.dlsu.s11.davidk.taste_eat.adapter.RecipesAdapter;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class DinnerFragment extends Fragment {

    private RecyclerView rv_list;
    private RecipesAdapter adapter;

    private String str_cuisine_name;
    private TextView tv_no_recipes;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DinnerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = this.getActivity().getSharedPreferences("CUISINE", Context.MODE_PRIVATE);
        str_cuisine_name = sp.getString("cuisine", "default");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dinner, container, false);

        tv_no_recipes = view.findViewById(R.id.tv_no_recipes);

        rv_list = view.findViewById(R.id.rv_list_recipes);
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 2));

        Query query = db.collection("recipes").whereEqualTo("cuisine", str_cuisine_name).whereEqualTo("meal", "dinner");

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
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