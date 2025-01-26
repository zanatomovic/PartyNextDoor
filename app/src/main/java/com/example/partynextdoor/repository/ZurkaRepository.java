package com.example.partynextdoor.repository;

import android.util.Log;

import com.example.partynextdoor.model.Zurka;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ZurkaRepository {
    private FirebaseFirestore db;

    public ZurkaRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void fetchData(FetchDataCallback callback) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        String danasnjiDatum = dateFormat.format(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 14);
        String datumZaDveNedelje = dateFormat.format(calendar.getTime());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query = db.collection("zurke")
                .whereGreaterThanOrEqualTo("datum", danasnjiDatum)
                .whereLessThanOrEqualTo("datum", datumZaDveNedelje);

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Zurka> zurkeList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d("Firestore Document", document.getData().toString());
                        Zurka zurka = document.toObject(Zurka.class);
                        zurkeList.add(zurka);
                    }
                    callback.onSuccess(zurkeList);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void fetchDataFiltered(String lokacija, String datum, String zanrMuzike, boolean filterUlaznice, FetchDataCallback callback) {
        Query query = db.collection("zurke")
                .whereEqualTo("lokacija", lokacija)
                .whereEqualTo("datum", datum)
                .whereEqualTo("zanrMuzike", zanrMuzike);

        if (filterUlaznice) {
            query = query.whereGreaterThan("cijenaUlaznice", 0);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d("ZurkaRepository", "Pretraga počinje za lokaciju: " + lokacija + ", datum: " + datum + ", žanr: " + zanrMuzike);
                    List<Zurka> zurkeList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Zurka zurka = document.toObject(Zurka.class);
                        zurkeList.add(zurka);
                        Log.d("ZurkaRepository", "Žurka: " + zurka.getNaziv());
                    }
                    callback.onSuccess(zurkeList);
                })
                .addOnFailureListener(e -> {
                    Log.e("ZurkaRepository", "Greška pri pretrazi: " + e.getMessage());
                    callback.onFailure(e);
                });
    }

    public void deleteZurka(String zurkaId) {
        db.collection("zurke").document(zurkaId).delete();
    }

    public void createZurka(Zurka zurka, FetchDataCallback callback) {
        db.collection("zurke").add(zurka)
                .addOnSuccessListener(documentReference -> {
                    if (callback != null) {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }

    public void updateZurka(String zurkaId, Zurka updatedZurka) {
        db.collection("zurke").document(zurkaId).set(updatedZurka);
    }

    public interface FetchDataCallback {
        void onSuccess(List<Zurka> zurke);
        void onFailure(Exception e);
    }
}

