package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;

public class PerfilActivity extends AppCompatActivity{

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;

    private TextView tvNome;
    private TextView tvData;
    private TextView tvLocalidade;

    private final static int ATUALIZA_PERFIL = 0;

    private final static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        tvNome = findViewById(R.id.textNomePerfil);
        tvData = findViewById(R.id.textDataNascimentoPerfil);
        tvLocalidade = findViewById(R.id.textLocalidadePerfil);


        getDadosPerfil();

        //inicializarComponentes();
    }
    @SuppressLint("SetTextI18n")
    public void getDadosPerfil() {
        perfil = dao.getPerfil(1l);

        if (perfil != null) {
            tvNome.setText(perfil.getNome());

            Calendar dtNascimento = Calendar.getInstance();
            dtNascimento.setTime(perfil.getDataNascimento());
            Calendar dtAtual = Calendar.getInstance();
            int idade = dtAtual.get(Calendar.YEAR) - dtNascimento.get(Calendar.YEAR);
            if (dtNascimento.get(Calendar.DAY_OF_YEAR) > dtAtual.get(Calendar.DAY_OF_YEAR)) {
                idade--;
            }
            tvData.setText(idade + " anos");

            tvLocalidade.setText(perfil.getCidade() + "/" + perfil.getEstado());


        }
    }
}
