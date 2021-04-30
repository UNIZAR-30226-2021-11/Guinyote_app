package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.R;
import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.util.Usuario;

public class UserProfile extends AppCompatActivity {

    private TextView userName,numPartidas,numVictorias;
    private TableRow fila;
    private TableLayout tabla;
    private Button buscar,borrar,history;
    private EditText buscarUser;
    private NavigationView desplegable;
    private ConstraintLayout pantalla;

    private FloatingActionButton fab;

    private boolean busquedaUsuario = false;
    private  GuinyoteClienteJWT guinyoteClienteJWT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        guinyoteClienteJWT = GuinyoteClienteJWT.getInstance();

        userName = findViewById(R.id.textView_user);
        numPartidas = findViewById(R.id.textView_num_partidas);
        numVictorias = findViewById(R.id.textView_numVictorias);
        tabla = findViewById(R.id.User_tableLayout);
        buscar = findViewById(R.id.button_buscar);
        borrar = findViewById(R.id.button_borrar);
        buscarUser = findViewById(R.id.editTextTextPersonName);
        fab = findViewById(R.id.fab);
        desplegable = findViewById(R.id.desplegable);
        pantalla = findViewById(R.id.pantalla);

        try {
            Usuario user = Usuario.getInstance();
            rellenarPantalla(user.getUsername());
        }catch (Exception e){
            Log.d("Get Usuario",e.getMessage());
        }

        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { goBack(); }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desplegable.setVisibility(View.VISIBLE);
            }
        });

        history =  findViewById(R.id.button_historial);
        history.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_history();
                    }
                }
        );

        buscar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act_buscar();
                    }
                }
        );

        pantalla.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        desplegable.setVisibility(View.INVISIBLE);
                    }
                }
        );

    }


    private void act_buscar() {
        if(buscarUser.getText().toString().isEmpty()){
            CharSequence text = "Escriba un nombre para buscar";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        else{
            Usuario user = null;
            try{
                user = guinyoteClienteJWT.getUsuario(this, buscarUser.getText().toString());
            }catch (Exception e){
                Log.d("Get Usuario",e.getMessage());
            }
            if(user == null){
                CharSequence text = "Usuario buscado incorrecto";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(this, text, duration);
                toast.show();
            }else{
                busquedaUsuario = true;
                buscar.setVisibility(View.INVISIBLE);
                desplegable.setVisibility(View.INVISIBLE);
                buscarUser.setVisibility(View.INVISIBLE);
                fab.setVisibility(View.INVISIBLE);
                try {
                    rellenarPantalla(buscarUser.getText().toString());
                } catch (Exception e){
                    Log.d("Get Usuario",e.getMessage());
                }
            }

        }
    }

    private void act_history(){
        if(busquedaUsuario){
            Intent i = new Intent(this, HistoryActivity.class);
            i.putExtra("userName",buscarUser.getText());
            startActivity(i);
        }else{
            Usuario user = Usuario.getInstance();
            Intent i = new Intent(this, HistoryActivity.class);
            i.putExtra("userName",user.getUsername());
            startActivity(i);
        }
    }

    private void goBack(){
        if(busquedaUsuario){
            busquedaUsuario = false;
            buscar.setVisibility(View.VISIBLE);
            buscarUser.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            try {
                Usuario user = Usuario.getInstance();
                rellenarPantalla(user.getUsername());
            }catch (Exception e){
                Log.d("Get Usuario",e.getMessage());
            }
        }
        else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void rellenarPantalla(String name) throws ExecutionException, InterruptedException {

        Usuario user = Usuario.getInstance();
        if(user.getUsername() != name) {
            user = guinyoteClienteJWT.getUsuario(this, name);
        }
        userName.setText(user.getUsername());
        Integer totalPartidas = user.getVictorias()+user.getDerrotas();
        numPartidas.setText(totalPartidas.toString());
        numVictorias.setText(user.getVictorias().toString());

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