package ph.dlsu.s11.davidk.taste_eat.adapter;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.RecipesActivity;
import ph.dlsu.s11.davidk.taste_eat.model.CuisineList;

public class CuisineAdapter extends FirestoreRecyclerAdapter<CuisineList, CuisineAdapter.CuisineHolder> {

    public CuisineAdapter(@NonNull FirestoreRecyclerOptions<CuisineList> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CuisineHolder holder, int position, @NonNull CuisineList model) {
        Picasso.get().load(model.getImage()).into(holder.img_cuisine);
        holder.tv_cuisine.setText(model.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                        String str_user_id = getRef(position).getKey();
                Context context = v.getContext();
                String str_name = model.getName();
                Intent intent = new Intent(context, RecipesActivity.class);

                Log.d("TAGS", "FROM: "+str_name);

                //for shared preferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("CUISINE", Context.MODE_PRIVATE);
                SharedPreferences.Editor EDITOR = sharedPreferences.edit();
                EDITOR.putString("cuisine", str_name);
//              EDITOR.clear();
                EDITOR.apply();

                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public CuisineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cuisine,
                parent, false);
        return new CuisineHolder(view);
    }

    class CuisineHolder extends RecyclerView.ViewHolder{
        ImageView img_cuisine;
        TextView tv_cuisine;

        public CuisineHolder(View itemView){
            super(itemView);
            img_cuisine = itemView.findViewById(R.id.img_cuisine);
            tv_cuisine = itemView.findViewById(R.id.tv_cuisine);
        }
    }

}
