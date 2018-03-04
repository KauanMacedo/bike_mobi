package br.senai.sp.informatica.mobile.bikemobi.model;


/**
 * Created by 43255815886 on 28/02/2018.
 */

public class Indicadores {

    private String codigo;

    private String sub_codigo;

    private int quantidade;

    public Indicadores() {

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSub_codigo() {
        return sub_codigo;
    }

    public void setSub_codigo(String sub_codigo) {
        this.sub_codigo = sub_codigo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "Indicadores{" +
                "codigo='" + codigo + '\'' +
                ", sub_codigo='" + sub_codigo + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}
