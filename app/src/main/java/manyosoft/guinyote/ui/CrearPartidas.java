package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

public class CrearPartidas extends AppCompatActivity {

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

    private void creacionPartida()  {
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        try {
            if(nombreSala.getText().toString().isEmpty()){
                Toast.makeText(this, "EL NOMBRE NO PUEDE ESTAR EN BLANCO", Toast.LENGTH_SHORT).show();

            }else{
                Usuario user = Usuario.getInstance();
                Partida nuevaPartida = guinyoteClienteJWT.createAndJoinGame(this, user.getId(), nombreSala.getText().toString(), publica.isChecked());
                if(nuevaPartida == null){
                    CharSequence text = "ERROR AL CREAR PARTIDA, PRUEBA CON OTRO NOMBRE";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(this, text, duration);
                    toast.show();
                }else{//Partida creada correctamente
                    nuevaPartida = guinyoteClienteJWT.getGameById(this,nuevaPartida.getId());
                    Intent i = new Intent(this, JuegoActivity.class);
                    i.putExtra("idPartida",nuevaPartida.getId());
                    i.putExtra("idPlayer",nuevaPartida.getPlayerId());
                    i.putExtra("idPair",nuevaPartida.getPairId());
                    i.putExtra("create",true);
                    i.putExtra("solo",false);
                    startActivity(i);
                }
            }

        }   catch (Exception e)    {
            Log.d("Crear Partida",e.getMessage());
        }

    }
}