package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.senai.sp.informatica.mobile.bikemobi.R;

public class AvaliacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);
    }
}
