package br.senai.sp.informatica.mobile.bikemobi.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rodol on 18/03/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Estado {

    public int id;
    public String sigla;
    public String nome;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
