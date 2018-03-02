package br.senai.sp.informatica.mobile.bikemobi.util;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by 43255815886 on 27/02/2018.
 */

public interface RestInterface {

    @Headers({"Content-Type: application/json"})
    @GET("perfil/todos")
    Call<ResponseBody> perfil();

}