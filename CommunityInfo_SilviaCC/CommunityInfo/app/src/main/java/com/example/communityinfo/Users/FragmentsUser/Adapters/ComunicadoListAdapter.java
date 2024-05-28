package com.example.communityinfo.Fragments.Adapters;

import com.example.communityinfo.Modelos.Comunicado;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.communityinfo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ComunicadoListAdapter extends RecyclerView.Adapter<ComunicadoListAdapter.ComunicadoViewHolder> {
    private List<Comunicado> comunicados;

    public ComunicadoListAdapter(List<Comunicado> comunicados) {
        this.comunicados = comunicados;
    }

    @NonNull
    @Override
    public ComunicadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_comunicado, parent, false);
        return new ComunicadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComunicadoViewHolder holder, int position) {
        Comunicado comunicado = comunicados.get(position);
        holder.titulo.setText(comunicado.getTitulo());
        holder.asunto.setText(comunicado.getAsunto());
        holder.contenido.setText(comunicado.getContenido());

        Date f = new Date(comunicado.getFecha());
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormato = format.format(f);
        holder.fecha.setText(fechaFormato);
    }

    @Override
    public int getItemCount() {
        return comunicados.size();
    }

    public static class ComunicadoViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, asunto, contenido, fecha;

        public ComunicadoViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_titulo);
            asunto = itemView.findViewById(R.id.tv_asunto);
            contenido = itemView.findViewById(R.id.tv_contenido);
            fecha = itemView.findViewById(R.id.tv_fecha);
        }
    }
}
