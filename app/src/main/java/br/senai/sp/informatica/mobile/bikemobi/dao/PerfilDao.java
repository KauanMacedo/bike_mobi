package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.StringReader;

import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 02/03/2018.
 */

public class PerfilDao {
    public static PerfilDao instance = new PerfilDao();

    public int code;
    public String json;
    private String url = "http://brunohafonso-001-site1.ctempurl.com/api/perfil/";

    private PerfilDao() {
    }

    public Perfil getPerfil(Long id){
        Perfil perfil = null;
        try{
            String json = new JSONParser.Consultar(url + "buscarid/1", new JSONParser.DataCallBack() {
                @Override
                public void setResponse(int code, String json) {
                    Log.d("BikeLog", "url: " + url + "buscarid/1" + " . code: " + code + ". json: " + json);
                }
            }).execute().get();



            if (!json.isEmpty()){
                ObjectMapper mapper = new ObjectMapper();
                perfil = mapper.readValue(new StringReader(json), Perfil.class);
            }

        } catch(Exception e){
            Log.d("BikeLog", "Erro ao getPerfil. Código de Retorno: " + code + ". URL: " + url + "buscarid/1" + ". Início do erro: "+ e);
        }
        return perfil;
    }
    public void setPerfil(Perfil perfil){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(perfil);
            new JSONParser.Alterar(url + "atualizar", json, new JSONParser.LocationCallBack() {
                @Override
                public void setResponse(int code, String location) {
                    Log.d("BikeLog", "code: " + String.valueOf(code));
                }
            }).execute();
        } catch (Exception e){Log.d("BikeLog", "Erro ao atualizar." + e);}
    }
}
