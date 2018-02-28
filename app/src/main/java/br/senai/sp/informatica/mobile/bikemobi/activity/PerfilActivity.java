package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import br.senai.sp.informatica.mobile.bikemobi.R;

public class PerfilActivity extends BaseActivity {

    TextView textViewNome;
    TextView textViewDataNascimento;
    TextView textViewLocalidade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        inicializarComponentes();
    }

    public void inicializarComponentes() {

        textViewNome = (TextView) findViewById(R.id.textNomePerfil);
        textViewDataNascimento = (TextView) findViewById(R.id.textDataNascimentoPerfil);
        textViewLocalidade = (TextView) findViewById(R.id.textLocalidadePerfil);

    }
}
