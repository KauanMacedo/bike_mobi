package br.senai.sp.informatica.mobile.bikemobi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 43255815886 on 28/02/2018.
 */

public class Login {

    @SerializedName("id")
    private int id;

    @SerializedName("nomeUsuario")
    private String nomeUsuario;

    @SerializedName("email")
    private String email;

    @SerializedName("senha")
    private String senha;

    public Login() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
