package ph.dlsu.s11.davidk.taste_eat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.RecipeItemActivity;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class RecipesSavedAdapter extends RecyclerView.Adapter<RecipesSavedAdapter.RecipesSavedViewHolder> {

    //need to put data
    private ArrayList<Recipes> bookmarkRecipesList;
    private Context context;

    public RecipesSavedAdapter(Context context, ArrayList<Recipes> bookmarkRecipesList) {
        this.bookmarkRecipesList = bookmarkRecipesList;
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return bookmarkRecipesList.size();
    }

    @NonNull
    @Override
    public RecipesSavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        RecipesSavedViewHolder recipesSavedViewHolder = new RecipesSavedViewHolder(view);

        return recipesSavedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesSavedAdapter.RecipesSavedViewHolder holder, int position) {
        Log.d("TAG_ADAPTER", "ADAPTER" + bookmarkRecipesList.get(position).getImage());
        Picasso.get().load(bookmarkRecipesList.get(position).getImage()).into(holder.img_recipe);
        holder.tv_name.setText(bookmarkRecipesList.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str_user_id = getRef(position).getKey();
                Context context = v.getContext();
                String str_name = bookmarkRecipesList.get(position).getName();
                String str_image = bookmarkRecipesList.get(position).getImage();
                String str_ingredients = bookmarkRecipesList.get(position).getIngredients();
                String str_instructions = bookmarkRecipesList.get(position).getInstructions();
                int likes = bookmarkRecipesList.get(position).getLikes();

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



    protected class RecipesSavedViewHolder extends RecyclerView.ViewHolder{
        ImageView img_recipe;
        TextView tv_name;

        public RecipesSavedViewHolder(View itemView){
            super(itemView);
            img_recipe = itemView.findViewById(R.id.img_recipe);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

    }
}
