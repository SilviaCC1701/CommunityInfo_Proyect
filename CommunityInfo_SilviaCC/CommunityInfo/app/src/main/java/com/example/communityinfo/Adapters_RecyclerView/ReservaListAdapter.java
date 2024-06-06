package com.example.communityinfo.Adapters_RecyclerView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public void removeItem(int position) {
        reservasList.remove(position);
        notifyItemRemoved(position);
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
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
        long now = calendar.getTimeInMillis();
        long fechaDeReserva = reserva.getFechaReserva();
        String horaInicio = reserva.getHoraInicio();
        String horaFin = reserva.getHoraFin();

        try {
            // Convertir horas a Date con un formato y zona horaria
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
            Date dateInicio = dateFormat.parse(horaInicio);
            Date dateFin = dateFormat.parse(horaFin);

            // Crear calendarios y establecer hora
            Calendar calendarInicio = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
            Calendar calendarFin = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"));
            calendarInicio.setTime(dateInicio);
            calendarFin.setTime(dateFin);

            // Establecer la fecha de reserva en los objetos Calendar
            calendar.setTimeInMillis(fechaDeReserva);
            calendarInicio.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendarInicio.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            calendarInicio.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            calendarFin.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
            calendarFin.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            calendarFin.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));

            if (dateInicio != null && dateFin != null) {

                // Obtener los tiempos en milisegundos del inicio y fin de la reserva
                long inicioTimestamp = calendarInicio.getTimeInMillis();
                long finTimestamp = calendarFin.getTimeInMillis();

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
