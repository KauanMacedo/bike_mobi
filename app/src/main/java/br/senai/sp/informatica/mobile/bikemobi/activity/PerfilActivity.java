package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.util.SaveSharedPreference;

public class PerfilActivity extends AppCompatActivity{

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;
    private LoginDao loginDao = LoginDao.instance;
    private Login login;

    private TextView tvNome;
    private TextView tvData;
    private TextView tvLocalidade;

    private ImageView ivPerfil;
    private ImageView ivAvatar;

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
        ivAvatar = findViewById(R.id.imageViewPerfil);


        getDadosPerfil();

        //inicializarComponentes();
        FloatingActionButton fabEditarPerfil = findViewById(R.id.fabEditarPerfil);
        fabEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivityForResult(intent, ATUALIZA_PERFIL);
            }
        });
    }

    public void getDadosPerfil() {
        /*perfil = dao.getPerfil(Integer.parseInt(SaveSharedPreference.getId(getApplicationContext())));

        if (perfil != null) {
        */
        login = loginDao.getLogin(Integer.parseInt(SaveSharedPreference.getId(getApplicationContext())));

        if(login != null){
            perfil = login.getPerfil();

            tvNome.setText(perfil.getNome());

            Calendar dtNascimento = Calendar.getInstance();
            dtNascimento.setTime(perfil.getDataNascimento());
            Calendar dtAtual = Calendar.getInstance();

            int idade = dtAtual.get(Calendar.YEAR) - dtNascimento.get(Calendar.YEAR);

            if (dtNascimento.get(Calendar.DAY_OF_YEAR) > dtAtual.get(Calendar.DAY_OF_YEAR)) {
                idade--;
            }
            tvData.setText(idade + " anos");

            tvLocalidade.setText(perfil.getCidade() + ", " + perfil.getEstado());

            if (!SaveSharedPreference.getAvatar(getApplicationContext()).isEmpty()) {
                ivAvatar.setImageResource(getResources().getIdentifier(SaveSharedPreference.getAvatar(getApplicationContext())
                        , "drawable"
                        , getPackageName()));
            }

        } else {
            Toast.makeText(this, "Não foi possível acessar informações de Perfil. Verifique sua internet e tente novamente mais tarde.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ATUALIZA_PERFIL){
            getDadosPerfil();
            Toast.makeText(getApplicationContext(), "Perfil atualizado.", Toast.LENGTH_LONG).show();
        }
    }
}
