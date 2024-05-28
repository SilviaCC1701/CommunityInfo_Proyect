package com.example.communityinfo.Users.ActivitiesUser;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.communityinfo.R;
import com.example.communityinfo.databinding.ActivityPrincipalBinding;

public class Principal extends AppCompatActivity {
    private ActivityPrincipalBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Actividad Principal que contiene los fragmentos y el menu de navegación
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