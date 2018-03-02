package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.model.Indicadores;
import br.senai.sp.informatica.mobile.bikemobi.util.RestInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PerfilActivity extends BaseActivity {

    // Variáveis API
    static boolean active = false;
    Indicadores indicadores;
    String tokenAuth;
    String teste;
    String codigo = "";
    String sub_codigo;
    int quantidade = 0;

    // Variáveis tela
    NavigationView navigationView;

    JSONObject jsonobject;

    // Construtor API
    private static OkHttpClient defaultHttpClient = new OkHttpClient();

    String nome, dataNascimento, localidade, bio, avatarUrl, dataCriacao, loginId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        inicializarComponentes();
    }

    public void inicializarComponentes() {
        setupNavDrawer();
        setupToolBar();
        apiPerfil();
    }

    private void apiPerfil() {

        SharedPreferences preferences = getSharedPreferences("bikemobi", MODE_PRIVATE);
        tokenAuth = preferences.getString("token", "");

        // OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(1, )

        Retrofit.Builder builder = new Retrofit.Builder().client(defaultHttpClient).baseUrl("http://192.168.4.4:5000/api/").addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        RestInterface restInterface = retrofit.create(RestInterface.class);

        Call<ResponseBody> call = restInterface.perfil();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        Log.d("response", "response");
                        teste = response.body().string();
                        Log.d("ENTROU", teste);
                        JSONObject object = new JSONObject(teste);
                        JSONArray jsonarray = object.getJSONArray("perfil");

                        for (int i = 0; i < jsonarray.length(); i++) {
                            jsonobject = jsonarray.getJSONObject(i);
                        }

                        nome = jsonobject.getString("nome");
                        dataNascimento = jsonobject.getString("dataNascimento");
                        localidade = jsonobject.getString("localidade");
                        bio = jsonobject.getString("bio");
                        avatarUrl = jsonobject.getString("avatarUrl");
                        dataCriacao = jsonobject.getString("dataCriacao");
                        loginId = jsonobject.getString("loginId");

                        Log.d("Buscou o nome", nome);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d("erra: ", t.getMessage());
            }
        });
    }
}
