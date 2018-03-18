package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.senai.sp.informatica.mobile.bikemobi.model.Cidade;
import br.senai.sp.informatica.mobile.bikemobi.model.Estado;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 17/03/2018.
 */

public class IbgeDao {

    public int code;
    public String json;
    private String url = "https://servicodados.ibge.gov.br/api/v1/localidades/";

    public IbgeDao() {
    }

    public List<Cidade> listarCidades(int idEstado){
        List<Cidade> cidades = new ArrayList<>();
        try {
            String json = new JSONParser.Consultar(url + "estados/" + idEstado + "/municipios", new JSONParser.DataCallBack() {
                @Override
                public void setResponse(int code, String json) {

                }
            }).execute().get();
            if (json != null) {
                ObjectMapper mapper = new ObjectMapper();
                cidades = Arrays.asList(mapper.readValue(new StringReader(json), Cidade[].class));
            }
        } catch(Exception e){
            Log.d("BikeLog", "Erro ao listarCidades " + code + " - " + e);
        }
        return cidades;
    }
    
    public List<String> listarNomesCidades(List<Cidade> cidades){
        List<String> nomesCidades = new ArrayList<>();
        nomesCidades.add("Cidade");
        for (Cidade obj : cidades) {
            nomesCidades.add(obj.getNome());
        }
        return nomesCidades;
    }

    public List<Estado> listarEstados(){
        List<Estado> estados = new ArrayList<>();
        try {
            String json = new JSONParser.Consultar(url + "estados", new JSONParser.DataCallBack() {
                @Override
                public void setResponse(int code, String json) {

                }
            }).execute().get();
            if (json != null) {
                ObjectMapper mapper = new ObjectMapper();
                estados = Arrays.asList(mapper.readValue(new StringReader(json), Estado[].class));
            }
        } catch(Exception e){
            Log.d("BikeLog", "Erro ao listarEstados " + code + " - " + e);
        }
        return estados;
    }
    public List<String> listarNomesEstados(List<Estado> estados){
        List<String> nomesEstados = new ArrayList<>();
        nomesEstados.add("Estado");
        for (Estado obj : estados) {
            nomesEstados.add(obj.getNome());
        }
        return nomesEstados;
    }
    public List<String> listarSiglasEstados(List<Estado> estados){
        List<String> siglasEstados = new ArrayList<>();
        siglasEstados.add("Estado");
        for (Estado obj : estados) {
            siglasEstados.add(obj.getSigla());
        }
        return siglasEstados;
    }

    public List<Integer> listarIdsEstados(List<Estado> estados){
        List<Integer> idsEstado = new ArrayList<>();
        for (Estado obj : estados) {
            idsEstado.add(obj.getId());
        }
        return idsEstado;
    }

    public int getIdEstado(String sigla){
        int idEstado = 0;

        for (Estado estado: listarEstados()) {
            if (estado.getSigla().equals(sigla)){
                idEstado = estado.getId();
            }
        }
        return idEstado;
    }

}
