package ph.dlsu.s11.davidk.taste_eat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

import ph.dlsu.s11.davidk.taste_eat.R;
import ph.dlsu.s11.davidk.taste_eat.model.Suggestion;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder> implements Serializable {

    //need to put data
    private ArrayList<Suggestion> suggestionArrayList;
    private Context context;

    public SuggestionAdapter (Context context, ArrayList<Suggestion> suggestionArrayList) {
        this.suggestionArrayList = suggestionArrayList;
        this.context = context;
    }

    @Override
    public int getItemCount() { return suggestionArrayList.size(); }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion, parent, false);

        SuggestionViewHolder suggestionViewHolder = new SuggestionViewHolder(view);

        return suggestionViewHolder;
    }

    @Override
    public void onBindViewHolder(final SuggestionAdapter.SuggestionViewHolder holder, final int position) {

        holder.tv_name.setText(suggestionArrayList.get(position).getSuggestor());
        holder.tv_date.setText(suggestionArrayList.get(position).getDate());
        holder.tv_suggestion.setText(suggestionArrayList.get(position).getSuggestion());

    }

    protected class SuggestionViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name, tv_date, tv_suggestion;

        public SuggestionViewHolder(View view){
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_date = view.findViewById(R.id.tv_date);
            tv_suggestion = view.findViewById(R.id.tv_suggestion);
        }

    }



}
