package br.senai.sp.informatica.mobile.bikemobi.model;


import java.sql.Date;

/**
 * Created by 43255815886 on 28/02/2018.
 */

public class Perfil {

    private int id;

    private String nome;

    private Date dataNascimento;

    private String localidade;

    private String bio;

    private String avatarUrl;

    private Date dataCriacao;

    private int loginId;

    private String login;

    private String estado;

    private String cidade;

    private String idLogin;

    private Date atualizadoEm;

    private Date criadoEm;

    private int qtdAtualizacoes;

    public int getQtdAtualizacoes() {
        return qtdAtualizacoes;
    }

    public void setQtdAtualizacoes(int qtdAtualizacoes) {
        this.qtdAtualizacoes = qtdAtualizacoes;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    private Date getAtualizadoEm() {
        return atualizadoEm;
    }

    private String atualizadoPor;

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(String idLogin) {
        this.idLogin = idLogin;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Perfil() {

    }

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

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    @Override
    public String toString() {
        return "Perfil{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + dataNascimento +
                ", localidade='" + localidade + '\'' +
                ", bio='" + bio + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", loginId=" + loginId +
                '}';
    }
}
