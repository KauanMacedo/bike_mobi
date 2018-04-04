package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.util.SaveSharedPreference;

public class LoginActivity extends AppCompatActivity {

    private EditText emailUser;
    private EditText senha;
    private Button btLogin;
    private TextView linkCad;

    private static int REQUEST_CADASTRO = 0;

    private LoginDao loginDao = LoginDao.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailUser = findViewById(R.id.emailUser);
        senha = findViewById(R.id.senha);
        linkCad = findViewById(R.id.linkCadastro);

        btLogin = findViewById(R.id.btLogin);

        try {
            byte[] data = Base64.decode("D201AFD3E79BF74CBDCAC73E88B9B969", Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            Log.d("BikeLog", text);
        } catch (Exception e){}

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(LoginActivity.this, "Login", Toast.LENGTH_SHORT).show();
                login();
            }
        });

        linkCad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CadastroActivity.class);
                startActivityForResult(intent, REQUEST_CADASTRO);
            }
        });
    }
    public void login(){

        if (!validate()) {
            onLoginFailed();
            return;
        }

        btLogin.setEnabled(false);


        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        // TODO: Implement your own authentication logic here.
        Login login = new Login();
        login.setEmail(emailUser.getText().toString());
        login.setSenha(senha.getText().toString());

        final boolean autenticado = loginDao.login(login);
        //Log.d("BikeLog", "acessToken: " + loginDao.getToken());


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // onLoginSuccess();
                        // onLoginFailed();
                        if (autenticado) {
                            onLoginSuccess();
                        } else {
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);

    }

    public void onLoginSuccess() {
        btLogin.setEnabled(true);

        String substr = loginDao.getToken().substring(loginDao.getToken().indexOf(".") + 1);
        String substr2 = substr.substring(0, substr.indexOf("."));
        //Log.d("BikeLog", "substr2: "+substr2);
        try {
            byte[] data = Base64.decode(substr2, Base64.DEFAULT);
            String text = new String(data, "UTF-8");
            Log.d("BikeLog", "acessTokenUser: " + loginDao.getToken());
            //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            JSONObject obj = new JSONObject(text);
            loginDao.setIdLogin(Integer.parseInt(obj.getJSONArray("unique_name").get(0).toString()));

            SaveSharedPreference.setUser(getApplicationContext()
                    , obj.getString("Nome")
                    , obj.getString("email")
                    , obj.getJSONArray("unique_name").get(0).toString()
                    , substr);
            SaveSharedPreference.setSenha(getApplicationContext(), senha.getText().toString());

            //Log.d("BikeLog", obj.getJSONArray("unique_name").get(0) + "");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Falha ao descriptografar o Token.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MainEmptyActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Não foi possível entrar.", Toast.LENGTH_LONG).show();

        btLogin.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CADASTRO){
            if (resultCode == RESULT_OK){
                //login();
                Toast.makeText(this, "Cadastro realizado. Faça seu logon e bem vindo ao BikeMobi!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailUser.getText().toString();
        String password = senha.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailUser.setError("digite um endereço de e-mail válido");
            valid = false;
        } else {
            emailUser.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            senha.setError("entre 4 e 10 caracteres alfanuméricos");
            valid = false;
        } else {
            senha.setError(null);
        }

        return valid;
    }
}
