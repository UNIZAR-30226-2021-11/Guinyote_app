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
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

public class UserProfile extends AppCompatActivity {

    private TextView userName;
    private TextView numPartidas;
    private TextView numVictorias;
    private TableRow fila;
    private TableLayout tabla;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userName = findViewById(R.id.textView_user);
        numPartidas = findViewById(R.id.textView_num_partidas);
        numVictorias = findViewById(R.id.textView_numVictorias);
        tabla = findViewById(R.id.User_tableLayout);

        try {
            rellenarPantalla();
        }catch (Exception e){
            Log.d("Get Usuario",e.getMessage());
        }

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { goBack(); }
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
        logout = findViewById(R.id.button_logout);
        logout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_logOut();
                    }
                }
        );

    }

    private void act_history(){
        Intent i = new Intent(this, HistoryActivity.class);
        startActivity(i);
    }

    private void act_logOut(){
        Usuario user = Usuario.getInstance();
        user.logOut();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void goBack(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    private void rellenarPantalla() throws ExecutionException, InterruptedException {
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        Usuario user = Usuario.getInstance();
        userName.setText(user.getUsername());
        ArrayList<Partida> historialPartidas = guinyoteClienteJWT.getPartidasByUser(this,user.getId());
        int iter = historialPartidas.size();
        if(iter >5){iter = 5;}
        for (int i = 0; i<iter;i++){
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
            if(p.getVictoria()) {
                resultado.setText("VICTORIA");
            }else{
                resultado.setText("DERROTA");
            }
            fila.addView(resultado);
            //PUNTOS
            TextView puntos = new TextView(this);
            puntos.setText(p.getPuntos());
            fila.addView(puntos);

            tabla.addView(fila);
        }
    }
}