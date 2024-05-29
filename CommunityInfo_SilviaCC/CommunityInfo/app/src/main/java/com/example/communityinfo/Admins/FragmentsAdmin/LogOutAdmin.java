package com.example.communityinfo.Admins.FragmentsAdmin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.communityinfo.Inicio;
import com.example.communityinfo.R;
import com.example.communityinfo.Users.ActivitiesUser.Principal;
import com.google.firebase.auth.FirebaseAuth;
public class LogOutAdmin extends Fragment {
    private View view;
    public LogOutAdmin() { } // Se requiere de un constructor vacio.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservas_admin, container, false);


        //showTextDialog_ConfirmDelete();
        return view;
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