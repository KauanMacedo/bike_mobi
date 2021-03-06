package br.senai.sp.informatica.mobile.bikemobi.activity.bkp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.activity.CadastroActivity;
import br.senai.sp.informatica.mobile.bikemobi.activity.HistoricoActivity;
import br.senai.sp.informatica.mobile.bikemobi.activity.PerfilActivity;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;


/**
 * Created by rodol on 10/03/2018.
 */

public class MainActivity_Perfil extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;

    private LoginDao loginDao = LoginDao.instance;
    private Login login;

    private TextView tvNome;
    private TextView tvData;
    private TextView tvLocalidade;
    private TextView tvNavHeaderNome;
    private TextView tvNavHeaderEmail;

    private final static int ATUALIZA_PERFIL = 0;

    private final static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        tvNome = findViewById(R.id.textNomePerfil);
        tvData = findViewById(R.id.textDataNascimentoPerfil);
        tvLocalidade = findViewById(R.id.textLocalidadePerfil);
        View header = navigationView.getHeaderView(0);
        tvNavHeaderNome = header.findViewById(R.id.tvNavHeaderNome);
        tvNavHeaderEmail = header.findViewById(R.id.textViewEditarPerfilNavDrawer);

        getDados();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvNavHeaderNome.setText(preferences.getString(this.getResources().getString(R.string.nome_perfil_key), this.getResources().getString(R.string.nome_perfil_default)));
        tvNavHeaderEmail.setText(preferences.getString(this.getResources().getString(R.string.email_perfil_key), this.getResources().getString(R.string.email_perfil_default)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_drawer_perfil:
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_cadastro:
                intent = new Intent(this, CadastroActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_historico:
                intent = new Intent(this, HistoricoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_avaliacao:
                intent = new Intent(this, AvaliacaoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_rota:
                intent = new Intent(this, RotaActivity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ATUALIZA_PERFIL:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Perfil atualizado", Toast.LENGTH_SHORT).show();
                    getDados();
                }
        }
    }

    public void getDados() {
        perfil = dao.getPerfil(loginDao.getIdLogin());
        login = loginDao.getLogin(loginDao.getIdLogin());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

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


            editor.putString(this.getResources().getString(R.string.nome_perfil_key), perfil.getNome());

        }

        if (login != null) {
            editor.putString(this.getResources().getString(R.string.email_perfil_key), login.getEmail());
        }
        editor.apply();
    }


}
