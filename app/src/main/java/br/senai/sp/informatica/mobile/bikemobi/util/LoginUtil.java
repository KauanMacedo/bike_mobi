package br.senai.sp.informatica.mobile.bikemobi.util;

import android.app.ProgressDialog;
import android.content.Context;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.activity.LoginActivity;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;

/**
 * Created by rodol on 31/03/2018.
 */

public class LoginUtil {


    public static boolean login(Context cont, String email, String senha){
        LoginDao loginDao = LoginDao.instance;
        final ProgressDialog progressDialog = new ProgressDialog(cont,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando...");
        progressDialog.show();

        Login login = new Login();
        login.setEmail(email);
        login.setSenha(senha);

        final boolean autenticado = loginDao.login(login);
        //Log.d("BikeLog", "acessToken: " + loginDao.getToken());


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        // onLoginSuccess();
                        // onLoginFailed();

                        progressDialog.dismiss();
                    }
                }, 3000);
        return autenticado;
    }
}
