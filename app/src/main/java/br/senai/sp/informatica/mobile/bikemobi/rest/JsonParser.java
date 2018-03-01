package br.senai.sp.informatica.mobile.bikemobi.rest;

import com.google.gson.Gson;

/**
 * Created by 43255815886 on 28/02/2018.
 */

public class JsonParser<T> {

    final Class<T> tipoObjeto;

    Gson gson = new Gson();

    public JsonParser(Class<T> tipoObjeto) {
        this.tipoObjeto = tipoObjeto;
    }
}
