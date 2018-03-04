package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.StringReader;

import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.rest.JSONParser;

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

                }
            }).execute().get();



            if (!json.isEmpty()){
                ObjectMapper mapper = new ObjectMapper();
                perfil = mapper.readValue(new StringReader(json), Perfil.class);
            }

        } catch(Exception e){
            Log.d("LogBike", "Erro ao getPerfil" + e);
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
                    Log.d("AlbumResponse", "code: " + String.valueOf(code));
                }
            }).execute();
        } catch (Exception e){Log.d("BikeTech", "Erro ao atualizar.");}
    }
}
