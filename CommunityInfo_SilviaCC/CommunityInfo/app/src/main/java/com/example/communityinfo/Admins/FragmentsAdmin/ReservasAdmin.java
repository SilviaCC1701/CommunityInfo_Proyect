package com.example.communityinfo.Admins.FragmentsAdmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Reserva;
import com.example.communityinfo.R;
import com.example.communityinfo.Admins.ActivitiesAdmin.CrearReservaAdmin;
import com.example.communityinfo.Admins.ActivitiesAdmin.ModificarReservaAdmin;
import com.example.communityinfo.Adapters_RecyclerView.ReservaListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReservasAdmin extends Fragment implements ReservaListAdapter.OnItemClickListener {

    private View view;
    private RecyclerView recyclerViewReservas;
    private ReservaListAdapter reservaAdapter;
    private List<Reserva> reservasList;
    private FirebaseAuth miAuth;
    private FirebaseFirestore miDb;
    private String cifSelected;
    private CalendarView calendarView;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public ReservasAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();
        cifSelected = readFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservas_admin, container, false);
        recyclerViewReservas = view.findViewById(R.id.rv_listadoReservasUsers);
        recyclerViewReservas.setLayoutManager(new LinearLayoutManager(getContext()));

        reservasList = new ArrayList<>();
        reservaAdapter = new ReservaListAdapter(reservasList, this);
        recyclerViewReservas.setAdapter(reservaAdapter);

        calendarView = view.findViewById(R.id.calendarViewAdmin);
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "-" + (month + 1) + "-" + year;
            try {
                Date date = sdf.parse(selectedDate);
                cargarReservas(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CrearReservaAdmin.class);
            startActivity(intent);
        });

        cargarReservas(new Date());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                confirmarEliminarReserva(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewReservas);

        return view;
    }

    private void cargarReservas(Date date) {
        FirebaseUser user = miAuth.getCurrentUser();
        if (user != null && cifSelected != null) {
            CollectionReference reservasRef = miDb.collection("comunidades").document(cifSelected).collection("reservas");
            long startOfDay = getStartOfDay(date).getTime();
            long endOfDay = getEndOfDay(date).getTime();

            reservasRef.whereGreaterThanOrEqualTo("fechaReserva", startOfDay)
                    .whereLessThanOrEqualTo("fechaReserva", endOfDay)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reservasList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Reserva reserva = doc.toObject(Reserva.class);
                                reserva.setId(doc.getId());
                                reservasList.add(reserva);
                            }
                            reservaAdapter.notifyDataSetChanged();
                            if (reservasList.isEmpty()) {
                                view.findViewById(R.id.tv_SinDatos).setVisibility(View.VISIBLE);
                            } else {
                                view.findViewById(R.id.tv_SinDatos).setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getContext(), "Error al cargar las reservas", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al obtener las reservas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado o CIF no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarReserva(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Reserva")
                .setMessage("¿Está seguro de que desea eliminar esta reserva?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> eliminarReserva(position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> reservaAdapter.notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eliminarReserva(int position) {
        Reserva reserva = reservasList.get(position);
        miDb.collection("comunidades").document(cifSelected).collection("reservas").document(reserva.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Reserva eliminada con éxito", Toast.LENGTH_SHORT).show();
                    reservaAdapter.removeItem(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar la reserva: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    reservaAdapter.notifyItemChanged(position);
                });
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    // Click en item para modificar cualquier reserva seleccionada
    @Override
    public void onItemClick(Reserva reserva) {
        Intent intent = new Intent(getContext(), ModificarReservaAdmin.class);
        intent.putExtra("reservaId", reserva.getId());
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(getContext().getFilesDir(), "Database");
        File file = new File(directory, "ComunidadCif.txt");
        StringBuilder content = new StringBuilder();

        if (!file.exists()) {
            System.err.println("El archivo no existe.");
            return null;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Ocurrió un error al leer el archivo: " + e.getMessage());
            try {
                Files.delete(file.toPath());
                System.out.println("Archivo eliminado debido a un error de lectura.");
            } catch (IOException deleteException) {
                System.err.println("No se pudo eliminar el archivo: " + deleteException.getMessage());
            }
            return null;
        }
        return content.toString().trim();
    }
}