package manyosoft.guinyote.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText codigoPartida;
    private Button unirsePorCodigo;
    private GuinyoteClienteJWT clienteJWT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_listado_partidas);

        // Botón de retroceso
        FloatingActionButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> onBackPressed());

        clienteJWT = GuinyoteClienteJWT.getInstance();

        listadoPartidas = findViewById(R.id.listadoPartidas);

        codigoPartida = findViewById(R.id.codigoPartida);
        unirsePorCodigo = findViewById(R.id.boton_buscar);

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

        listadoPartidas.setOnItemClickListener((adapterView, view, position, id) -> {
            Toast.makeText(ListadoPartidasActivity.this, "Seleccionaste la partida: " + partidas.get(position).getNombre() + ", id:"+id, Toast.LENGTH_SHORT).show();
            Intent teamSelection = new Intent(ListadoPartidasActivity.this, SeleccionEquipoActivity.class);
            teamSelection.putExtra("id",id);
            startActivity(teamSelection);
        });

        unirsePorCodigo.setOnClickListener(view -> {
            Partida recuperada;
            Long idPartida;
            try {
                idPartida = Long.valueOf(codigoPartida.getText().toString());
                recuperada = clienteJWT.getGameById(ListadoPartidasActivity.this, idPartida);
            } catch (InterruptedException | NumberFormatException | ExecutionException e) {
                e.printStackTrace();
                recuperada = null;
                idPartida = null;
            }

            // TODO Buscar también por nombre (no hay método en la API Rest de momento)

            if(recuperada != null)  {
                Intent teamSelection = new Intent(ListadoPartidasActivity.this, SeleccionEquipoActivity.class);
                teamSelection.putExtra("id",idPartida);
                startActivity(teamSelection);
            } else {
                Toast.makeText(ListadoPartidasActivity.this, "No se ha podido encontrar la partida indicada", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
}