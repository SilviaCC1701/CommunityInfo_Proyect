package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.communityinfo.R;
import com.example.communityinfo.databinding.ActivityPrincipalAdminBinding;

public class PrincipalAdmin extends AppCompatActivity {
    private ActivityPrincipalAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Actividad PrincipalAdmin que contiene los fragmentos y el menu de navegación
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.FragmentoComunicadosAdmin, R.id.FragmentoReservasAdmin, R.id.FragmentoGestionAdmin
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_principalAdmin);
        NavigationUI.setupWithNavController(binding.navViewAdmin, navController);
    }
}