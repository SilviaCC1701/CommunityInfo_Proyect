package com.example.communityinfo.Adapters_RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComunicadoListAdapter extends RecyclerView.Adapter<ComunicadoListAdapter.ComunicadoViewHolder> {
    private List<Comunicado> comunicadosList;
    private OnItemClickListener listener;

    public ComunicadoListAdapter(List<Comunicado> comunicados, OnItemClickListener listener) {
        this.comunicadosList = comunicados;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComunicadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_comunicado, parent, false);
        return new ComunicadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComunicadoViewHolder holder, int position) {
        Comunicado comunicado = comunicadosList.get(position);

        // Convertir Fecha long de tipo milisegundos a Date
        long fechaReservadaEpoch = comunicado.getFecha();
        Date fechaReservada = new Date(fechaReservadaEpoch);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaFormato = format.format(fechaReservada);


        // Relleno de TextView con valores del item selected
        holder.txtFecha.setText(fechaFormato);
        holder.txtTitulo.setText(comunicado.getTitulo());
        holder.txtAsunto.setText(comunicado.getAsunto());
        holder.txtContenido.setText(comunicado.getContenido());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(comunicado));
    }

    @Override
    public int getItemCount() {
        return comunicadosList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Comunicado comunicado);
    }

    public void removeItem(int position) {
        comunicadosList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ComunicadoViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtTitulo, txtAsunto, txtContenido;

        public ComunicadoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.tv_fecha);
            txtTitulo = itemView.findViewById(R.id.tv_titulo);
            txtAsunto = itemView.findViewById(R.id.tv_asunto);
            txtContenido = itemView.findViewById(R.id.tv_contenido);
        }
    }
}
