//package br.senai.sp.informatica.mobile.bikemobi.rest;
//
//
//import com.google.gson.Gson;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by Tecnico_Manha on 12/06/2017.
// */
//
//public class JsonParser<T> {
//
//    final Class<T> tipoObjeto;
//
//    // cria uma instancia da lib Gson
//    Gson gson = new Gson();
//
//    // construtor
//    public JsonParser(Class<T> tipoObjeto) {
//        this.tipoObjeto = tipoObjeto;
//    }
//
//    // converte json para objeto
//    public T toObject(String json) {
//        return gson.fromJson(json, tipoObjeto);
//    }
//
//    // converte objeto para json
//    public String fromObject(T objeto) {
//        return gson.toJson(objeto);
//    }
//
//    // retorna uma lista de objetos
//    public List<T> toList(String json, Class<T[]> objeto) {
//        return Arrays.asList(gson.fromJson(json, objeto));
//    }
//}