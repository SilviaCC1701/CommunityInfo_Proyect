package com.example.communityinfo.Admins.FragmentsAdmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityinfo.Adapters_RecyclerView.ListaGestionAdapter;
import com.example.communityinfo.R;

import java.util.ArrayList;
import java.util.List;


public class GestionAdmin extends Fragment {
    private View view;
    private RecyclerView listasRecyclerView;
    private ListaGestionAdapter listaGestionAdapter;
    List<String> listadoGestionList;
    public GestionAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Listado de items/herramientas del rol admin
        listadoGestionList = new ArrayList<String>();
        listadoGestionList.add("Residentes");
        listadoGestionList.add("Comunidades");
        listadoGestionList.add("LogOut");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gestion_admin, container, false);

        // Sobre el RecyclerView
        listasRecyclerView = view.findViewById(R.id.listasGestion);
        listasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Crea adapter y establece lista en el RecyclerList
        listaGestionAdapter = new ListaGestionAdapter(getContext(), listadoGestionList);
        listasRecyclerView.setAdapter(listaGestionAdapter);

        return view;
    }
}