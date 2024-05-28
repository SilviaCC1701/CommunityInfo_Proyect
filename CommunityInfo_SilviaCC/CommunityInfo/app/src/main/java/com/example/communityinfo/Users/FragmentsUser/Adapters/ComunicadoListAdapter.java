package com.example.communityinfo.Users.FragmentsUser.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.Modelos.Reserva;
import com.example.communityinfo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComunicadoListAdapter extends RecyclerView.Adapter<ComunicadoListAdapter.ComunicadoViewHolder> {
    private List<Comunicado> comunicadosList;

    public ComunicadoListAdapter(List<Comunicado> comunicados) {
        this.comunicadosList = comunicados;
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

        // Asignación valores
        holder.txtTitulo.setText(comunicado.getTitulo());
        holder.txtAsunto.setText(comunicado.getAsunto());
        holder.txtContenido.setText(comunicado.getContenido());

        // Convertir Fecha long de tipo Epoch timestamp a Date
        Date fecha = new Date(comunicado.getFecha() * 1000);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaFormato = format.format(fecha);
        holder.txtFecha.setText(fechaFormato);
    }

    @Override
    public int getItemCount() {
        return comunicadosList.size();
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
