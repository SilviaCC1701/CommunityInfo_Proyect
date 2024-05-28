package com.example.communityinfo.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.communityinfo.Inicio;
import com.example.communityinfo.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogOut extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogOut() { } // Se requiere de un constructor vacio.

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLogout.
     */
    // TODO: Rename and change types and number of parameters
    public static LogOut newInstance(String param1, String param2) {
        LogOut fragment = new LogOut();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseAuth.getInstance().signOut();
        //Intent intent=new Intent(getContext(), Inicio.class);
        //startActivity(intent);
        //getActivity().finish();
        showTextDialog_ConfirmDelete();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // view = inflater.inflate(R.layout.fragment_logout, container, false);
        // return view;

        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    private void showTextDialog_ConfirmDelete(){
        AlertDialog.Builder alertDialog_Builder = new AlertDialog.Builder(getContext());
        alertDialog_Builder.setTitle("¿Esta seguro que Cerrar Sesión?");
        alertDialog_Builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                dialog.cancel();

                Intent intent=new Intent(getContext(), Inicio.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        alertDialog_Builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                Intent intent=new Intent(getContext(), Principal.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        alertDialog_Builder.show();
    }
}