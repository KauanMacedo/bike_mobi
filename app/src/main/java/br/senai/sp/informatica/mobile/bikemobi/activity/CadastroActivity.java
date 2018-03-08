package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.fragment.DateDialog;

public class CadastroActivity extends AppCompatActivity implements View.OnClickListener {

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;

    private EditText etNomeUsuario;
    private EditText etEmail;
    private EditText etSenha;
    private EditText etNomeCompleto;
    private EditText etDataNascimento;
    private EditText etBiografia;
    private EditText etCidade;
    private EditText etEstado;

    private Calendar calendar;

    private final static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        etNomeUsuario = findViewById(R.id.editNomeUsuario);
        etEmail = findViewById(R.id.editEmail);
        etNomeCompleto = findViewById(R.id.editNome);
        etSenha = findViewById(R.id.editSenha);
        etDataNascimento = findViewById(R.id.editDataNascimento);
        etBiografia = findViewById(R.id.editBiografia);
        etCidade = findViewById(R.id.editCidade);
        etEstado = findViewById(R.id.editEstado);

        calendar = Calendar.getInstance();
        etDataNascimento.setOnClickListener(this);
        etDataNascimento.setFocusable(false);
        etDataNascimento.setKeyListener(null);


        perfil = dao.getPerfil(1l);
        if (perfil != null) {
            etNomeCompleto.setText(perfil.getNome());
            etNomeUsuario.setText(perfil.getLogin());
            etBiografia.setText(perfil.getBio());
            etDataNascimento.setText(fmt.format(perfil.getDataNascimento()));
            calendar.setTime(perfil.getDataNascimento());
            etCidade.setText(perfil.getCidade());
            etEstado.setText(perfil.getEstado());
        }

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cadastro_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mi_salvar:
                perfil.setLogin(etNomeUsuario.getText().toString());
                perfil.setBio(etBiografia.getText().toString());
                perfil.setNome(etNomeCompleto.getText().toString());
                Date dataNascimento = new Date(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                perfil.setDataNascimento(dataNascimento);
                perfil.setCidade(etCidade.getText().toString());
                perfil.setEstado(etEstado.getText().toString());

                dao.setPerfil(perfil);
                setResult(RESULT_OK);
                break;
        }
        finish();
        return true;
    }

    @Override
    public void onClick(View v) {

        DateDialog dialog = new DateDialog();
        try {
            calendar.setTime(fmt.parse(etDataNascimento.getText().toString()));
            dialog.setCalendar(calendar);
        } catch (ParseException e) {
            Log.d("Erro parse data: ", e.getMessage());
        }
        dialog.setEditText(etDataNascimento);
        dialog.setView(v);
        dialog.show(getFragmentManager(), "Data de Lançamento");
        try {
            Log.d("AlbumTeste:", etDataNascimento.getText().toString());
            calendar.setTime(fmt.parse(etDataNascimento.getText().toString()));
        } catch (ParseException e) {
        }

    }
}
