package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
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
import br.senai.sp.informatica.mobile.bikemobi.fragment.DateDialog;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.util.SaveSharedPreference;

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
    private String urlAvatar;
    private EditText etCidade;
    private EditText etEstado;

    private Spinner spEstado;
    private Spinner spCidade;

    private Calendar calendar;

    private ImageView avatar1, avatar2, avatar3, avatar4, avatar5, avatar6;
    private ImageView ivAvatar;


    private final static DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_old);

        etNomeUsuario = findViewById(R.id.editNomeUsuario);
        etEmail = findViewById(R.id.editEmail);
        etNomeCompleto = findViewById(R.id.editNome);
        etSenha = findViewById(R.id.editSenha);
        etDataNascimento = findViewById(R.id.editDataNascimento);
        etBiografia = findViewById(R.id.editBiografia);
        spEstado = findViewById(R.id.spEstado);
        spCidade = findViewById(R.id.spCidade);

        avatar1 = findViewById(R.id.avatar_1);
        avatar2 = findViewById(R.id.avatar_2);
        avatar3 = findViewById(R.id.avatar_3);
        avatar4 = findViewById(R.id.avatar_4);
        avatar5 = findViewById(R.id.avatar_5);
        avatar6 = findViewById(R.id.avatar_6);

        calendar = Calendar.getInstance();
        etDataNascimento.setOnClickListener(this);
        etDataNascimento.setFocusable(false);
        etDataNascimento.setKeyListener(null);

        IbgeDao ibgeDao = new IbgeDao();

        List<String> siglasEstados = ibgeDao.listarSiglasEstados(ibgeDao.listarEstados());

        Log.d("BikeLog", "token: " + loginDao.getToken());

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

        //Log.d("BikeLog", loginDao.getIdLogin() + "");
        if (!SaveSharedPreference.getId(getApplication()).isEmpty()) {
            //perfil = perfilDao.getPerfil(Integer.parseInt(SaveSharedPreference.getId(getApplicationContext())));
            login = loginDao.getLogin(Integer.parseInt(SaveSharedPreference.getId(getApplicationContext())));
            perfil = login.getPerfil();
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
            //login = loginDao.getLogin(Integer.parseInt(SaveSharedPreference.getId(getApplicationContext())));
            if (login != null) {
                etEmail.setText(login.getEmail());
                etNomeUsuario.setText(login.getNomeUsuario());
                etSenha.setText(login.getSenha());
            }
        } else {
            perfil = new Perfil();
            login = new Login();

            spEstado.setSelection(siglasEstados.indexOf("SP"));

            List<String> nomesCidades = ibgeDao.listarNomesCidades(ibgeDao.listarCidades(35));

            arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bikemobi, nomesCidades);
            spinnerArrayAdapter = arrayAdapter;
            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_bikemobi);
            spCidade.setAdapter(spinnerArrayAdapter);


            //etCidade.setText(perfil.getCidade());
            spCidade.setSelection(9);
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
        int resposta = 0;
        switch (id) {
            case R.id.mi_salvar:
                if (validar()) {
                    perfil.setLogin(etNomeUsuario.getText().toString());
                    perfil.setAvatarUrl(urlAvatar);
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
                    login.setSenha(etSenha.getText().toString());

                    login.setPerfil(perfil);
                    resposta = loginDao.alterar(login);
                    //perfilDao.setPerfil(perfil);
                    setResult(RESULT_OK);
                } else {
                    return false;
                }
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

    public void updSpinnerCidade(int idEstado) {

        IbgeDao ibgeDao = new IbgeDao();
        List<Integer> idsEstado = ibgeDao.listarIdsEstados(ibgeDao.listarEstados());
        List<String> nomesCidades = ibgeDao.listarNomesCidades(ibgeDao.listarCidades(idsEstado.get(idEstado)));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item_bikemobi, nomesCidades);
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item_bikemobi);
        spCidade.setAdapter(spinnerArrayAdapter);
        if (perfil != null) {
            spCidade.setSelection(nomesCidades.indexOf(perfil.getCidade()));
        } else {
            if (nomesCidades.indexOf("São Paulo") > 0) {
                spCidade.setSelection(nomesCidades.indexOf("São Paulo"));
            }
        }
    }

    public void avatarOnClick(View view) {

        if (avatar1.isPressed()) {
            avatar1.setImageResource(R.drawable.man_1_check);
            urlAvatar = "https://image.ibb.co/hdwL9x/man_1.png";
        } else if (avatar2.isPressed()) {
            avatar2.setImageResource(R.drawable.man_2_check);
            urlAvatar = "https://image.ibb.co/kschNH/man_2.png";
        } else if (avatar3.isPressed()) {
            avatar3.setImageResource(R.drawable.man_3_check);
            urlAvatar = "https://image.ibb.co/dOiNNH/man_3.png";
        } else if (avatar4.isPressed()) {
            avatar4.setImageResource(R.drawable.woman_1_check);
            urlAvatar = "https://image.ibb.co/cx4U2H/woman_1.png";
        } else if (avatar5.isPressed()) {
            avatar5.setImageResource(R.drawable.woman_3_check);
            urlAvatar = "https://image.ibb.co/kUewhH/woman_3.png";
        } else {
            avatar6.setImageResource(R.drawable.woman_2_check);
            urlAvatar = "https://image.ibb.co/hj7hNH/woman_2.png";
        }
    }

    public boolean validar() {
        boolean validado = true;
        if (!isValidEmail(etEmail.getText().toString())) {
            etEmail.setError("Campo inválido.");
            validado = false;
        }

        if (TextUtils.isEmpty(etNomeCompleto.getText().toString())) {
            etNomeCompleto.setError("Este campo é obrigatório");
            validado = false;
        }

        if (etNomeCompleto.getText().length() <= 4) {
            etNomeCompleto.setError("Este campo deve ter no mínimo de 5 caracteres.");
            validado = false;
        }

        if (TextUtils.isEmpty(etDataNascimento.getText().toString())) {
            etDataNascimento.setError("Este campo é obrigatório");
            validado = false;
        }

        if (spCidade.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Escolha uma cidade.", Toast.LENGTH_SHORT).show();
            validado = false;
        }

        if (TextUtils.isEmpty(etBiografia.getText().toString())) {
            etBiografia.setError("Este campo é obrigatório");
            validado = false;
        }

        if (TextUtils.isEmpty(etNomeUsuario.getText().toString())) {
            etNomeUsuario.setError("Este campo é obrigatório");
            validado = false;
        }

        if (etNomeUsuario.getText().length() <= 4) {
            etNomeUsuario.setError("Este campo deve ter no mínimo de 5 caracteres.");
            validado = false;
        }

        if (TextUtils.isEmpty(etEmail.getText().toString())) {
            etEmail.setError("Este campo é obrigatório");
            validado = false;
        }

        if (TextUtils.isEmpty(etSenha.getText().toString())) {
            etSenha.setError("Este campo é obrigatório");
            validado = false;
        }

        if (etSenha.getText().length() <= 5) {
            etSenha.setError("Este campo deve ter no mínimo de 6 caracteres.");
            validado = false;
        }
        return validado;
    }
}
