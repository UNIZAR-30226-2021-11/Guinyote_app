package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

public class UserProfile extends AppCompatActivity {

    private static final int ACTIVITY_HISTORY = 0;

    private TextView userName;
    private TextView numPartidas;
    private TextView numVictorias;
    private TableRow fila;
    private TableLayout tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.textView_user);
        numPartidas = findViewById(R.id.textView_num_partidas);
        numVictorias = findViewById(R.id.textView_numVictorias);
        tabla = findViewById(R.id.User_tableLayout);

        GuinyoteClienteJWT guinyoteClienteJWT = new GuinyoteClienteJWT();
        try {
            Usuario user = guinyoteClienteJWT.getUsuario(this,getIntent().getExtras().getString("userName"));
        }catch (Exception e){
            Log.d("Get Usuario",e.getMessage());
        }
        try {
            //TODO: hacer el bucle bien
            ArrayList<Partida> historialPartidas = guinyoteClienteJWT.getPartidasPublicas(this);
            for (int i = 0; i<4;i++){
                Partida p = historialPartidas.get(i);
                fila = new TableRow(this);
                //DURACION
                TextView duracion = new TextView(this);
                duracion.setText("PONER DURACION");
                //EQUIPOS
                TextView equipos = new TextView(this);
                equipos.setText(p.getJugadores());
                fila.addView(equipos);
                //RESULTADO
                TextView resultado = new TextView(this);
                resultado.setText("PONER EL RESULTADO");
                fila.addView(resultado);
                //PUNTOS
                TextView puntos = new TextView(this);
                puntos.setText("PONER LOS PUNTOS");
                fila.addView(puntos);

                tabla.addView(fila);
            }
        }catch (Exception e){
            Log.d("Get Partidas",e.getMessage());
        }



        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Button history = (Button) findViewById(R.id.button_historial);
        history.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_history();
                    }
                }
        );

    }

    private void act_history(){
        Intent i = new Intent(this, HistoryActivity.class);
        startActivityForResult(i,ACTIVITY_HISTORY);
    }
}