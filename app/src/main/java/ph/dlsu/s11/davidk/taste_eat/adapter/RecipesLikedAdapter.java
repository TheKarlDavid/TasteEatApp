package ph.dlsu.s11.davidk.taste_eat.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.RecipeItemActivity;
import ph.dlsu.s11.davidk.taste_eat.helper.ItemTouchHelperAdapter;
import ph.dlsu.s11.davidk.taste_eat.helper.OnStartDragListener;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class RecipesLikedAdapter extends RecyclerView.Adapter<RecipesLikedAdapter.RecipesLikedViewHolder> implements ItemTouchHelperAdapter {
    //need to put data
    private ArrayList<Recipes> likedRecipeList;
    private Context context;

    private OnStartDragListener listener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String temp = "";

    public RecipesLikedAdapter(Context context, ArrayList<Recipes> likedRecipeList, OnStartDragListener listener) {
        this.likedRecipeList = likedRecipeList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount(){
        return likedRecipeList.size();
    }

    @NonNull
    @Override
    public RecipesLikedAdapter.RecipesLikedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        RecipesLikedAdapter.RecipesLikedViewHolder recipesLikedViewHolder = new RecipesLikedAdapter.RecipesLikedViewHolder(view);

        return recipesLikedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesLikedAdapter.RecipesLikedViewHolder holder, int position) {
        Log.d("TAG_ADAPTER", "ADAPTER" + likedRecipeList.get(position).getImage());
        Picasso.get().load(likedRecipeList.get(position).getImage()).into(holder.img_recipe);
        holder.tv_name.setText(likedRecipeList.get(position).getName());

        holder.item.setOnTouchListener((view, motionEvent) -> {
            final int action = motionEvent.getAction();
            if(action == MotionEvent.ACTION_DOWN)
                listener.onStartDrag(holder);
            return false;
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str_user_id = getRef(position).getKey();
                Context context = v.getContext();
                String str_name = likedRecipeList.get(position).getName();
                String str_image = likedRecipeList.get(position).getImage();
                String str_ingredients = likedRecipeList.get(position).getIngredients();
                String str_instructions = likedRecipeList.get(position).getInstructions();
                int likes = likedRecipeList.get(position).getLikes();

                Intent intent = new Intent(context, RecipeItemActivity.class);

                intent.putExtra("name", str_name);
                intent.putExtra("image", str_image);
                intent.putExtra("ingredients", str_ingredients);
                intent.putExtra("instructions", str_instructions);
                intent.putExtra("likes", likes);

                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(likedRecipeList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        SharedPreferences sharedPreferences = context.getSharedPreferences("APP_USER", Context.MODE_PRIVATE);
        String str_user_email = sharedPreferences.getString("user", null);

        db.collection("likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("TAG", document.getId() + " => " + document.getData());
                                //if user liked
                                if(str_user_email.equals(document.getString("email"))){
                                    String strId = document.getId();
                                    temp = "";
                                    for(int i=0; i<likedRecipeList.size(); i++){
                                    Log.d("TAG", i + " : "+ likedRecipeList.size());
                                        if(i==0){
                                            temp = temp.concat(likedRecipeList.get(i).getName());
                                        }
                                        else{
                                            temp = temp.concat(","+likedRecipeList.get(i).getName());
                                        }
                                        Log.d("TAG",  "HERE : "+ temp);
                                    }

                                    //create new map object to send data to the db
                                    Map<String, Object> like = new HashMap<>();

                                    //add data to map
                                    like.put("email", str_user_email);
                                    like.put("recipes", temp);

                                    //update
                                    db.collection("likes")
                                            .document(strId).set(like);
                                }
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        likedRecipeList.remove(position);
        notifyItemRemoved(position);
    }


    protected class RecipesLikedViewHolder extends RecyclerView.ViewHolder{
        ImageView img_recipe;
        TextView tv_name;
        CardView item;

        public RecipesLikedViewHolder(View itemView){
            super(itemView);
            img_recipe = itemView.findViewById(R.id.img_recipe);
            tv_name = itemView.findViewById(R.id.tv_name);

            item = itemView.findViewById(R.id.item);
        }

    }

}
