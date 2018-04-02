package br.senai.sp.informatica.mobile.bikemobi.model;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created by 43255815886 on 28/02/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Login {

    private int id;

    private String nomeUsuario;

    private String email;

    private String senha;

    private Perfil perfil;

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

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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
