package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.View;
import android.widget.Toast;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.communityinfo.Adapters_RecyclerView.ComunidadListAdapter;
import com.example.communityinfo.Adapters_RecyclerView.ResidenteListAdapter;
import com.example.communityinfo.Inicio;
import com.example.communityinfo.Modelos.Comunidad;
import com.example.communityinfo.Modelos.Residente;
import com.example.communityinfo.R;
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

public class ContenidoLista extends AppCompatActivity {
    private Toolbar toolbarListaSelected;
    private FloatingActionButton buttonPlus;
    private List<Residente> residentesList;
    private List<Comunidad> comunidadesList;
    private ResidenteListAdapter residenteListAdapter;
    private ComunidadListAdapter comunidadListAdapter;
    private RecyclerView recyclerViewResidentes;
    private RecyclerView recyclerViewComunidades;
    private String nombreListaSelected;
    private String cifSelected;
    private FirebaseAuth miAuth;
    private FirebaseFirestore miDb;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contenido_lista);

        miAuth = FirebaseAuth.getInstance();
        miDb = FirebaseFirestore.getInstance();
        cifSelected = readFile(); // Contiene el CIF de la comunidad seleccionada
        toolbarListaSelected = findViewById(R.id.myToolbarContenidoList);
        buttonPlus = findViewById(R.id.floatingActionButton);

        // Recopilo en nombre de la lista seleccionada
        nombreListaSelected = (String) getIntent().getSerializableExtra("item_lista");

        // Establezco titulo del Toolbar
        toolbarListaSelected.setTitle(nombreListaSelected);

        // Sobre el RecyclerView de Residentes
        recyclerViewResidentes = findViewById(R.id.listRecyclerResidentesLista);
        recyclerViewResidentes.setLayoutManager(new LinearLayoutManager(this));
        // Sobre el RecyclerView de Comunidades
        recyclerViewComunidades = findViewById(R.id.listRecyclerComunidadesLista);
        recyclerViewComunidades.setLayoutManager(new LinearLayoutManager(this));

        buttonPlus.setOnClickListener(v -> {
            if (nombreListaSelected.equalsIgnoreCase("residentes")) {
                Intent intent = new Intent(ContenidoLista.this, CrearResidente.class);
                startActivity(intent);
            } else if (nombreListaSelected.equalsIgnoreCase("comunidades")) {
                Intent intent = new Intent(ContenidoLista.this, CrearComunidad.class);
                startActivity(intent);
            }
        });

        // Visualiza en el recyclerView lista de residentes de la coleccion de "residentes" del cifSelected
        if (nombreListaSelected.equalsIgnoreCase("residentes")) {
            setupRecyclerViewResidentes();
            cargarResidentesList(cifSelected);
        }

        // Visualiza en el recyclerView lista de comunidades de la coleccion de "comunidades"
        if (nombreListaSelected.equalsIgnoreCase("comunidades")) {
            setupRecyclerViewComunidades();
            cargarComunidadesList();
        }

        if (nombreListaSelected.equalsIgnoreCase("logout")){
            buttonPlus.setVisibility(View.GONE);
            recyclerViewComunidades.setVisibility(View.GONE);
            recyclerViewResidentes.setVisibility(View.GONE);
            showTextDialog_ConfirmLogout();
        }
    }

    private void setupRecyclerViewResidentes() {
        buttonPlus.setVisibility(View.VISIBLE);
        recyclerViewResidentes.setVisibility(View.VISIBLE);
        recyclerViewComunidades.setVisibility(View.GONE);
        residentesList = new ArrayList<>();
        residenteListAdapter = new ResidenteListAdapter(residentesList, residente -> {
            Intent intent = new Intent(ContenidoLista.this, ModificarResidente.class);
            intent.putExtra("residenteId", residente.getId());
            startActivity(intent);
        });
        recyclerViewResidentes.setAdapter(residenteListAdapter);

        // Configuración del ItemTouchHelper para swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                confirmarEliminarResidente(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewResidentes);
    }

    private void setupRecyclerViewComunidades() {
        recyclerViewComunidades.setVisibility(View.VISIBLE);
        buttonPlus.setVisibility(View.VISIBLE);
        recyclerViewResidentes.setVisibility(View.GONE);
        comunidadesList = new ArrayList<>();
        comunidadListAdapter = new ComunidadListAdapter(comunidadesList, comunidad -> {
            Intent intent = new Intent(ContenidoLista.this, ModificarComunidad.class);
            intent.putExtra("comunidadId", comunidad.getCif());
            startActivity(intent);
        });
        recyclerViewComunidades.setAdapter(comunidadListAdapter);

        // Configuración del ItemTouchHelper para swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                confirmarEliminarComunidad(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewComunidades);
    }

    private void cargarResidentesList(String cif) {
        FirebaseUser user = miAuth.getCurrentUser();
        if (user != null) {
            CollectionReference residentesRef = miDb.collection("comunidades").document(cif).collection("residentes");
            residentesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    residentesList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Residente residente = doc.toObject(Residente.class);
                        residente.setId(doc.getId());
                        residentesList.add(residente);
                    }
                    residenteListAdapter.notifyDataSetChanged();
                    if (residentesList.isEmpty()) {
                        this.findViewById(R.id.tv_SinDatos).setVisibility(View.VISIBLE);
                    } else {
                        this.findViewById(R.id.tv_SinDatos).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ContenidoLista.this, "Error al cargar los residentes", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ContenidoLista.this, "Error al obtener los residentes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(ContenidoLista.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarComunidadesList() {
        FirebaseUser user = miAuth.getCurrentUser();
        if (user != null) {
            CollectionReference comunidadesRef = miDb.collection("comunidades");
            comunidadesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    comunidadesList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Comunidad comunidad = doc.toObject(Comunidad.class);
                        comunidad.setCif(doc.getId());
                        comunidadesList.add(comunidad);
                    }
                    comunidadListAdapter.notifyDataSetChanged();
                    if (comunidadesList.isEmpty()) {
                        this.findViewById(R.id.tv_SinDatos).setVisibility(View.VISIBLE);
                    } else {
                        this.findViewById(R.id.tv_SinDatos).setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(ContenidoLista.this, "Error al cargar las comunidades", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ContenidoLista.this, "Error al obtener las comunidades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(ContenidoLista.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarEliminarResidente(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Residente")
                .setMessage("¿Estás seguro de que deseas eliminar este residente?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> eliminarResidente(position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> residenteListAdapter.notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void confirmarEliminarComunidad(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Comunidad")
                .setMessage("¿Estás seguro de que deseas eliminar esta comunidad?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> eliminarComunidad(position))
                .setNegativeButton(android.R.string.no, (dialog, which) -> comunidadListAdapter.notifyItemChanged(position))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eliminarResidente(int position) {
        Residente residente = residentesList.get(position);
        miDb.collection("comunidades").document(cifSelected).collection("residentes").document(residente.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ContenidoLista.this, "Residente eliminado con éxito", Toast.LENGTH_SHORT).show();
                    residentesList.remove(position);
                    residenteListAdapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContenidoLista.this, "Error al eliminar el residente: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    residenteListAdapter.notifyItemChanged(position);
                });
    }

    private void eliminarComunidad(int position) {
        Comunidad comunidad = comunidadesList.get(position);
        miDb.collection("comunidades").document(comunidad.getCif())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ContenidoLista.this, "Comunidad eliminada con éxito", Toast.LENGTH_SHORT).show();
                    comunidadesList.remove(position);
                    comunidadListAdapter.notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ContenidoLista.this, "Error al eliminar la comunidad: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    comunidadListAdapter.notifyItemChanged(position);
                });
    }

    private void showTextDialog_ConfirmLogout(){
        AlertDialog.Builder alertDialog_Builder = new AlertDialog.Builder(ContenidoLista.this);
        alertDialog_Builder.setTitle("¿Esta seguro que Cerrar Sesión?");
        alertDialog_Builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cerrarSesion();
            }
        });
        alertDialog_Builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ContenidoLista.this.finish();

            }
        });
        alertDialog_Builder.show();
    }

    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(ContenidoLista.this, Inicio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @SuppressLint("NewApi")
    private String readFile() {
        File directory = new File(ContenidoLista.this.getFilesDir(), "Database");
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