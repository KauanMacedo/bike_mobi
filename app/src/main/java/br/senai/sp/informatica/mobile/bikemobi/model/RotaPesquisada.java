package br.senai.sp.informatica.mobile.bikemobi.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by rodol on 21/03/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RotaPesquisada {

    public long distancia;
    public String duracao;
    public String destinoEnd;
    public double destinoLat;
    public double destinoLng;
    public String origemEnd;
    public double origemLat;
    public double origemLng;
    public String polylinePoints;
    public int idLogin;
    public Date criadoEm;

    public long getDistancia() {
        return distancia;
    }

    public void setDistancia(long distancia) {
        this.distancia = distancia;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getDestinoEnd() {
        return destinoEnd;
    }

    public void setDestinoEnd(String destinoEnd) {
        this.destinoEnd = destinoEnd;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLng() {
        return destinoLng;
    }

    public void setDestinoLng(double destinoLng) {
        this.destinoLng = destinoLng;
    }

    public String getOrigemEnd() {
        return origemEnd;
    }

    public void setOrigemEnd(String origemEnd) {
        this.origemEnd = origemEnd;
    }

    public double getOrigemLat() {
        return origemLat;
    }

    public void setOrigemLat(double origemLat) {
        this.origemLat = origemLat;
    }

    public double getOrigemLng() {
        return origemLng;
    }

    public void setOrigemLng(double origemLng) {
        this.origemLng = origemLng;
    }

    public String getPolylinePoints() {
        return polylinePoints;
    }

    public void setPolylinePoints(String polylinePoints) {
        this.polylinePoints = polylinePoints;
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    @JsonIgnore
    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }
}
