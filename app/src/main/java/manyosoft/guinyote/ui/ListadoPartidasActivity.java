package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import manyosoft.guinyote.util.GuinyoteClienteJWT;
import manyosoft.guinyote.util.ListadoPartidasAdapter;
import manyosoft.guinyote.util.Partida;
import manyosoft.guinyote.R;

public class ListadoPartidasActivity extends AppCompatActivity {

    private ListView listadoPartidas;
    private ArrayList<Partida> partidas;
    private GuinyoteClienteJWT clienteJWT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listado_partidas);

        // Botón de retroceso
        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        clienteJWT = GuinyoteClienteJWT.getInstance();

        listadoPartidas = (ListView) findViewById(R.id.listadoPartidas);

        // SOLICITA PARTIDAS
        try {
            partidas = clienteJWT.getPartidasPublicas(this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            partidas = new ArrayList<>();
        }

        // Adaptador para la lista de partidas
        ListadoPartidasAdapter adaptador = new ListadoPartidasAdapter(this, R.layout.list_item_partidas, partidas);
        listadoPartidas.setAdapter(adaptador);
        listadoPartidas.setClickable(true);

        listadoPartidas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(ListadoPartidasActivity.this, "Seleccionaste la partida: " + partidas.get(position).getNombre(), Toast.LENGTH_SHORT).show();
                // TODO Lanza la actividad de seleccion de equipo con la partida seleccionada en la lista
            }
        });
    }

    
}