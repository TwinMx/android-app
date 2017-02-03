package fr.isen.twinmx.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.isen.twinmx.R;
import fr.isen.twinmx.ui.holders.ManualHolder;
import fr.isen.twinmx.utilabc.manual.ManualPage;

/**
 * Created by pierredfc.
 */

public class ManualAdapter extends RecyclerView.Adapter<ManualHolder> {

    private List<ManualPage> instructions;

    public ManualAdapter(List<ManualPage> instructions)
    {
        this.instructions = instructions;
    }

    @Override
    public ManualHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manual_item, parent, false);
        return new ManualHolder(v);
    }

    @Override
    public void onBindViewHolder(ManualHolder holder, int position) {
        if (position < this.getItemCount()) {
            holder.bind(instructions.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return this.instructions.size();
    }

    public void setItems(List<ManualPage> pages)
    {
        this.instructions = pages;
        notifyDataSetChanged();
    }
}
