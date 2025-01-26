package com.example.partynextdoor.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.partynextdoor.R;
import com.example.partynextdoor.model.Zurka;

import javax.annotation.Nullable;

public class DetaljiDialogFragment extends DialogFragment {

    private Zurka zurka;

    public static DetaljiDialogFragment newInstance(Zurka zurka) {
        DetaljiDialogFragment fragment = new DetaljiDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("zurka", zurka);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalji_zurke, container, false);

        ImageView header = view.findViewById(R.id.header);
        TextView naziv = view.findViewById(R.id.naziv);
        TextView organizator = view.findViewById(R.id.organizator);
        TextView opis = view.findViewById(R.id.opis);
        TextView zanrMuzike = view.findViewById(R.id.zanr_muzike);
        TextView izvodjac = view.findViewById(R.id.izvodjac);
        TextView lokacija = view.findViewById(R.id.lokacija);
        TextView datum_vrijeme = view.findViewById(R.id.datum_vrijeme);
        TextView cijenaUlazniceLabel = view.findViewById(R.id.cijenaLabel);
        TextView cijenaUlaznice = view.findViewById(R.id.cijena_ulaznice);
        TextView kontaktLabel = view.findViewById(R.id.kontaktLabel);
        TextView kontakt = view.findViewById(R.id.kontakt);

        if (getArguments() != null) {
            zurka = (Zurka) getArguments().getSerializable("zurka");

            if (zurka != null) {
                naziv.setText(zurka.getNaziv());
                organizator.setText(zurka.getOrganizator());
                opis.setText(zurka.getOpis());
                zanrMuzike.setText(zurka.getZanrMuzike());
                izvodjac.setText(zurka.getIzvodjac());
                lokacija.setText(zurka.getLokacija());
                datum_vrijeme.setText(zurka.getDatum());
                datum_vrijeme.append("\n" + zurka.getVrijeme());
                kontakt.setText(zurka.getKontakt());

                Glide.with(this)
                        .load(zurka.getPozadinaUri())
                        .placeholder(R.drawable.placeholder)
                        .into(header);

                if (zurka.getCijenaUlaznice() > 0) {
                    cijenaUlaznice.setText(String.format("%.2f RSD", zurka.getCijenaUlaznice()));
                    cijenaUlazniceLabel.setVisibility(View.VISIBLE);
                    cijenaUlaznice.setVisibility(View.VISIBLE);
                } else {
                    cijenaUlaznice.setVisibility(View.GONE);
                }
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_bkg);
        }
    }

}
