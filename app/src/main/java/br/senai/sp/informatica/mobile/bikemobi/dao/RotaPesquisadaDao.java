package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.StringReader;

import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.model.RotaPesquisada;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 21/03/2018.
 */

public class RotaPesquisadaDao {
    public static RotaPesquisadaDao instance = new RotaPesquisadaDao();

    public int code;
    public String json;
    private String url = "http://brunohafonso-001-site1.ctempurl.com/api/RotaPesquisada/";

    private RotaPesquisadaDao() {
    }

    public void postRotaPesquisada(RotaPesquisada rota){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(rota);
            new JSONParser.Incluir(url + "cadastrar", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    Log.d("BikeLog", "url: " + url + "cadastrar" + " . code: " + code + ". json: " + json + " location: " + location);
                }
            }).execute();

        }catch (Exception e){
            Log.d("BikeLog", "Erro ao postRotaPesquisada. Código de Retorno: " + code + ". URL: " + url + "cadastrar" + ". Início do erro: "+ e);
        }
    }


}
