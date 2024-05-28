package com.example.communityinfo.Admins.ActivitiesAdmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.example.communityinfo.R;
import com.example.communityinfo.databinding.ActivityPrincipalBinding;

public class PrincipalAdmin extends AppCompatActivity {
    private ActivityPrincipalBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Actividad PrincipalAdmin que contiene los fragmentos y el menu de navegación
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.FragmentoComunicados, R.id.FragmentoReservas, R.id.FragmentoLogOut
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_principal);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}