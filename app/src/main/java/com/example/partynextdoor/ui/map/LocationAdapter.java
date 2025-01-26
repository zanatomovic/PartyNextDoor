package com.example.partynextdoor.ui.map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Context context;
    private PlacesClient placesClient;

    public LocationAdapter(Context context) {
        this.context = context;

        if (!Places.isInitialized()) {
            String apiKey = getApiKey();
            if (apiKey != null) {
                Places.initialize(context, apiKey);
                placesClient = Places.createClient(context);
            } else {
                Log.e("LocationAdapter", "Google Places API Key is missing.");
            }
        }

    }

    public void requestLocationPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d("LocationPermission", "Dozvola za lokaciju već odobrena.");
        }
    }

    public void setupAutocomplete(final AutoCompleteTextView autoCompleteTextView, AutocompleteSessionToken token) {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                            .setQuery(s.toString())
                            .setSessionToken(token)
                            .build();

                    placesClient.findAutocompletePredictions(request)
                            .addOnSuccessListener(response -> {
                                List<String> suggestions = new ArrayList<>();
                                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                    suggestions.add(prediction.getPrimaryText(null).toString());
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        context, android.R.layout.simple_dropdown_item_1line, suggestions);
                                autoCompleteTextView.setAdapter(adapter);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Greška: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public String getApiKey() {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}