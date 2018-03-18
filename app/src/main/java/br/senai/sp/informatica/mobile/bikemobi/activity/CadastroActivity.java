package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.IbgeDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Cidade;
import br.senai.sp.informatica.mobile.bikemobi.model.Estado;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.fragment.DateDialog;

public class CadastroActivity extends AppCompatActivity implements View.OnClickListener {

    private PerfilDao perfilDao = PerfilDao.instance;
    private Perfil perfil;

    private LoginDao loginDao = LoginDao.instance;
    private Login login;

    private EditText etNomeUsuario;
    private EditText etEmail;
    private EditText etSenha;
    private EditText etNomeCompleto;
    private EditText etDataNascimento;
    private EditText etBiografia;
    private EditText etCidade;
    private EditText etEstado;

    private Spinner spEstado;
    private Spinner spCidade;

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
        spEstado = findViewById(R.id.spEstado);
        spCidade = findViewById(R.id.spCidade);

        calendar = Calendar.getInstance();
        etDataNascimento.setOnClickListener(this);
        etDataNascimento.setFocusable(false);
        etDataNascimento.setKeyListener(null);

        IbgeDao ibgeDao = new IbgeDao();

        List<String> siglasEstados = ibgeDao.listarSiglasEstados(ibgeDao.listarEstados());


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bikemobi, siglasEstados);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_bikemobi);
        spEstado.setAdapter(spinnerArrayAdapter);

        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                updSpinnerCidade(position - 1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        perfil = perfilDao.getPerfil(1l);
        if (perfil != null) {
            etNomeCompleto.setText(perfil.getNome());
            etBiografia.setText(perfil.getBio());
            etDataNascimento.setText(fmt.format(perfil.getDataNascimento()));
            calendar.setTime(perfil.getDataNascimento());
            //etEstado.setText(perfil.getEstado());
            spEstado.setSelection(siglasEstados.indexOf(perfil.getEstado()));


            int idEstado = ibgeDao.getIdEstado(perfil.getEstado());

            List<String> nomesCidades = ibgeDao.listarNomesCidades(ibgeDao.listarCidades(idEstado));

            arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bikemobi, nomesCidades);
            spinnerArrayAdapter = arrayAdapter;
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_bikemobi);
            spCidade.setAdapter(spinnerArrayAdapter);


            //etCidade.setText(perfil.getCidade());
            spCidade.setSelection(nomesCidades.indexOf(perfil.getCidade()));

        }


        login = loginDao.getLogin(1l);
        if (login != null){
            etEmail.setText(login.getEmail());
            etNomeUsuario.setText(login.getNomeUsuario());
            etSenha.setText(login.getSenha());
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
                if (!isValidEmail(etEmail.getText().toString())){
                    Toast.makeText(this, "E-mail inválido.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(etNomeCompleto.getText().toString())){
                    Toast.makeText(this, "Preencha o " + this.getResources().getString(R.string.nome) + ".", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(etDataNascimento.getText().toString())){
                    Toast.makeText(this, "Preencha a " + this.getResources().getString(R.string.data_de_nascimento) + ".", Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (TextUtils.isEmpty(etBiografia.getText().toString())){
                    Toast.makeText(this, "Preencha a " + this.getResources().getString(R.string.biografia) + ".", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(etNomeUsuario.getText().toString())){
                    Toast.makeText(this, "Preencha o " + this.getResources().getString(R.string.nome_do_usuario) + ".", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (TextUtils.isEmpty(etEmail.getText().toString())){
                    Toast.makeText(this, "Preencha o " + this.getResources().getString(R.string.e_mail) + ".", Toast.LENGTH_SHORT).show();
                    return false;
                }
                perfil.setLogin(etNomeUsuario.getText().toString());
                perfil.setBio(etBiografia.getText().toString());
                perfil.setNome(etNomeCompleto.getText().toString());
                Date dataNascimento = new Date(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                perfil.setDataNascimento(dataNascimento);
                //perfil.setCidade(etCidade.getText().toString());
                perfil.setCidade(spCidade.getSelectedItem().toString());
                //perfil.setEstado(etEstado.getText().toString());
                perfil.setEstado(spEstado.getSelectedItem().toString());

                login.setEmail(etEmail.getText().toString());
                login.setNomeUsuario(etNomeUsuario.getText().toString());


                loginDao.setLogin(login);
                perfilDao.setPerfil(perfil);
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
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void updSpinnerCidade(int idEstado){

        IbgeDao ibgeDao = new IbgeDao();
        List<Integer> idsEstado = ibgeDao.listarIdsEstados(ibgeDao.listarEstados());
        List<String> nomesCidades = ibgeDao.listarNomesCidades(ibgeDao.listarCidades(idsEstado.get(idEstado)));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bikemobi, nomesCidades);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_bikemobi);
        spCidade.setAdapter(spinnerArrayAdapter);
        if (perfil != null) {
            spCidade.setSelection(nomesCidades.indexOf(perfil.getCidade()));
        }
    }
}
