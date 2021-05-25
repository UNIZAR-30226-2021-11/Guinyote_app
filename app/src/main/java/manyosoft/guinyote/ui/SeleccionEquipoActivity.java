package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

import static java.lang.Long.parseLong;

public class SeleccionEquipoActivity extends AppCompatActivity {
    private long id,idPartida;
    private TextView j1t1, j2t1, j1t2, j2t2,codigoPartida;
    private Button joinT1, joinT2;
    private ArrayList<String> jugadores = null;

    GuinyoteClienteJWT clienteJWT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_equipo);

        Intent intent = getIntent();
        idPartida = intent.getLongExtra("id",-1);

        clienteJWT = GuinyoteClienteJWT.getInstance();
        j1t1 = findViewById(R.id.jugador1Team1);
        j2t1 = findViewById(R.id.jugador2Team1);
        j1t2 = findViewById(R.id.jugador1Team2);
        j2t2 = findViewById(R.id.jugador2Team2);
        joinT1 = findViewById(R.id.buttonTeam1);
        joinT2 = findViewById(R.id.buttonTeam2);
        codigoPartida = findViewById(R.id.codigoPartida);

        codigoPartida.setText(String.valueOf(idPartida));

        actualizarPantalla();

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        for(String a : jugadores)   {
            if(a!=null) Log.d("Depurando", a);
            else Log.d("Depurando", "null");
        }

        Long idPareja1,idPareja2;
        if(jugadores.get(4) == null){
            idPareja1 = (long) -1;
        }else{
            idPareja1 = parseLong(jugadores.get(4));
        }
        if(jugadores.get(5) == null){
            idPareja2 = (long) -1;
        }else{
            idPareja2 = parseLong(jugadores.get(5));
        }


        joinT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Usuario user = Usuario.getInstance();
                actualizarPantalla();
                if(jugadores != null && (jugadores.get(0) == null || jugadores.get(1) == null )){
                    if(!jugadores.contains(user.getUsername())) {
                        Long idJugador = clienteJWT.joinGame(SeleccionEquipoActivity.this, user.getId().longValue(), idPareja1);
                        if (idJugador != -1) {
                            Intent teamSelection = new Intent(SeleccionEquipoActivity.this, JuegoActivity.class);
                            teamSelection.putExtra("idPartida",idPartida);
                            teamSelection.putExtra("idPlayer",idJugador);
                            teamSelection.putExtra("idPair",idPareja1);
                            teamSelection.putExtra("solo",false);
                            startActivity(teamSelection);
                        } else {
                            CharSequence text = "ERROR AL UNIRSE AL EQUIPO";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                            toast.show();
                        }
                    }else{
                        CharSequence text = "YA PERTENECE A UN EQUIPO";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                        toast.show();
                    }

                }else{
                    CharSequence text = "EQUIPO LLENO";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                    toast.show();
                }

            }
        });


        joinT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actualizarPantalla();
                Usuario user = Usuario.getInstance();
                if(jugadores != null && (jugadores.get(2) == null || jugadores.get(3) == null )){
                    if(!jugadores.contains(user.getUsername())) {
                        Long idJugador = clienteJWT.joinGame(SeleccionEquipoActivity.this, user.getId().longValue(), idPareja2);
                        if (idJugador != -1) {
                            Intent teamSelection = new Intent(SeleccionEquipoActivity.this, JuegoActivity.class);
                            teamSelection.putExtra("idPartida",idPartida);
                            teamSelection.putExtra("idPlayer",idJugador);
                            teamSelection.putExtra("idPair",idPareja2);
                            teamSelection.putExtra("solo",false);
                            startActivity(teamSelection);
                        } else {
                            CharSequence text = "ERROR AL UNIRSE AL EQUIPO";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                            toast.show();
                            actualizarPantalla();
                        }
                    }else{
                        CharSequence text = "YA PERTENECE A UN EQUIPO";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                        toast.show();
                    }
                }else{
                    CharSequence text = "EQUIPO LLENO";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(SeleccionEquipoActivity.this, text, duration);
                    toast.show();
                }
            }
        });
    }

    void actualizarPantalla(){
        try {
            jugadores = clienteJWT.getJugadores(this, idPartida);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if(jugadores != null)   {
            if(jugadores.get(0) != null) j1t1.setText(jugadores.get(0));
            else                         j1t1.setText("-----");

            if(jugadores.get(1) != null) j2t1.setText(jugadores.get(1));
            else                         j2t1.setText("-----");

            if(jugadores.get(2) != null) j1t2.setText(jugadores.get(2));
            else                         j1t2.setText("-----");

            if(jugadores.get(3) != null) j2t2.setText(jugadores.get(3));
            else                         j2t2.setText("-----");

            if(jugadores.get(0) != null && jugadores.get(1) != null)    {
                joinT1.setText(R.string.Full);
            } else {
                joinT1.setText(R.string.Join);
            }

            if(jugadores.get(2) != null && jugadores.get(3) != null)    {
                joinT2.setText(R.string.Full);
            } else {
                joinT2.setText(R.string.Join);
            }
        }
        else {
            j1t1.setText(R.string.Error);
            j2t1.setText(R.string.Error);
            j1t2.setText(R.string.Error);
            j2t2.setText(R.string.Error);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        actualizarPantalla();
    }
}