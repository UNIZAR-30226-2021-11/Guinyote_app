package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

public class CrearPartidas extends AppCompatActivity {

    private static final int ACTIVITY_SELECT_TEAM = 0;

    private EditText nombreSala;
    private CheckBox publica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_partidas);

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        nombreSala = findViewById(R.id.editText_crearPartida_nombreSala);
        publica = findViewById(R.id.checkBox_crearPartida);

        Button crearPartida = findViewById(R.id.button_crearPartida);
        crearPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creacionPartida();
            }
        }
        );
    }

    private void creacionPartida(){
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        try {
            Usuario user = Usuario.getInstance();
            guinyoteClienteJWT.createAndJoinGame(this, user.getId(), nombreSala.getText().toString(), publica.isChecked());
            Intent i = new Intent(this, SeleccionEquipoActivity.class);
            startActivityForResult(i, ACTIVITY_SELECT_TEAM);
        }catch (Exception e){
            Log.d("Crear Partida",e.getMessage());
        }

    }
}