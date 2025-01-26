package com.example.partynextdoor.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.partynextdoor.ui.map.LocationAdapter;
import com.example.partynextdoor.Main;
import com.example.partynextdoor.R;
import com.example.partynextdoor.model.Zurka;
import com.example.partynextdoor.repository.ZurkaRepository;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class Kreiraj extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText nazivEditText, vrijemeEditText, cijenaEditText, organizatorEditText, izvodjacEditText, opisEditText, kontaktEditText;
    private Spinner zanrMuzikeSpinner;
    private TextView datumTextView, pozadinaTextView;
    private ImageView pozadina;
    private CheckBox ulazniceCheckBox;
    private Uri pozadinaUri;
    private Button btnKreiraj;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    pozadinaUri = uri;
                    pozadina.setVisibility(View.VISIBLE);
                    pozadina.setImageURI(uri);
                } else {
                    Log.d("PhotoPicker", "Nije odabrana pozadina");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj);

        autoCompleteTextView = findViewById(R.id.autocomplete_location);
        nazivEditText = findViewById(R.id.naziv);
        datumTextView = findViewById(R.id.datum);
        vrijemeEditText = findViewById(R.id.vrijeme);
        organizatorEditText = findViewById(R.id.organizator);
        zanrMuzikeSpinner = findViewById(R.id.zanr_muzike);
        izvodjacEditText = findViewById(R.id.izvodjac);
        opisEditText = findViewById(R.id.opis);
        ulazniceCheckBox = findViewById(R.id.ulaznice_checkBox);
        cijenaEditText = findViewById(R.id.cijena_ulaznice);
        kontaktEditText = findViewById(R.id.kontakt);
        pozadinaTextView = findViewById(R.id.pozadina);
        pozadina = findViewById(R.id.pozadina_view);
        btnKreiraj = findViewById(R.id.btn_kreiraj);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Kreiranje žurke");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LocationAdapter locationAdapter = new LocationAdapter(this);
        locationAdapter.requestLocationPermissions(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        locationAdapter.setupAutocomplete(autoCompleteTextView, token);

        datumTextView.setOnClickListener(v -> openDatePickerDialog());

        ulazniceCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            cijenaEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        pozadinaTextView.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        btnKreiraj.setOnClickListener(v -> {
            String naziv = nazivEditText.getText().toString().trim();
            String organizator = organizatorEditText.getText().toString().trim();
            String zanrMuzike = zanrMuzikeSpinner.getSelectedItem().toString();
            String izvodjac = izvodjacEditText.getText().toString().trim();
            String opis = opisEditText.getText().toString().trim();
            String lokacija = autoCompleteTextView.getText().toString().trim();
            String datum = datumTextView.getText().toString().trim();
            String vrijeme = vrijemeEditText.getText().toString().trim();
            String cijenaText = cijenaEditText.getText().toString().trim();
            String kontakt = kontaktEditText.getText().toString().trim();

            if (lokacija.isEmpty() || naziv.isEmpty() || datum.isEmpty() || vrijeme.isEmpty() || organizator.isEmpty() || izvodjac.isEmpty() || opis.isEmpty() || kontakt.isEmpty()) {
                Toast.makeText(this, "Popunite sva obavezna polja.", Toast.LENGTH_SHORT).show();
                return;
            }

            double cijenaUlaznice = 0;
            if (cijenaEditText.getVisibility() == View.VISIBLE) {
                try {
                    cijenaUlaznice = Double.parseDouble(cijenaText);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Cijena ulaznice nije validan broj.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String slikaUri = (pozadinaUri != null) ? pozadinaUri.toString() : "";
            if (slikaUri.isEmpty()) {
                Toast.makeText(this, "Molimo odaberite pozadinsku sliku.", Toast.LENGTH_SHORT).show();
                return;
            }

            Zurka novaZurka = new Zurka(naziv, organizator, zanrMuzike, izvodjac, opis, lokacija, datum, vrijeme, cijenaUlaznice, kontakt, slikaUri);

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String userId = auth.getCurrentUser().getUid();
            novaZurka.setUserId(userId);

            ZurkaRepository repository = new ZurkaRepository();
            repository.createZurka(novaZurka, new ZurkaRepository.FetchDataCallback() {
                @Override
                public void onSuccess(List<Zurka> fetchedZurke) {
                    Toast.makeText(Kreiraj.this, "Žurka je uspješno dodana!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(Kreiraj.this, "Greška pri dodavanju žurke: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
