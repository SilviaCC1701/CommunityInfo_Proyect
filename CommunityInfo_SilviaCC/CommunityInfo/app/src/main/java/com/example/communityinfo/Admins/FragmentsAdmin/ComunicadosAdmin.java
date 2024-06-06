package com.example.communityinfo.Admins.FragmentsAdmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.communityinfo.Adapters_RecyclerView.ComunicadoListAdapter;
import com.example.communityinfo.Admins.ActivitiesAdmin.ModificarComunicado;
import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.R;
import com.example.communityinfo.Admins.ActivitiesAdmin.CrearComunicado;
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
import java.util.ArrayList;
import java.util.List;

public class ComunicadosAdmin extends Fragment {
    private View view;
    private RecyclerView recyclerViewComunicadosAdmin;
    private ComunicadoListAdapter comunicadoAdapter;
    private List<Comunicado> comunicadosList;
    private FirebaseAuth miAuth;
    private FirebaseFirestore miDb;
    private String cifSelected; // Contiene el cif de la comunidad seleccionada

    public ComunicadosAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comunicados_admin, container, false);
        recyclerViewComunicadosAdmin = view.findViewById(R.id.rv_listadoComunicadosAdmin);
        recyclerViewComunicadosAdmin.setLayoutManager(new LinearLayoutManager(getContext()));


        comunicadosList = new ArrayList<>();
        comunicadoAdapter = new ComunicadoListAdapter(comunicadosList, comunicado -> {
            Intent intent = new Intent(getContext(), ModificarComunicado.class);
            intent.putExtra("comunicadoId", comunicado.getId());
            startActivity(intent);
        });
        recyclerViewComunicadosAdmin.setAdapter(comunicadoAdapter);

        if (cifSelected != null) {
            cargarComunicados(cifSelected);
        } else {
            Toast.makeText(getContext(), "No se encontró el CIF de la comunidad", Toast.LENGTH_SHORT).show();
        }

        // Configurar el ItemTouchHelper para swipeButton
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                confirmarEliminarComunicado(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewComunicadosAdmin);

        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CrearComunicado.class);
            startActivity(intent);
        });

        return view;
    }

    private void cargarComunicados(String cif) {
        FirebaseUser user = miAuth.getCurrentUser();
        if (user != null) {
            CollectionReference comunicadosRef = miDb.collection("comunidades").document(cif).collection("comunicados");
            comunicadosRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    comunicadosList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Comunicado comunicado = doc.toObject(Comunicado.class);
                        comunicado.setId(doc.getId());
                        comunicadosList.add(comunicado);
                    }
                    comunicadoAdapter.notifyDataSetChanged();
                    if (comunicadosList.isEmpty()) {
                        view.findViewById(R.id.tv_SinDatos).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.tv_SinDatos).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar los comunicados", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error al obtener los comunicados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarComunicado(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Comunicado")
                .setMessage("¿Estás seguro de que deseas eliminar este comunicado?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> eliminarComunicado(position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> comunicadoAdapter.notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eliminarComunicado(int position) {
        Comunicado comunicado = comunicadosList.get(position);
        miDb.collection("comunidades").document(cifSelected).collection("comunicados").document(comunicado.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Comunicado eliminado con éxito", Toast.LENGTH_SHORT).show();
                    comunicadoAdapter.removeItem(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al eliminar el comunicado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    comunicadoAdapter.notifyItemChanged(position);
                });
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