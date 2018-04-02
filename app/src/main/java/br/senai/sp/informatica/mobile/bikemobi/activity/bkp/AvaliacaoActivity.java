package br.senai.sp.informatica.mobile.bikemobi.activity.bkp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.senai.sp.informatica.mobile.bikemobi.R;

public class AvaliacaoActivity extends AppCompatActivity {

    public RadioGroup rgTrajeto;
    public RadioButton rbTrajeto;
    public RadioGroup rgSeguranca;
    public RadioButton rbSeguranca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);

        rgTrajeto = findViewById(R.id.rgTrajeto);
        rgSeguranca = findViewById(R.id.rgSeguranca);
    }


    public void onClickAvalEnviar(View view) {
        int idTraj = rgTrajeto.getCheckedRadioButtonId();
        int idSeg = rgSeguranca.getCheckedRadioButtonId();

        rbTrajeto = findViewById(idTraj);
        rbSeguranca = findViewById(idSeg);

        if (rbTrajeto != null){
            Toast.makeText(this, rbTrajeto.getId() + "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Selecione uma opção para Trajeto", Toast.LENGTH_SHORT).show();
        }

        if (rbSeguranca != null){
            Toast.makeText(this, rbSeguranca.getId() + "", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Selecione uma opção para Segurança", Toast.LENGTH_SHORT).show();
        }

    }
}
