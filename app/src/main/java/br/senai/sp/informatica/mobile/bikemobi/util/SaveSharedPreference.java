package br.senai.sp.informatica.mobile.bikemobi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;


/**
 * Created by rodol on 31/03/2018.
 */

public class SaveSharedPreference {
    static final String PREF_USUARIO = "nome_user";
    static final String PREF_EMAIL = "email_user";
    static final String PREF_SENHA = "senha_user";
    static final String PREF_ID = "id_user";
    static final String PREF_TOKEN = "token_user";

    static SharedPreferences getSharedPreferences(Context cont){
        return PreferenceManager.getDefaultSharedPreferences(cont);
    }

    public static void setUser(Context cont, String nome, String email, String id, String token){
        Editor editor = getSharedPreferences(cont).edit();
        editor.putString(PREF_USUARIO, nome);
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_ID, id);
        editor.putString(PREF_TOKEN, token);
        editor.commit();
    }

    public static void setSenha(Context cont, String senha){
        Editor editor = getSharedPreferences(cont).edit();
        editor.putString(PREF_SENHA, senha);
        editor.commit();
    }

    public static String getUser(Context cont){
        return getSharedPreferences(cont).getString(PREF_USUARIO, "");
    }
    public static String getEmail(Context cont){
        return getSharedPreferences(cont).getString(PREF_EMAIL, "");
    }
    public static String getSenha(Context cont){
        return getSharedPreferences(cont).getString(PREF_SENHA, "");
    }
    public static String getId(Context cont){
        return getSharedPreferences(cont).getString(PREF_ID, "");
    }
    public static String getToken(Context cont){
        return getSharedPreferences(cont).getString(PREF_TOKEN, "");
    }
    public static void clearUserName(Context cont)
    {
        Editor editor = getSharedPreferences(cont).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }

}
