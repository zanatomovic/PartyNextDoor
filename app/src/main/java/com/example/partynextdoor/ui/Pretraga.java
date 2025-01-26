package com.example.partynextdoor.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Locale;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.partynextdoor.ui.map.LocationAdapter;
import com.example.partynextdoor.Main;
import com.example.partynextdoor.R;
import com.example.partynextdoor.model.Zurka;
import com.example.partynextdoor.repository.ZurkaRepository;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;

import java.util.ArrayList;
import java.util.List;

public class Pretraga extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private TextView datumTextView;
    private Spinner zanrMuzikeSpinner;
    private CheckBox ulazniceCheckBox;
    private Boolean ulaznice;
    private Button btnPretrazi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretraga);

        autoCompleteTextView = findViewById(R.id.autocomplete_location);
        datumTextView = findViewById(R.id.datum);
        zanrMuzikeSpinner = findViewById(R.id.zanr_muzike);
        ulazniceCheckBox = findViewById(R.id.ulaznice_checkBox);
        btnPretrazi = findViewById(R.id.btn_pretrazi);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Pretraga žurke");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LocationAdapter locationAdapter = new LocationAdapter(this);
        locationAdapter.requestLocationPermissions(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        locationAdapter.setupAutocomplete(autoCompleteTextView, token);

        ZurkaRepository zurkaRepository = new ZurkaRepository();

        datumTextView.setOnClickListener(v -> openDatePickerDialog());

        ulaznice = false;
        ulazniceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ulaznice = isChecked;
        });

        btnPretrazi.setOnClickListener(v -> {
            String lokacija = autoCompleteTextView.getText().toString().trim();
            String datum = datumTextView.getText().toString().trim();
            String zanrMuzike = zanrMuzikeSpinner.getSelectedItem().toString();

            if (lokacija.isEmpty() || datum.isEmpty() || zanrMuzike.isEmpty()) {
                Toast.makeText(this, "Popunite sva obavezna polja.", Toast.LENGTH_SHORT).show();
                return;
            }

            ZurkaRepository repository = new ZurkaRepository();
            repository.fetchDataFiltered( lokacija, datum, zanrMuzike, ulaznice, new ZurkaRepository.FetchDataCallback() {
                @Override
                public void onSuccess(List<Zurka> fetchedZurke) {
                    Log.d("Main", "Fetched zurke size: " + fetchedZurke.size());
                    if (fetchedZurke.isEmpty()) {
                        Toast.makeText(Pretraga.this, "Nema rezultata za zadate parametre.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Pretraga.this, RezultatiPretrageActivity.class);
                        intent.putExtra("rezultati", new ArrayList<>(fetchedZurke));
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(Pretraga.this, "Došlo je do greške: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), Main.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void openDatePickerDialog() {
        DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            String formattedDate = String.format("%04d.%02d.%02d", year, month + 1, day);
            datumTextView.setText(formattedDate);
        }, 2025, 0, 15);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), Main.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}