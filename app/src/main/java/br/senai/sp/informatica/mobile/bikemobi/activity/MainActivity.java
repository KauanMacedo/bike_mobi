package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.content.Intent;
import android.os.Bundle;
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

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;

    private TextView tvNome;
    private TextView tvData;
    private TextView tvLocalidade;

    private final static int ATUALIZA_PERFIL = 0;

    private final static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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


        getDadosPerfil();

        //inicializarComponentes();
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
        switch (id){
            case R.id.nav_drawer_cadastro:
                intent = new Intent(this, CadastroActivity.class);
                startActivityForResult(intent, ATUALIZA_PERFIL);
                break;
            case  R.id.nav_drawer_historico:
                intent = new Intent(this, HistoricoActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_drawer_avaliacao:

                break;
            case R.id.nav_drawer_rota:

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ATUALIZA_PERFIL:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Perfil atualizado", Toast.LENGTH_SHORT).show();
                    getDadosPerfil();
                }
        }
    }

    public void getDadosPerfil(){
        perfil = dao.getPerfil(1l);
        if (perfil != null){
            tvNome.setText(perfil.getNome());
            tvData.setText("Nascimento: " + fmt.format(perfil.getDataNascimento()));
            tvLocalidade.setText(perfil.getLocalidade());
        }
    }
}
