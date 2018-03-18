package br.senai.sp.informatica.mobile.bikemobi.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by rodol on 17/03/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cidade {
    public int id;
    public String nome;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
