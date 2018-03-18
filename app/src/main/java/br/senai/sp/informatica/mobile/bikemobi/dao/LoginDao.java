package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.StringReader;

import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.util.JSONParser;

/**
 * Created by rodol on 17/03/2018.
 */

public class LoginDao {
    public static LoginDao instance = new LoginDao();

    public int code;
    public String json;
    private String url = "http://brunohafonso-001-site1.ctempurl.com//api/cadastro/";

    public LoginDao() {
    }

    public Login getLogin(Long id){
        Login login = null;
        try {
            String json = new JSONParser.Consultar(url + "buscarid/1", new JSONParser.DataCallBack() {
                @Override
                public void setResponse(int code, String json) {
                    Log.d("BikeLog", "url: " + url + "buscarid/1" + " . code: " + code + ". json: " + json);
                }
            }).execute().get();
            if (json != null){
                ObjectMapper mapper = new ObjectMapper();
                login = mapper.readValue(new StringReader(json), Login.class);
            }

        } catch (Exception e){
            Log.d("BikeLog", "Erro ao getLogin. Código de Retorno: " + code + ". URL: " + url + "buscarid/1" + ". Início do erro: "+ e);
        }

        return login;
    }

    public void setLogin(Login login){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(login);
            new JSONParser.Alterar(url + "atualizar", json, new JSONParser.LocationCallBack() {
                @Override
                public void setResponse(int code, String location) {

                }
            }).execute();
        } catch (Exception e) {}
    }
}
