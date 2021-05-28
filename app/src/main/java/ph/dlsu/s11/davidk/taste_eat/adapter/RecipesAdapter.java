package ph.dlsu.s11.davidk.taste_eat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.RecipeItemActivity;
import ph.dlsu.s11.davidk.taste_eat.model.Recipes;

public class RecipesAdapter extends FirestoreRecyclerAdapter <Recipes, RecipesAdapter.RecipesHolder>{

    public RecipesAdapter(@NonNull FirestoreRecyclerOptions<Recipes> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecipesHolder holder, int position, @NonNull Recipes model) {
        Log.d("FRAGMENT", "ADAPTER" + model.getName().toString());
        Picasso.get().load(model.getImage()).into(holder.img_recipe);
        holder.tv_name.setText(model.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str_user_id = getRef(position).getKey();
                Context context = v.getContext();
                String str_name = model.getName();
                String str_image = model.getImage();
                String str_ingredients = model.getIngredients();
                String str_instructions = model.getInstructions();
                String str_cuisine = model.getCuisine();
                String str_meal = model.getMeal();
                int likes = model.getLikes();

                Intent intent = new Intent(context, RecipeItemActivity.class);

                intent.putExtra("name", str_name);
                intent.putExtra("image", str_image);
                intent.putExtra("ingredients", str_ingredients);
                intent.putExtra("instructions", str_instructions);
                intent.putExtra("cuisine", str_cuisine);
                intent.putExtra("meal", str_meal);
                intent.putExtra("likes", likes);

                v.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public RecipesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe,
                parent, false);
        return new RecipesHolder(view);
    }


    class RecipesHolder extends RecyclerView.ViewHolder{
        ImageView img_recipe;
        TextView tv_name;

        public RecipesHolder(View itemView){
            super(itemView);
            img_recipe = itemView.findViewById(R.id.img_recipe);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
