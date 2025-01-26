package com.example.partynextdoor.model;

import android.net.Uri;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Zurka implements Serializable {
    private String naziv, organizator, zanrMuzike, izvodjac, opis, lokacija, datum, vrijeme,
            kontakt, pozadinaUri, userId;
    private Double cijenaUlaznice;

    public Zurka() {
    }

    public Zurka(String naziv, String organizator, String zanrMuzike, String izvodjac, String opis,
                 String lokacija, String datum, String vrijeme, Double cijenaUlaznice,
                 String kontakt, String pozadinaUri) {
        this.naziv = naziv;
        this.organizator = organizator;
        this.zanrMuzike = zanrMuzike;
        this.izvodjac = izvodjac;
        this.opis = opis;
        this.lokacija = lokacija;
        this.datum = datum;
        this.vrijeme = vrijeme;
        this.cijenaUlaznice = cijenaUlaznice;
        this.kontakt = kontakt;
        this.pozadinaUri = pozadinaUri;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOrganizator() {
        return organizator;
    }

    public void setOrganizator(String organizator) {
        this.organizator = organizator;
    }

    public String getZanrMuzike() {
        return zanrMuzike;
    }

    public void setZanrMuzike(String zanrMuzike) {
        this.zanrMuzike = zanrMuzike;
    }

    public String getIzvodjac() {
        return izvodjac;
    }

    public void setIzvodjac(String izvodjac) {
        this.izvodjac = izvodjac;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getLokacija() {
        return lokacija;
    }

    public void setLokacija(String lokacija) {
        this.lokacija = lokacija;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getKontakt() {
        return kontakt;
    }

    public void setKontakt(String kontakt) {
        this.kontakt = kontakt;
    }

    public Double getCijenaUlaznice() {
        return cijenaUlaznice;
    }

    public void setCijenaUlaznice(Double cijenaUlaznice) {
        this.cijenaUlaznice = cijenaUlaznice;
    }

    public Uri getPozadinaUri() {
        return Uri.parse(pozadinaUri);
    }

    public void setPozadinaUri(String pozadinaUri) {
        this.pozadinaUri = pozadinaUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

