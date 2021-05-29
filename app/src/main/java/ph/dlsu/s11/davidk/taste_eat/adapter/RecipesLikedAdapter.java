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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.RecipeItemActivity;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class RecipesLikedAdapter extends RecyclerView.Adapter<RecipesLikedAdapter.RecipesLikedViewHolder>{
    //need to put data
    private ArrayList<Recipes> likedRecipeList;
    private Context context;

    public RecipesLikedAdapter(Context context, ArrayList<Recipes> likedRecipeList) {
        this.likedRecipeList = likedRecipeList;
        this.context = context;
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



    protected class RecipesLikedViewHolder extends RecyclerView.ViewHolder{
        ImageView img_recipe;
        TextView tv_name;

        public RecipesLikedViewHolder(View itemView){
            super(itemView);
            img_recipe = itemView.findViewById(R.id.img_recipe);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

    }

}