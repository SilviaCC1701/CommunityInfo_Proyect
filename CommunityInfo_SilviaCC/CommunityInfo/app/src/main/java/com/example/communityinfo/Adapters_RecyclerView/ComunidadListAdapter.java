package com.example.communityinfo.Adapters_RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.communityinfo.Modelos.Comunidad;
import com.example.communityinfo.R;

import java.util.List;

public class ComunidadListAdapter extends RecyclerView.Adapter<ComunidadListAdapter.ComunidadViewHolder> {
    private List<Comunidad> comunidadesList;
    private OnItemClickListener listener;

    public ComunidadListAdapter(List<Comunidad> comunidadesList, OnItemClickListener listener) {
        this.comunidadesList = comunidadesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComunidadListAdapter.ComunidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_comunidad, parent, false);
        return new ComunidadListAdapter.ComunidadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComunidadViewHolder holder, int position) {
        Comunidad comunidad = comunidadesList.get(position);
        holder.txtCif.setText(comunidad.getCif());
        holder.txtNombre.setText(comunidad.getNombreComunidad());
        holder.txtDireccion.setText(comunidad.getDireccion());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(comunidad));
    }

    @Override
    public int getItemCount() {
        return comunidadesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Comunidad comunidad);
    }

    public void removeItem(int position) {
        comunidadesList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ComunidadViewHolder extends RecyclerView.ViewHolder {
        TextView txtCif, txtNombre, txtDireccion;

        public ComunidadViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCif = itemView.findViewById(R.id.tv_CIF);
            txtNombre = itemView.findViewById(R.id.tv_NombreComunidad);
            txtDireccion = itemView.findViewById(R.id.tv_DireccionComunidad);
        }
    }
}
