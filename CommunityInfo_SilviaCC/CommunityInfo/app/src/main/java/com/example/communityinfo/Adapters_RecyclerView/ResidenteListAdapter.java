package com.example.communityinfo.Adapters_RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Residente;
import com.example.communityinfo.R;

import java.util.List;

public class ResidenteListAdapter extends RecyclerView.Adapter<ResidenteListAdapter.ResidenteViewHolder>{
    private List<Residente> residentesList;
    private OnItemClickListener listener;

    public ResidenteListAdapter(List<Residente> residentesList, OnItemClickListener listener) {
        this.residentesList = residentesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResidenteListAdapter.ResidenteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_residente, parent, false);
        return new ResidenteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResidenteViewHolder holder, int position) {
        Residente residente = residentesList.get(position);
        holder.txtDni.setText(residente.getDni());
        holder.txtNombre.setText(residente.getNombre());
        holder.txtNombreUser.setText(residente.getNombreUsuario());
        holder.txtTelefono.setText(residente.getTelefono());
        holder.txtDireccion.setText(residente.getDireccion());
        holder.txtEmail.setText(residente.getEmail());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(residente));
    }

    @Override
    public int getItemCount() {
        return residentesList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Residente residente);
    }

    public void removeItem(int position) {
        residentesList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ResidenteViewHolder extends RecyclerView.ViewHolder {
        TextView txtDni, txtNombre, txtNombreUser, txtTelefono, txtDireccion, txtEmail;

        public ResidenteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDni = itemView.findViewById(R.id.tv_DNI);
            txtNombre = itemView.findViewById(R.id.tv_NombreResidente);
            txtNombreUser = itemView.findViewById(R.id.tv_NombreUserResidente);
            txtTelefono = itemView.findViewById(R.id.tv_TelefonoResidente);
            txtDireccion = itemView.findViewById(R.id.tv_DireccionResidente);
            txtEmail = itemView.findViewById(R.id.tv_EmailResidente);
        }
    }
}
