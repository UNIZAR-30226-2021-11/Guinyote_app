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

public class HistoryActivity extends AppCompatActivity {

    private TableRow fila;
    private TableLayout tabla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        tabla = findViewById(R.id.history_tableLayout);
        try {
            String userName = getIntent().getExtras().getString("userName");
            rellenarPantalla(userName);
        }catch (Exception e){
            Log.d("Get Usuario",e.getMessage());
        }

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    private void rellenarPantalla(String name) throws ExecutionException, InterruptedException {
        GuinyoteClienteJWT guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();
        Usuario user = Usuario.getInstance();
        if(user.getUsername() != name) {
            user = guinyoteClienteJWT.getUsuario(this, name);
        }

        ArrayList<Partida> historialPartidas = guinyoteClienteJWT.getPartidasByUser(this,user.getId());
        int iter = historialPartidas.size();
        for (int i = 0; i<iter;i++){
            Partida p = historialPartidas.get(i);
            fila = new TableRow(this);
            fila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
            //DURACION
            TextView duracion = new TextView(this);
            duracion.setText(p.getEnd());
            duracion.setGravity(Gravity.CENTER);
            fila.addView(duracion);
            //EQUIPOS
            TextView equipos = new TextView(this);
            equipos.setText(p.getNombre());
            equipos.setGravity(Gravity.CENTER);
            fila.addView(equipos);
            //RESULTADO
            TextView resultado = new TextView(this);
            if(p.getVictoria()) {
                resultado.setText("VICTORIA");
            }else{
                resultado.setText("DERROTA");
            }
            resultado.setGravity(Gravity.CENTER);
            fila.addView(resultado);
            //PUNTOS
            TextView puntos = new TextView(this);
            puntos.setText(p.getPuntos().toString());
            puntos.setGravity(Gravity.CENTER);
            fila.addView(puntos);

            tabla.addView(fila);
        }
    }
}
