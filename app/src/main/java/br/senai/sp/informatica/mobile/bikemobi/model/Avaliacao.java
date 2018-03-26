package br.senai.sp.informatica.mobile.bikemobi.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rodol on 25/03/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Avaliacao {
    public int avTrajeto;
    public int avSeguranca;
    public int idRotaRealizada;
    public int idLogin;
    public int id;

    public Avaliacao() {
    }

    public int getAvTrajeto() {
        return avTrajeto;
    }

    public void setAvTrajeto(int avTrajeto) {
        this.avTrajeto = avTrajeto;
    }

    public int getAvSeguranca() {
        return avSeguranca;
    }

    public void setAvSeguranca(int avSeguranca) {
        this.avSeguranca = avSeguranca;
    }

    public int getIdRotaRealizada() {
        return idRotaRealizada;
    }

    public void setIdRotaRealizada(int idRotaRealizada) {
        this.idRotaRealizada = idRotaRealizada;
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
