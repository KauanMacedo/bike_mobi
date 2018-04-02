package br.senai.sp.informatica.mobile.bikemobi.dao;

import android.util.Log;
import android.widget.Toast;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

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
    private String url = "http://brunohafonso-001-site1.ctempurl.com/api/cadastro/";

    public String token;
    public int idLogin;


    public LoginDao() {
    }

    public int getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(int idLogin) {
        this.idLogin = idLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Login getLogin(final int id){
        Login login = null;
        try {
            String json = new JSONParser.Consultar(url + "buscarid/" + id, new JSONParser.DataCallBack() {
                @Override
                public void setResponse(int code, String json) {
                    Log.d("BikeLog", "url: " + url + "buscarid/" + id + " . code: " + code + ". json: " + json);
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

    public int setLogin(Login login){
        final int[] resposta = new int[1];
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(login);
            JSONObject jsonSemSenha = new JSONObject(json);
            jsonSemSenha.remove("senha");
            json = jsonSemSenha.toString();
            new JSONParser.Alterar(url + "atualizar", json, new JSONParser.LocationCallBack() {
                @Override
                public void setResponse(int code, String location) {
                    resposta[0] = code;
                }
            }).execute();
        } catch (Exception e) {}
        return resposta[0];
    }

    public int postLogin(Login login){
        final int[] resposta = new int[1];
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(login);
            Log.d("BikeLog", "json: "+ json);
            new JSONParser.Incluir(url + "cadastrar", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    Log.d("BikeLog", "url: " + url + "cadastrar"+ " . code: " + code + ". json: " + json);
                    resposta[0] = code;
                }
            }).execute();
        } catch (Exception e){}
        return resposta[0];
    }

    public boolean login(Login login){
        String parser = null;
        boolean aut = false;
        try {
            json = "{\"email\":\"" + login.getEmail() + "\", \"senha\":\"" + login.getSenha() + "\"}";

            parser = new JSONParser.Incluir(url + "login", json, new JSONParser.LocationAndDataCallBack() {
                @Override
                public void setResponse(int code, String location, String json) {
                    Log.d("BikeLog", "url: " + url + "login" + " . code: " + code + ". json: " + json + " location: " + location);
                }
            }).execute().get();

            if (parser != null){
                //Log.d("BikeLog", "login: " + parser);
                JSONObject obj = new JSONObject(parser);
                token = obj.getString("acessToken");
                aut = obj.getBoolean("autenticacao");;
            }
        } catch (Exception e){
            Log.d("BikeLog", "Erro ao login. Código de Retorno: " + code + ". URL: " + url + "login" + ". Início do erro: "+ e);
        }
        return aut;
    }

    public int alterar(Login login){
        int resposta;
        if (login.getId() != 0){
            resposta = setLogin(login);
        } else {
            resposta = postLogin(login);
        }
        return resposta;
    }

}
