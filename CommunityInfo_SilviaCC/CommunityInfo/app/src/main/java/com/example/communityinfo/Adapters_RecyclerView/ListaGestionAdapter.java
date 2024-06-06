package com.example.communityinfo.Adapters_RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Admins.ActivitiesAdmin.ContenidoLista;
import com.example.communityinfo.R;
import java.util.List;

public class ListaGestionAdapter extends RecyclerView.Adapter<ListaGestionAdapter.ListaViewHolder> {
    private Context context;
    private List<String> listNombresGestion;
    public ListaGestionAdapter(Context context, List<String> listListaFill){
        this.context = context;
        this.listNombresGestion = listListaFill;
    }

    @NonNull
    @Override
    public ListaGestionAdapter.ListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListaGestionAdapter.ListaViewHolder(LayoutInflater.from(context).inflate(R.layout.item_lista_gestion,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListaGestionAdapter.ListaViewHolder holder, int position) {
        String itemLista = (String) listNombresGestion.get(position);

        if(itemLista.equalsIgnoreCase("Residentes")){
            holder.iconoLista.setImageResource(R.drawable.ic_residentes);
            holder.nombreLista.setText(itemLista);
        }

        if(itemLista.equalsIgnoreCase("Comunidades")){
            holder.iconoLista.setImageResource(R.drawable.ic_comunidades);
            holder.nombreLista.setText(itemLista);
        }

        if(itemLista.equalsIgnoreCase("LogOut")){
            holder.iconoLista.setImageResource(R.drawable.ic_logout);
            holder.nombreLista.setText(itemLista);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewListaData = new Intent(v.getContext(), ContenidoLista.class);
                viewListaData.putExtra("item_lista", itemLista);
                context.startActivity(viewListaData);
            }
        });
    }

    @Override
    public int getItemCount() { return this.listNombresGestion.size(); }

    public class ListaViewHolder extends RecyclerView.ViewHolder{
        private ImageView iconoLista;
        private TextView nombreLista;
        public ListaViewHolder(@NonNull View itemView) {
            super(itemView);
            iconoLista = itemView.findViewById(R.id.iconoTypeLista_img);
            nombreLista = itemView.findViewById(R.id.nombreObj_txt);
        }
    }
}
