package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;

import br.senai.sp.informatica.mobile.bikemobi.model.RotaRealizada;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 24/03/2018.
 */

public class RotaRealizadaDao {
    public static RotaRealizadaDao instance = new RotaRealizadaDao();

    public int code;
    public String json;
    private String url = "http://brunohafonso-001-site1.ctempurl.com/api/RotaRealizada/";

    private RotaRealizadaDao() {
    }

    public int postRotaReal(RotaRealizada rota){
        int resposta = 0;
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            json = objectMapper.writeValueAsString(rota);

            String jsonParser = new JSONParser.Incluir(url + "cadastrar", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    //Log.d("BikeLog", "url: " + url + "cadastrar" + " . code: " + code + ". json: " + json + " location: " + location);
                }
            }).execute().get();
            //Log.d("BikeLog", "jsonParser: "+ jsonParser);
            if (jsonParser != null) {
                JSONObject obj = new JSONObject(jsonParser);
                resposta = obj.getInt("ultimoIdCadastrado");
            }
            //Log.d("BikeLog", "ultimoIdCadastrado: "+ resposta);

        }catch (Exception e){
            Log.d("BikeLog", "Erro ao postRotaPesquisada. Código de Retorno: " + code + ". URL: " + url + "cadastrar" + ". Início do erro: "+ e);
        }
        return resposta;
    }
}
