package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import br.senai.sp.informatica.mobile.bikemobi.model.Avaliacao;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 25/03/2018.
 */

public class AvaliacaoDao {
    public static AvaliacaoDao instance = new AvaliacaoDao();

    public int code;
    public String json;
    private String url = "http://brunohafonso-001-site1.ctempurl.com/api/Avaliacao/";

    private AvaliacaoDao() {
    }

    public void postAvaliacao(Avaliacao aval){
        try{
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(aval);

            new JSONParser.Incluir(url + "cadastrar", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    Log.d("BikeLog", "url: " + url + "cadastrar" + " . code: " + code + ". json: " + json + " location: " + location);
                }
            }).execute();

        } catch (Exception e){
            Log.d("BikeLog", "Erro ao postRotaPesquisada. Código de Retorno: " + code + ". URL: " + url + "cadastrar" + ". Início do erro: "+ e);
        }
    }
}
