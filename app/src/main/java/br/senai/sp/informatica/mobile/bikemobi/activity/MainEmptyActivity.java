package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.util.SaveSharedPreference;

/**
 * Created by rodol on 31/03/2018.
 */

public class MainEmptyActivity extends AppCompatActivity {

    //private LoginDao loginDao = LoginDao.instance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        //Log.d("BikeLog", "getid: " + SaveSharedPreference.getId(getApplicationContext()).isEmpty());
        //if (loginDao.getToken() != null){
        if (SaveSharedPreference.getId(getApplicationContext()).isEmpty()){
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
