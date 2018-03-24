package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

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

    public void postRotaReal(RotaRealizada rota){
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(rota);
            new JSONParser.Incluir(url + "Cadastrar", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    Log.d("BikeLog", "url: " + url + "cadastrar" + " . code: " + code + ". json: " + json);
                }
            });

        } catch (IOException e){
            Log.d("BikeLog", "Erro ao postRotaRealizada. Código de Retorno: " + code + ". URL: " + url + "cadastrar" + ". Início do erro: "+ e);
        }
    }
}
