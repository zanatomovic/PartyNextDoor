package com.example.partynextdoor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.bumptech.glide.Glide;
import com.example.partynextdoor.model.Zurka;
import com.example.partynextdoor.ui.DetaljiDialogFragment;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final List<Zurka> zurkeList;
    private Context context;

    public RecyclerViewAdapter(List<Zurka> zurkeList, Context context) {
        this.zurkeList = zurkeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zurka_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Zurka zurka = zurkeList.get(position);
        holder.title.setText(zurka.getNaziv());
        holder.subtitle.setText(zurka.getLokacija());

        if (zurka.getPozadinaUri() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(zurka.getPozadinaUri())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.icon);
        } else {
            holder.icon.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            DetaljiDialogFragment fragment = DetaljiDialogFragment.newInstance(zurka);

            fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "detaljiDialog");
        });
    }

    @Override
    public int getItemCount() {
        return zurkeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.subtitle);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
