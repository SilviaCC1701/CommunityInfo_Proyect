package com.example.communityinfo.Users.FragmentsUser.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Reserva;
import com.example.communityinfo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservaListAdapter extends RecyclerView.Adapter<ReservaListAdapter.ReservaViewHolder> {
    private List<Reserva> reservasList;
    private OnItemClickListener listener;

    public ReservaListAdapter(List<Reserva> reservas, OnItemClickListener listener) {
        this.reservasList = reservas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservasList.get(position);

        // Convertir Fecha long de tipo milisegundos a Date
        long fechaReservadaEpoch = reserva.getFechaReserva();
        Date fechaReservada = new Date(fechaReservadaEpoch);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String fechaFormato = format.format(fechaReservada);

        // Obtener estado de la reserva
        String estado = getEstadoReserva(reserva);

        holder.txtFecha.setText(fechaFormato);
        holder.txtArea.setText(reserva.getAreaUbicacion());
        holder.txtHoraInicio.setText(reserva.getHoraInicio());
        holder.txtHoraFin.setText(reserva.getHoraFin());
        holder.txtMotivo.setText(reserva.getMotivo());
        holder.txtEstado.setText(estado);

        // Cambia de color el texto del estado según la fecha
        int color;
        switch (estado) {
            case "PRÓXIMAMENTE":
                color = holder.itemView.getContext().getResources().getColor(R.color.orange);
                break;
            case "EN PROCESO":
                color = holder.itemView.getContext().getResources().getColor(R.color.blue);
                break;
            case "FINALIZADO":
                color = holder.itemView.getContext().getResources().getColor(R.color.red);
                break;
            default:
                color = holder.itemView.getContext().getResources().getColor(android.R.color.black);
                break;
        }
        holder.txtEstado.setTextColor(color);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(reserva));
    }


    @Override
    public int getItemCount() {
        return reservasList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Reserva reserva);
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtArea, txtHoraInicio, txtHoraFin, txtMotivo, txtEstado;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.tv_fechaReserva);
            txtArea = itemView.findViewById(R.id.tv_areaUbicacion);
            txtHoraInicio = itemView.findViewById(R.id.tv_horaInicio);
            txtHoraFin = itemView.findViewById(R.id.tv_horaFin);
            txtMotivo = itemView.findViewById(R.id.tv_motivo);
            txtEstado = itemView.findViewById(R.id.tv_estadoReserva);
        }
    }

    private String getEstadoReserva(Reserva reserva) {
        long now = System.currentTimeMillis();
        long fechaDeReserva = reserva.getFechaReserva(); // Este debería ser en milisegundos
        String horaInicio = reserva.getHoraInicio();
        String horaFin = reserva.getHoraFin();

        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date dateInicio = dateFormat.parse(horaInicio);
            Date dateFin = dateFormat.parse(horaFin);
            if (dateInicio != null && dateFin != null) {
                long inicioTimestamp = fechaDeReserva + dateInicio.getTime();
                long finTimestamp = fechaDeReserva + dateFin.getTime();

                if (now < inicioTimestamp) {
                    return "PRÓXIMAMENTE";
                } else if (now >= inicioTimestamp && now <= finTimestamp) {
                    return "EN PROCESO";
                } else {
                    return "FINALIZADO";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
