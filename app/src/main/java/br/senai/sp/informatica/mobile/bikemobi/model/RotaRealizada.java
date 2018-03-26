package br.senai.sp.informatica.mobile.bikemobi.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rodol on 24/03/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RotaRealizada {
    public double latInicio;
    public double lngInicio;
    public double latFim;
    public double lngFim;
    public String duracaoString;
    public int duracaoInt;
    public int kilometros;
    public int idLogin;
    public int idRotaPesquisada;

    public double getLatInicio() {
        return latInicio;
    }

    public void setLatInicio(double latInicio) {
        this.latInicio = latInicio;
    }

    public double getLngInicio() {
        return lngInicio;
    }

    public void setLngInicio(double lngInicio) {
        this.lngInicio = lngInicio;
    }

    public double getLatFim() {
        return latFim;
    }

    public void setLatFim(double latFim) {
        this.latFim = latFim;
    }

    public double getLngFim() {
        return lngFim;
    }

    public void setLngFim(double lngFim) {
        this.lngFim = lngFim;
    }

    public String getDuracaoString() {
        return duracaoString;
    }

    public void setDuracaoString(String duracaoString) {
        this.duracaoString = duracaoString;
    }

    public int getDuracaoInt() {
        return duracaoInt;
    }

    public void setDuracaoInt(int duracaoInt) {
        this.duracaoInt = duracaoInt;
    }

    public int getKilometros() {
        return kilometros;
    }

    public void setKilometros(int kilometros) {
        this.kilometros = kilometros;
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    public int getIdRotaPesquisada() {
        return idRotaPesquisada;
    }

    public void setIdRotaPesquisada(int idRotaPesquisada) {
        this.idRotaPesquisada = idRotaPesquisada;
    }
}
