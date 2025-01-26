package com.example.partynextdoor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.partynextdoor.model.Zurka;
import com.example.partynextdoor.ui.Kreiraj;
import com.example.partynextdoor.ui.PodesavanjeFragment;
import com.example.partynextdoor.ui.Pretraga;
import com.example.partynextdoor.ui.Prijava;
import com.example.partynextdoor.repository.ZurkaRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Main extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private ImageButton btnPretraga, btnKreiraj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Zurka> zurke = new ArrayList<>();
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(zurke, this);
        recyclerView.setAdapter(adapter);

        fetchZurkeData(zurke, adapter);

        btnPretraga = findViewById(R.id.btn_pretraga);
        btnKreiraj = findViewById(R.id.btn_kreiraj);

        btnPretraga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Pretraga.class);
                startActivity(intent);
                finish();
            }
        });

        btnKreiraj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Kreiraj.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_user) {

        } else if (item.getItemId() == R.id.nav_settings) {
            replaceFragment(new PodesavanjeFragment());
        } else if (item.getItemId() == R.id.nav_share) {

        } else if (item.getItemId() == R.id.nav_about) {

        } else if (item.getItemId() == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), Prijava.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Odjavili ste se.", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchZurkeData(List<Zurka> zurke, RecyclerViewAdapter adapter) {
        ZurkaRepository zurkaRepository = new ZurkaRepository();
        zurkaRepository.fetchData(new ZurkaRepository.FetchDataCallback() {
            @Override
            public void onSuccess(List<Zurka> fetchedZurke) {
                Log.d("Main", "Fetched zurke size: " + fetchedZurke.size());
                if (fetchedZurke.isEmpty()) {
                    Log.d("Main", "Nema podataka.");
                }
                zurke.clear();
                zurke.addAll(fetchedZurke);
                Log.d("Main", "Number of zurke added to adapter: " + zurke.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Main.this, "Greška prilikom učitavanja podataka.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
