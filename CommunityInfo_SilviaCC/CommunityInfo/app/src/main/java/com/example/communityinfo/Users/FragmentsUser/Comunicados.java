package com.example.communityinfo.Users.FragmentsUser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Modelos.Comunicado;
import com.example.communityinfo.R;
import com.example.communityinfo.Users.FragmentsUser.Adapters.ComunicadoListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class Comunicados extends Fragment {
    private View view;
    private RecyclerView recyclerViewComunicados;
    private ComunicadoListAdapter comunicadoAdapter;
    private List<Comunicado> comunicadosList;
    private FirebaseAuth miAuth;
    private FirebaseFirestore miDb;
    private String cifSelected; // Contiene el cif de la comunidad seleccionada

    public Comunicados() { } // Se requiere de un constructor vacío.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_comunicados, container, false);
        recyclerViewComunicados = view.findViewById(R.id.rv_listadoComunicados);
        recyclerViewComunicados.setLayoutManager(new LinearLayoutManager(getContext()));

        comunicadosList = new ArrayList<>();
        comunicadoAdapter = new ComunicadoListAdapter(comunicadosList);
        recyclerViewComunicados.setAdapter(comunicadoAdapter);

        if (cifSelected != null) {
            cargarComunicados(cifSelected);
        } else {
            Toast.makeText(getContext(), "No se encontró el CIF de la comunidad", Toast.LENGTH_SHORT).show();
        }

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
            // Elimina el archivo en caso de error de lectura
            try {
                Files.delete(file.toPath());
                System.out.println("Archivo eliminado debido a un error de lectura.");
            } catch (IOException deleteException) {
                System.err.println("No se pudo eliminar el archivo: " + deleteException.getMessage());
            }
            return null;
        }
        return content.toString().trim(); // Elimina el salto de línea final
    }
}
